package druid.elf.tool.model;

import lombok.Data;

@Data
//  分页传参实体
public class PageParam {

    //  是否分页
    private Boolean isPaging = true;

    //  当前页码
    private Integer pageNum = 1;

    //  每页条数
    private Integer pageSize = 8;
}
