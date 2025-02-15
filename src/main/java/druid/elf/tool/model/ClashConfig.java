package druid.elf.tool.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ClashConfig {
    private List<Map<String, Object>> proxies;
    private List<Map<String, Object>> proxyGroups;
    private List<Map<String, Object>> listeners;

}
