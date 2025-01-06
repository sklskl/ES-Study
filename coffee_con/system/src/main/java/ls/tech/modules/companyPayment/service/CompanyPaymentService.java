package ls.tech.modules.companyPayment.service;

import ls.tech.modules.companyPayment.domain.CompanyPayment;
import ls.tech.modules.companyPayment.domain.CompanyPaymentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentService
 * @author: skl
 * @create: 2025-01-03 10:24
 */
@Service
public interface CompanyPaymentService {
    /**
     * 绑定支付方式
     *
     * @param request 支付绑定数据
     */
    void bindPayments(CompanyPaymentRequest request);

    /**
     * 禁用支付方式
     *
     * @param companyId 公司ID
     * @param payTypeCode 支付方式编码
     */
    void disablePayment(Integer companyId, String payTypeCode);

    /**
     * 根据公司ID获取绑定的支付方式
     *
     * @param companyId 公司ID
     * @return 支付方式列表
     */
    List<CompanyPayment> getPaymentsByCompanyId(Integer companyId);
}