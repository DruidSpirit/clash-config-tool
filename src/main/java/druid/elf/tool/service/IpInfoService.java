package druid.elf.tool.service;


import druid.elf.tool.entity.IpInfo;
import druid.elf.tool.model.PageParam;
import druid.elf.tool.repository.IpInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IpInfoService {

    @Autowired
    private IpInfoRepository ipInfoRepository;

    // 分页查询
    public Page<IpInfo> findAll(PageParam pageParam) {
        // 创建一个排序规则，首先按 sortOrder 升序排序，再按 addTime 降序排序，如果这两个字段相等，再按 id 升序排序
        Sort sort = Sort.by(
                Sort.Order.asc("sortOrder"),  // 按 sortOrder 升序
                Sort.Order.desc("addTime"),   // 按 addTime 降序
                Sort.Order.asc("id")         // 按 id 升序，确保排序的唯一性
        );

        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pageParam.getPageNum() - 1, pageParam.getPageSize(), sort);

        // 调用 JPA Repository 的 findAll 方法，传入分页请求
        return ipInfoRepository.findAll(pageRequest);
    }

    // 保存或修改 IP 信息
    public IpInfo save(IpInfo ipInfo) {
        ipInfo.setAddTime(LocalDateTime.now());
        if ( ipInfo.getId() == null ) {
            IpInfo byIpAddress = ipInfoRepository.findByIpAddress(ipInfo.getIpAddress());
            if ( byIpAddress != null ) throw new RuntimeException("ip地址已存在");
        }
        return ipInfoRepository.save(ipInfo);
    }

    // 批量删除 IP 信息
    @Transactional
    public void deleteBatch(List<String> ids) {
        ipInfoRepository.deleteAllById(ids);  // 使用 JPA 提供的批量删除方法
    }

    @Transactional(rollbackFor = Exception.class)
    public List<IpInfo> batchSave(List<IpInfo> ipInfos) {
        // 获取所有ipInfos中的ip地址
        List<String> ips = ipInfos.stream()
                .map(IpInfo::getIpAddress)
                .collect(Collectors.toList());

        // 查询数据库中已经存在的ip信息
        List<IpInfo> existingIpInfos = ipInfoRepository.findByIpAddressIn(ips);

        // 获取已经存在的ip地址
        Set<String> existingIps = existingIpInfos.stream()
                .map(IpInfo::getIpAddress)
                .collect(Collectors.toSet());

        // 过滤掉ipInfos中已经存在于数据库的数据
        List<IpInfo> newIpInfos = ipInfos.stream()
                .filter(ipInfo -> !existingIps.contains(ipInfo.getIpAddress()))
                .collect(Collectors.toList());

        // 保存那些在数据库中不存在的数据
        return ipInfoRepository.saveAll(newIpInfos);
    }

    // 查询 IP 信息
    public IpInfo findById(String id) {
        return ipInfoRepository.findById(id).orElseGet(IpInfo::new);
    }

    // 删除 IP 信息
    public void deleteById(String id) {
        ipInfoRepository.deleteById(id);
    }

    public static final String newClashFileName = "new_clash_config.yaml";
    public String processConfigFile(File configFile, int port) throws Exception {
        // 创建 LoaderOptions
        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(Map.class, loaderOptions));

        // 读取原始配置文件并解析为 Map
        Map<String, Object> clashConfig = yaml.load(new FileInputStream(configFile));
        if (clashConfig == null) {
            clashConfig = new HashMap<>();
        }

        // 获取静态 IP 信息
        List<IpInfo> allIpInfos = ipInfoRepository.findAll();

        // 获取并初始化 `proxies` 节点
        List<Map<String, Object>> proxies = (List<Map<String, Object>>) clashConfig.get("proxies");
        if (proxies == null) {
            proxies = new ArrayList<>();
            clashConfig.put("proxies", proxies);
        }

        // 获取并初始化 `proxy-groups` 节点
        List<Map<String, Object>> proxyGroups = (List<Map<String, Object>>) clashConfig.get("proxy-groups");
        if (proxyGroups == null) {
            proxyGroups = new ArrayList<>();
            clashConfig.put("proxy-groups", proxyGroups);
        }

        // 获取并初始化 `listeners` 节点
        List<Map<String, Object>> listeners = (List<Map<String, Object>>) clashConfig.get("listeners");
        if (listeners == null) {
            listeners = new ArrayList<>();
            clashConfig.put("listeners", listeners);
        }

        // 遍历 IP 信息，构造代理配置
        for (int i = 0; i < allIpInfos.size(); i++) {
            IpInfo ipInfo = allIpInfos.get(i);
            String proxyName = "nice Proxy " + (i + 1);
            String proxyGroupName = "nice Proxy group " + (i + 1);
            String listenerName = "nice Proxy listener " + (i + 1);

            // 添加 proxy
            Map<String, Object> proxy = new HashMap<>();
            proxy.put("name", proxyName);
            proxy.put("type", "socks5");
            proxy.put("server", ipInfo.getIpAddress());
            proxy.put("port", ipInfo.getPort());
            proxy.put("username", ipInfo.getUsername());
            proxy.put("password", ipInfo.getPassword());
            proxies.add(proxy);

            // 添加 proxy-group
            Map<String, Object> proxyGroup = new HashMap<>();
            proxyGroup.put("name", proxyGroupName);
            proxyGroup.put("type", "relay");
            proxyGroup.put("proxies", List.of("自动选择", proxyName));
            proxyGroups.add(proxyGroup);

            // 添加 listener
            Map<String, Object> listener = new HashMap<>();
            listener.put("name", listenerName);
            listener.put("type", "mixed");
            listener.put("port", port++);
            listener.put("proxy", proxyGroupName);
            listeners.add(listener);
        }

        // 将修改后的配置写入新文件
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml outputYaml = new Yaml(options);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newClashFileName, StandardCharsets.UTF_8))) {
            outputYaml.dump(clashConfig, writer);
        }

        return newClashFileName;
    }

}
