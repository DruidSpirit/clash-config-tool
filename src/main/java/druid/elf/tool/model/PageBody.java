package druid.elf.tool.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Data
@NoArgsConstructor
public class PageBody<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2056823229578504942L;
    private static final int DEFAULT_PAGE_NUM_SIZE = 8;

    //  分页总条数
    private long total;

    //  当前页
    private long pageNum;

    //  每页条数
    private long pageSize;

    //  总页数
    private long pageTotal;

    //  分页条的长度
    private Integer pageNumSize;

    //  分页条的页码
    private List<Long> pageNums;

    //  响应结果
    private List<T> list;

    /**
     * 初始化并计算分页相关参数
     * @param total         分页条数
     * @param pageNum       页码
     * @param pageSize      每页多少条
     * @param pageTotal     总页数
     * @param list          分页数据集
     */
    public PageBody(long total, long pageNum, long pageSize, long pageTotal, List<T> list, Integer pageNumSize ) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pageTotal = pageTotal;
        this.list = list;
        if ( pageNumSize == null ) pageNumSize = DEFAULT_PAGE_NUM_SIZE;
        this.pageNumSize = pageNumSize;

        //  计算当前页所在的页码范围
        long d = this.pageNum/this.pageNumSize;    //  获取当前页d段页码上面
        if ( this.pageNum%this.pageNumSize == 0 ) d = d-1;
        this.pageNums = new ArrayList<>();
        for ( long i = d*this.pageNumSize+1; i < d*this.pageNumSize + this.pageNumSize+1; i++ ) {
            if ( i>this.pageTotal ) break;
            this.pageNums.add(i);
        }


    }

    /**
     * 提取mybatis的分页数据重新包装
     * @param page  分页对象
     * @param <T>   具体数据类型
     * @return      封装后的分页对象
     */
    public static <T> PageBody<T> dealWithIPage(Page<T> page){

        return new PageBody<>( page.getTotalElements(), page.getNumber(), page.getSize(), page.getTotalPages(), page.getContent(),DEFAULT_PAGE_NUM_SIZE );
    }

    public static <T> PageBody<T> dealWithIPage(Page<T> page, Integer pageNumSize ){

        return new PageBody<>( page.getTotalElements(), page.getNumber(), page.getSize(), page.getTotalPages(), page.getContent(),pageNumSize );
    }

    /**
     * 将list集合包装成分页数据,同时使用替代的list来作为其返回数据
     * @param page          分页对象
     * @param replaceList   分页数据替换对象
     * @param <T>           具体数据类型
     * @return              封装后的分页对象
     */
    public static <T> PageBody<T> dealWithList(Page<?> page , List<T> replaceList ){

        return new PageBody<>( page.getTotalElements(), page.getNumber(), page.getSize(), page.getTotalPages(), replaceList,DEFAULT_PAGE_NUM_SIZE);
    }

    public static <T> PageBody<T> dealWithList(Page<?> page , List<T> replaceList, Integer pageNumSize ){

        return new PageBody<>( page.getTotalElements(), page.getNumber(), page.getSize(), page.getTotalPages(), replaceList, pageNumSize );
    }
}
