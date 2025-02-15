package druid.elf.tool.controller;

import druid.elf.tool.entity.IpInfo;
import druid.elf.tool.model.PageParam;
import druid.elf.tool.service.IpInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/ipinfo")
public class IpInfoController {

    @Autowired
    private IpInfoService ipInfoService;
    @Autowired
    private ResourceLoader resourceLoader;

    // 页面跳转到 IP 信息列表页面
    @GetMapping("/index")  // 直接返回视图名称
    public String listPage() {
        return "ipinfo";  // 返回 ipinfo/list 页面视图
    }

    // 获取分页的IP信息
    @PostMapping("/list")
    @ResponseBody
    public Page<IpInfo> list(@RequestBody PageParam pageParam) {
        return ipInfoService.findAll(pageParam);  // 返回分页数据
    }

    // 保存或更新 IP 信息
    @PostMapping("/save")
    @ResponseBody
    public IpInfo save(@RequestBody IpInfo ipInfo) {
        return ipInfoService.save(ipInfo);  // 自动判断新增或修改
    }

    @PostMapping("/batchSave")
    @ResponseBody
    public List<IpInfo> batchSave(@RequestBody List<IpInfo> ipInfos) {
        // 批量保存数据
        return ipInfoService.batchSave(ipInfos);
    }

    // 查询 IP 信息
    @GetMapping("/query/{id}")
    @ResponseBody
    public IpInfo query(@PathVariable String id) {
        return ipInfoService.findById(id);  // 删除操作
    }

    // 删除 IP 信息
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) {
        ipInfoService.deleteById(id);  // 删除操作
    }

    // 生成 Clash 配置文件
    @PostMapping("/generate")
    @ResponseBody
    public String generateClashConfig(
            @RequestParam(value = "link", required = false) String link,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "startPort", defaultValue = "7901") int startPort) {

        try {
            File configFile = null;

            // 1. 处理订阅链接
            if (link != null && !link.isEmpty()) {
                configFile = downloadFileFromLink(link);
            }
            // 2. 处理上传的文件
            else if (file != null && !file.isEmpty()) {
                configFile = convertMultipartFileToFile(file);
            }
            // 3. 如果既没有链接也没有文件，返回错误
            else {
                return "Error: No link or file provided.";
            }

            // 调用服务层处理逻辑
            try {
                return ipInfoService.processConfigFile(configFile, startPort);
            } catch (Exception e) {
                log.error("文件生成异常", e);
                throw new RuntimeException("文件生成异常");
            }

        } catch (IOException e) {
            log.error("文件网络异常",e);
            return "Error: " + e.getMessage();
        }
    }

    private File downloadFileFromLink(String link) throws IOException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("clash-config-", ".yaml");

        // 使用 HttpClient 下载文件
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .build();

        // 发送请求并将文件保存到临时路径
        HttpResponse<Path> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofFile(tempFile));
        } catch (InterruptedException e) {
            log.error("网络请求异常", e);
            throw new RuntimeException(e);
        }

        // 检查响应状态码
        if (response.statusCode() != 200) {
            throw new IOException("Failed to download file from link: " + link);
        }

        return tempFile.toFile();
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        // 将 MultipartFile 转换为 File 对象
        Path tempFile = Files.createTempFile("clash-config-", ".yaml");
        file.transferTo(tempFile);
        return tempFile.toFile();
    }

    @GetMapping("/check-file")
    public ResponseEntity<String> checkFileExists() {
        String projectRootPath = System.getProperty("user.dir");
        String filePath = projectRootPath + "/" +IpInfoService.newClashFileName;
        Resource resource = resourceLoader.getResource("file:" + filePath);
        if (resource.exists()) {
            return ResponseEntity.ok(IpInfoService.newClashFileName);
        } else {
            return ResponseEntity.ok("");
        }
    }

    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        String projectRootPath = System.getProperty("user.dir");
        String filePath = projectRootPath + "/" +IpInfoService.newClashFileName;
        Resource resource = resourceLoader.getResource("file:" + filePath);
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}