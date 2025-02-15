package druid.elf.tool.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Comment("IP信息维护表")
@Accessors(chain = true)
public class IpInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4023589405965629556L;

    @Id
    @Comment("主键ID")
    @GeneratedValue(generator = "snowFlake")
    @GenericGenerator(name = "snowFlake", strategy = "druid.elf.tool.util.SnowIdGenerator")
    private String id;

    @Comment("IP地址")
    @Column(length = 45)
    private String ipAddress;

    @Comment("连接端口")
    @Column
    private Integer port;

    @Comment("用户名")
    @Column(nullable = false, length = 50)
    private String username;

    @Comment("密码")
    @Column(nullable = false, length = 100)
    private String password;

    @Comment("连接类型")
    @Column(nullable = false, length = 20)
    private String connectionType;

    @Comment("IP所属国家")
    @Column(length = 50)
    private String country;

    @Comment("排序字段")
    @Column
    private Integer sortOrder;

    @Comment("添加时间")
    @Column
    private LocalDateTime addTime;
}
