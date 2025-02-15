package druid.elf.tool.repository;

import druid.elf.tool.entity.IpInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpInfoRepository extends JpaRepository<IpInfo, String> {
    // 你可以根据需要在这里添加自定义查询方法
    IpInfo findByIpAddress(String ipAddress);

    // 根据ipAddress列表查询数据库中已经存在的IpInfo
    List<IpInfo> findByIpAddressIn(List<String> ipAddresses);
}