package ls.tech.modules.companyCups.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: CompanyCups
 * @author: skl
 * @create: 2025-01-04 09:30
 */

@Data
@Entity
@Table(name = "cof_company_cups")
public class CompanyCups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 多对一关系，关联 CompanyInfo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private CofCompanyInfo companyInfo;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "free_cups_count", nullable = false)
    private Integer freeCupsCount;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "period", nullable = false, columnDefinition = "int default 1")
    private Integer period = 1;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "operator")
    private Integer operator;
}
