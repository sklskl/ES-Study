package ls.tech.modules.companyPayment.domain;

import lombok.Data;

import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentRequest
 * @author: skl
 * @create: 2025-01-03 10:25
 */
@Data
public class CompanyPaymentRequest {
    /**
     * 企业ID
     */
    private Integer companyId;

    /**
     * 支付方式编码集合
     */
    private List<String> payTypeCodes;

    /**
     * 操作员ID
     */
//    private Integer operatorId;
}