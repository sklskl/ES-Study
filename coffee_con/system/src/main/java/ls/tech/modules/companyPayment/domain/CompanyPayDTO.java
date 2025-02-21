package ls.tech.modules.companyPayment.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: cofCompanyPay
 * @author: skl
 * @create: 2025-01-02 23:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPayDTO {
    private Integer id;                 // 公司支付记录ID
    private Integer companyId; // 企业ID
    private String payTypeCode; // 支付方式编码集合
    private String name;//企业名称
    private LocalDateTime createTime;//创建时间

    private LocalDateTime modifyTime;   // 修改时间
    private String operator;            // 操作员
    private Integer status;             // 状态 1:正常 0:删除
    private Integer sort;             // 排序
}