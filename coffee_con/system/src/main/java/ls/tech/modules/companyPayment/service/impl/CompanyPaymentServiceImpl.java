package ls.tech.modules.companyPayment.service.impl;

import lombok.extern.slf4j.Slf4j;
import ls.tech.exception.BadRequestException;
import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyCups.repository.CofCompanyInfoRepository;
import ls.tech.modules.companyPayment.domain.*;
import ls.tech.modules.companyPayment.repository.CompanyPayRepository;
import ls.tech.modules.companyPayment.repository.PayTypeRepository;
import ls.tech.modules.companyPayment.service.CompanyPaymentService;
import ls.tech.modules.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentServiceImpl
 * @description: 企业支付方式服务实现类
 */
@Slf4j
@Service
public class CompanyPaymentServiceImpl implements CompanyPaymentService {

    private final CompanyPayRepository companyPayRepository;
    private final PayTypeRepository payTypeRepository;
    private final CofCompanyInfoRepository cofCompanyInfoRepository;

    @Autowired
    public CompanyPaymentServiceImpl(CompanyPayRepository companyPayRepository,
                                     PayTypeRepository payTypeRepository,
                                     CofCompanyInfoRepository cofCompanyInfoRepository) {
        this.companyPayRepository = companyPayRepository;
        this.payTypeRepository = payTypeRepository;
        this.cofCompanyInfoRepository = cofCompanyInfoRepository;
    }

    /**
     * 绑定支付方式
     *
     * @param request 支付绑定数据
     */
    @Override
    @Transactional
    public void bindPayments(CompanyPaymentRequest request) {
        Integer companyId = request.getCompanyId();
        List<String> payTypeCodes = Optional.ofNullable(request.getPayTypeCodes())
                .orElseThrow(() -> new BadRequestException("支付方式编码列表不能为空"));

        log.info("开始绑定公司ID={} 的支付方式: {}", companyId, payTypeCodes);

        // 验证公司是否存在
        CofCompanyInfo company = cofCompanyInfoRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("无效的公司ID: " + companyId));

        // 验证支付方式编码是否存在于 cof_pay_type 表中并且状态为启用
        List<CofPayType> validPayTypes = payTypeRepository.findByCodeIn(payTypeCodes).stream()
                .filter(pt -> pt.getStatus() == 1)  // 只过滤出启用状态的支付方式
                .collect(Collectors.toList());

        // 如果有禁用的支付方式
        List<String> invalidPayTypes = payTypeCodes.stream()
                .filter(code -> validPayTypes.stream().noneMatch(pt -> pt.getCode().equals(code)))
                .collect(Collectors.toList());

        if (!invalidPayTypes.isEmpty()) {
            log.warn("以下支付方式已禁用，无法绑定: {}", invalidPayTypes);
            throw new BadRequestException("支付方式已禁用，无法绑定: " + invalidPayTypes);
        }

        // 查找已经绑定的支付方式（包括禁用的）
        List<CompanyPayment> existingBindings = companyPayRepository.findByCompanyIdAndPayTypeCodeIn(companyId, payTypeCodes);

        // 查找已禁用（status = 0）的支付方式，并报错
        List<CompanyPayment> disabledBindings = existingBindings.stream()
                .filter(binding -> binding.getStatus() == 0)  // 过滤出已禁用的支付方式
                .collect(Collectors.toList());

        if (!disabledBindings.isEmpty()) {
            List<String> disabledCodes = disabledBindings.stream()
                    .map(CompanyPayment::getPayTypeCode)
                    .collect(Collectors.toList());
            log.warn("以下支付方式已禁用，正在重新启用: {}", disabledCodes);
            // 将已禁用的支付方式重新启用
            disabledBindings.forEach(binding -> {
                binding.setStatus(1); // 设置为启用
                binding.setModifyTime(LocalDateTime.now());
            });
            companyPayRepository.saveAll(disabledBindings); // 批量保存
        }

        // 查找还没有绑定的支付方式（包括启用的）
        List<CompanyPayment> newBindings = payTypeCodes.stream()
                .filter(code -> existingBindings.stream().noneMatch(binding -> binding.getPayTypeCode().equals(code)))
                .map(payTypeCode -> {
                    CompanyPayment companyPay = new CompanyPayment();
                    companyPay.setCompanyId(companyId);
                    companyPay.setPayTypeCode(payTypeCode);
                    companyPay.setStatus(1); // 设置状态为启用
                    companyPay.setCreateTime(LocalDateTime.now());
                    companyPay.setModifyTime(LocalDateTime.now());
                    return companyPay;
                })
                .collect(Collectors.toList());

        // 保存所有新的绑定记录
        try {
            companyPayRepository.saveAll(newBindings);
            log.info("已为公司ID={} 绑定新的支付方式: {}", companyId, newBindings.stream().map(CompanyPayment::getPayTypeCode).collect(Collectors.toList()));
        } catch (DataIntegrityViolationException ex) {
            log.error("外键约束失败，无法绑定支付方式: {}", payTypeCodes, ex);
            throw new BadRequestException("支付方式绑定失败，存在无效的支付方式编码。");
        }
    }

    /**
     * 禁用支付方式
     *
     * @param companyId    公司ID
     * @param payTypeCodes 支付方式编码
     * @return 禁用结果映射
     */
    @Override
    @Transactional
    public Map<String, String> disablePayment(Integer companyId, List<String> payTypeCodes) {
        if (companyId == null) {
            throw new BadRequestException("公司ID不能为空");
        }
        if (payTypeCodes == null || payTypeCodes.isEmpty()) {
            throw new BadRequestException("支付方式编码列表不能为空");
        }

        Map<String, String> results = new HashMap<>();
        log.info("开始禁用公司ID={} 的支付方式: {}", companyId, payTypeCodes);

        // 查询所有相关的支付方式
        List<CompanyPayment> companyPays = companyPayRepository.findByCompanyIdAndPayTypeCodeIn(companyId, payTypeCodes);

        // 获取存在的 payTypeCodes
        Set<String> existingPayTypeCodes = companyPays.stream()
                .map(CompanyPayment::getPayTypeCode)
                .collect(Collectors.toSet());

        // 处理传入的 payTypeCodes 中不存在的情况
        for (String code : payTypeCodes) {
            if (!existingPayTypeCodes.contains(code)) {
                results.put(code, "该支付方式不存在或不属于该公司");
            }
        }

        // 遍历查询结果，检查每个支付方式的状态
        for (CompanyPayment companyPay : companyPays) {
            String code = companyPay.getPayTypeCode();
            if (companyPay.getStatus() == 0) { // 已经禁用
                results.put(code, "该支付方式已经禁用");
            } else {
                companyPay.setStatus(0); // 设置为禁用
                companyPay.setModifyTime(LocalDateTime.now());
                results.put(code, "支付方式禁用成功");
            }
        }

        // 批量保存所有修改
        companyPayRepository.saveAll(companyPays);
        log.info("禁用支付方式结果: {}", results);

        return results;
    }

    /**
     * 根据公司ID获取绑定的支付方式（分页）
     *
     * @param companyId 公司ID
     * @param pageable  分页参数
     * @return 分页的 CompanyPayDTO 列表
     */
    @Override
    public Page<CompanyPayDTO> getCompanyPaymentDTOsByCompanyId(Integer companyId, Pageable pageable) {
        if (companyId == null) {
            throw new BadRequestException("公司ID不能为空");
        }
        log.info("获取公司ID={} 的绑定支付方式，分页信息: {}", companyId, pageable);
        Page<CompanyPayment> paymentPage = companyPayRepository.findByCompanyId(companyId, pageable);
        return paymentPage.map(this::mapProjectionToDTO); // 使用正确的映射函数
    }

    /**
     * 根据公司ID获取绑定的支付方式 DTO 列表
     *
     * @param companyId 公司ID
     * @return CompanyPayDTO 列表
     */
    @Override
    public List<CompanyPayDTO> getCompanyPaymentDTOsByCompanyId(Integer companyId) {
        if (companyId == null) {
            throw new BadRequestException("公司ID不能为空");
        }
        CofCompanyInfo companyInfo = cofCompanyInfoRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID " + companyId));
        log.info("获取公司ID={} 的绑定支付方式 DTO 列表", companyId);

        List<CompanyPayDTO> dtoList = companyPayRepository.findCompanyPayDTOsByCompanyIdNative(companyId)
                .stream()
                .map(this::mapToDTOFromObjectArray) // 使用正确的映射函数
                .collect(Collectors.toList());

        return dtoList;
    }

    /**
     * 根据公司代码获取绑定的支付方式 DTO 列表
     *
     * @param companyCode 公司代码
     * @return CompanyPayDTO 列表
     */
    @Override
    public List<CompanyPayDTO> getCompanyPaymentDTOsByCompanyCode(String companyCode) {
        if (companyCode == null || companyCode.trim().isEmpty()) {
            throw new BadRequestException("公司代码不能为空");
        }
        CofCompanyInfo companyInfo = cofCompanyInfoRepository.findByCompanyCode(companyCode)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with code " + companyCode));
        log.info("获取公司代码={} 的绑定支付方式 DTO 列表", companyCode);

        List<CompanyPayDTO> dtoList = companyPayRepository.findCompanyPayDTOsByCompanyCodeNative(companyCode)
                .stream()
                .map(this::mapToDTOFromObjectArray) // 使用正确的映射函数
                .collect(Collectors.toList());

        return dtoList;
    }

    /**
     * 根据公司ID获取企业支付信息列表
     *
     * @param companyId 公司ID
     * @return CompanyPayment 列表
     */
    @Override
    public List<CompanyPayment> getPaymentsByCompanyId(Integer companyId) {
        if (companyId == null) {
            throw new BadRequestException("公司ID不能为空");
        }
        log.info("获取公司ID={} 的企业支付信息列表", companyId);
        return companyPayRepository.findByCompanyId(companyId);
    }

    @Override
    public Page<PaymentMethodStatusDTO> getAllPaymentMethodsWithStatusNative(Integer companyId, Pageable pageable) {
        // 调用分页查询方法
        Page<Object[]> pageResult = companyPayRepository.findAllPaymentMethodsWithStatusJPQL(companyId, pageable);

        // 将分页结果中的数据转换为 List<PaymentMethodStatusDTO>
        List<PaymentMethodStatusDTO> dtoList = pageResult.getContent().stream()
                .map(record -> {
                    String code = (String) record[0];
                    String name = (String) record[1];
                    Integer status = record[2] != null ? ((Number) record[2]).intValue() : null;
                    LocalDateTime createTime = record[3] != null ? ((java.sql.Timestamp) record[3]).toLocalDateTime() : null;

                    PaymentMethodStatusDTO dto = new PaymentMethodStatusDTO();
                    dto.setCode(code);
                    dto.setName(name);
                    dto.setStatus(status);
                    dto.setCreateTime(createTime);
                    return dto;
                })
                .collect(Collectors.toList());

        // 返回分页结果
        return new PageImpl<>(dtoList, pageable, pageResult.getTotalElements());
    }


    /**
     * 将 CompanyPayment 实体映射为 CompanyPayDTO
     *
     * @param payment CompanyPayment 实体
     * @return CompanyPayDTO 对象
     */
    private CompanyPayDTO mapProjectionToDTO(CompanyPayment payment) {
        return new CompanyPayDTO(
                payment.getId(),
                payment.getCompanyId(),
                payment.getPayTypeCode(),
                payment.getPayType() != null ? payment.getPayType().getName() : null,
                payment.getCreateTime(),
                payment.getModifyTime(),
                payment.getOperator(),
                payment.getStatus(),
                payment.getSort()
        );
    }

    /**
     * 将原生查询结果映射为 CompanyPayDTO
     *
     * @param record 原生查询结果数组
     * @return CompanyPayDTO 对象
     */
    private CompanyPayDTO mapToDTOFromObjectArray(Object[] record) {
        return new CompanyPayDTO(
                record[0] != null ? ((Number) record[0]).intValue() : null, // id
                record[1] != null ? ((Number) record[1]).intValue() : null, // companyId
                (String) record[2], // payTypeCode
                (String) record[3], // name
                record[4] != null ? ((java.sql.Timestamp) record[4]).toLocalDateTime() : null, // createTime
                record[5] != null ? ((java.sql.Timestamp) record[5]).toLocalDateTime() : null, // modifyTime
                (String) record[6], // operator
                record[7] != null ? ((Number) record[7]).intValue() : null, // status
                record[8] != null ? ((Number) record[8]).intValue() : null  // sort
        );
    }
}
