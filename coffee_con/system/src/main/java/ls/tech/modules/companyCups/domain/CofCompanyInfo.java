package ls.tech.modules.companyCups.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @program: coffee_con
 * @ClassName: CompanyInfo
 * @author: skl
 * @create: 2025-01-04 10:08
 */


@Data
@Entity
@Table(name = "cof_company_info")
public class CofCompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_code", nullable = false, length = 30)
    private String companyCode;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "contract_start_date", nullable = false)
    private LocalDateTime contractStartDate;

    @Column(name = "contract_end_date", nullable = false)
    private LocalDateTime contractEndDate;

    @Column(name = "contract_status", nullable = false)
    private Integer contractStatus;

    @Column(name = "collaboration_mode", nullable = false, length = 10)
    private String collaborationMode;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "operator", length = 10)
    private String operator;

    @Column(name = "aes_key", length = 100)
    private String aesKey;

    @OneToMany(mappedBy = "companyInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CompanyCups> companyCups;
}

