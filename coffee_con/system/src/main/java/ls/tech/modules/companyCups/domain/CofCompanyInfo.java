package ls.tech.modules.companyCups.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "status", nullable = false)//企业状态 1:正常 0：删除 2:注销
    private Integer status;

    @Column(name = "contract_start_date", nullable = false)//合同开始时间
    private LocalDateTime contractStartDate;

    @Column(name = "contract_end_date", nullable = false)//合同结束时间
    private LocalDateTime contractEndDate;

    @Column(name = "contract_status", nullable = false)//合同状态 1:进行中 0:待确认 2:已终止
    private Integer contractStatus;

    @Column(name = "collaboration_mode", nullable = false, length = 10)//合作模式 关联字典表
    private String collaborationMode;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Column(name = "operator", length = 10)
    private String operator;

    @Column(name = "aes_key", length = 100)
    private String aesKey;
}

