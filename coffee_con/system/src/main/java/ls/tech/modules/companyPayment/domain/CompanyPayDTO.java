package ls.tech.modules.companyPayment.domain;


import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: cofCompanyPay
 * @author: skl
 * @create: 2025-01-02 23:26
 */
public class CompanyPayDTO {
    private Integer companyId; // 企业ID
    private List<String> payTypeCodes; // 支付方式编码集合
    private Integer operatorId; // 操作员ID
}