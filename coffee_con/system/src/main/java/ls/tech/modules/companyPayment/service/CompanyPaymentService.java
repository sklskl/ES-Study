package ls.tech.modules.companyPayment.service;

import ls.tech.modules.companyPayment.domain.CompanyPayDTO;
import ls.tech.modules.companyPayment.domain.CompanyPayment;
import ls.tech.modules.companyPayment.domain.CompanyPaymentRequest;
import ls.tech.modules.companyPayment.domain.PaymentMethodStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentService
 * @description: 企业支付方式服务接口
 */
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
     * @param companyId     公司ID
     * @param payTypeCodes  支付方式编码
     * @return 禁用结果映射
     */
    Map<String, String> disablePayment(Integer companyId, List<String> payTypeCodes);

    /**
     * 根据公司ID获取绑定的支付方式 DTO 列表
     *
     * @param companyId 公司ID
     * @return CompanyPayDTO 列表
     */
    List<CompanyPayDTO> getCompanyPaymentDTOsByCompanyId(Integer companyId);

    /**
     * 分页获取公司支付方式 DTO 列表
     *
     * @param companyId 公司ID
     * @param pageable  分页参数
     * @return 分页的 CompanyPayDTO 列表
     */
    Page<CompanyPayDTO> getCompanyPaymentDTOsByCompanyId(Integer companyId, Pageable pageable);

    /**
     * 根据公司代码获取绑定的支付方式 DTO 列表
     *
     * @param companyCode 公司代码
     * @return CompanyPayDTO 列表
     */
    List<CompanyPayDTO> getCompanyPaymentDTOsByCompanyCode(String companyCode);

    /**
     * 根据公司ID获取企业支付信息列表
     *
     * @param companyId 公司ID
     * @return CompanyPayment 列表
     */
    List<CompanyPayment> getPaymentsByCompanyId(Integer companyId);

    /**
     * 获取所有支付方式及其绑定状态
     *
     * @param companyId 企业ID
     * @return 支付方式及绑定状态列表
     */
    Page<PaymentMethodStatusDTO> getAllPaymentMethodsWithStatusNative(Integer companyId,Pageable pageable);

}
