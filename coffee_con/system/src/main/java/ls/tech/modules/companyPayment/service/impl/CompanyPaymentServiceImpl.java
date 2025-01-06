package ls.tech.modules.companyPayment.service.impl;

import lombok.extern.slf4j.Slf4j;
import ls.tech.modules.companyPayment.domain.PayType;
import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyCups.repository.CofCompanyInfoRepository;
import ls.tech.modules.companyPayment.domain.CompanyPayment;
import ls.tech.modules.companyPayment.domain.CompanyPaymentRequest;
import ls.tech.modules.companyPayment.repository.CompanyPayRepository;
import ls.tech.modules.companyPayment.repository.PayTypeRepository;
import ls.tech.modules.companyPayment.service.CompanyPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentServiceImpl
 * @author: skl
 * @create: 2025-01-03 13:53
 */
@Slf4j
@Service
public class CompanyPaymentServiceImpl implements CompanyPaymentService {
    @Autowired
    private CompanyPayRepository companyPayRepository;
    @Autowired
    private PayTypeRepository payTypeRepository;
    @Autowired
    private CofCompanyInfoRepository cofCompanyInfoRepository;


    /**
     * 绑定支付方式
     *
     * @param request 支付绑定数据
     */
    @Override
    @Transactional
    public void bindPayments(CompanyPaymentRequest request) {
        Integer companyId = request.getCompanyId();
        List<String> payTypeCodes = request.getPayTypeCodes();
        Integer operatorId = request.getOperatorId();

        log.info("开始绑定公司ID={} 的支付方式: {}", companyId, payTypeCodes);

        // 验证公司是否存在
        CofCompanyInfo company = cofCompanyInfoRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("无效的公司ID: " + companyId));

        // 验证支付方式编码是否存在于 cof_pay_type 表中并且状态为正常
        List<PayType> validPayTypes = payTypeRepository.findByCodeIn(payTypeCodes).stream()
                .filter(pt -> pt.getStatus() == 1)
                .collect(Collectors.toList());

        if (validPayTypes.size() != payTypeCodes.size()) {
            // 找出无效的编码
            Set<String> validCodes = validPayTypes.stream()
                    .map(PayType::getCode)
                    .collect(Collectors.toSet());
            List<String> invalidCodes = payTypeCodes.stream()
                    .filter(code -> !validCodes.contains(code))
                    .collect(Collectors.toList());
            log.warn("绑定失败，存在无效的支付方式编码: {}", invalidCodes);
            throw new IllegalArgumentException("无效的支付方式编码: " + invalidCodes);
        }

        // 查找已经绑定的支付方式
        List<CompanyPayment> existingBindings = companyPayRepository.findByCompanyIdAndPayTypeCodeIn(companyId, payTypeCodes);
        if (!existingBindings.isEmpty()) {
            List<String> alreadyBoundCodes = existingBindings.stream()
                    .map(CompanyPayment::getPayTypeCode)
                    .collect(Collectors.toList());
            log.info("公司ID={} 已经绑定了支付方式编码: {}", companyId, alreadyBoundCodes);
            throw new IllegalArgumentException("支付方式编码已绑定: " + alreadyBoundCodes);
        }

        // 创建新的绑定记录
        List<CompanyPayment> companyPays = payTypeCodes.stream()
                .map(payTypeCode -> {
                    CompanyPayment companyPay = new CompanyPayment();
                    companyPay.setCompanyId(companyId);
                    companyPay.setPayTypeCode(payTypeCode);
                    companyPay.setStatus(1); // 设置状态为正常
                    companyPay.setOperator(String.valueOf(operatorId));
                    companyPay.setCreateTime(LocalDateTime.now());
                    companyPay.setModifyTime(LocalDateTime.now());
                    return companyPay;
                })
                .collect(Collectors.toList());

        // 保存所有新的绑定记录
        try {
            companyPayRepository.saveAll(companyPays);
            log.info("已为公司ID={} 绑定新的支付方式: {}", companyId, payTypeCodes);
        } catch (DataIntegrityViolationException ex) {
            log.error("外键约束失败，无法绑定支付方式: {}", payTypeCodes, ex);
            throw new IllegalArgumentException("支付方式绑定失败，存在无效的支付方式编码。");
        }
    }


    /**
     * 禁用支付方式
     *
     * @param companyId   公司ID
     * @param payTypeCode 支付方式编码
     */
    @Override
    @Transactional
    public void disablePayment(Integer companyId, String payTypeCode) {
        List<CompanyPayment> companyPays = companyPayRepository.findByCompanyId(companyId);
        for (CompanyPayment companyPay : companyPays) {
            if (companyPay.getPayTypeCode().equals(payTypeCode)) {
                companyPay.setStatus(0); // 设置为禁用
                companyPay.setModifyTime(LocalDateTime.now());
                companyPayRepository.save(companyPay);
            }
        }
    }

    /**
     * 根据公司ID获取绑定的支付方式
     *
     * @param companyId 公司ID
     * @return 支付方式列表
     */
    @Override
    public List<CompanyPayment> getPaymentsByCompanyId(Integer companyId) {
        return companyPayRepository.findByCompanyId(companyId);
    }
}