package ls.tech.modules.companyPayment.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import ls.tech.modules.ResponseDTO;
import ls.tech.modules.companyPayment.domain.CompanyPaymentRequest;
import ls.tech.modules.companyPayment.domain.PaymentMethodStatusDTO;
import ls.tech.modules.companyPayment.service.CompanyPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentController
 * @author: skl
 * @create: 2025-01-02 23:39
 */
@RestController
@RequestMapping("/api/company_payment")
@Api(tags = "企业管理表-支付绑定")
@Slf4j

public class CompanyPaymentController {

    @Autowired
    private CompanyPaymentService companyPaymentService;

    /**
     * 绑定支付方式
     *
     * @param request 支付绑定数据
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation("绑定支付方式")
    @PreAuthorize("@el.check('companyPayment:add')")
    public ResponseEntity<ResponseDTO<Object>> bindPayments(@RequestBody CompanyPaymentRequest request) {
        try {
            // 调用服务层方法，绑定支付方式
            companyPaymentService.bindPayments(request);
            // 返回成功响应
            ResponseDTO<Object> response = ResponseDTO.success(request.getCompanyId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 错误响应，传入错误码500和错误消息
            Map<String, String> errorData = new HashMap<>();
            errorData.put("message", "绑定支付方式失败：" + e.getMessage());

            // 使用 ResponseDTO.error() 返回错误数据
            ResponseDTO<Object> errorResponse = ResponseDTO.error(-1, "error", errorData);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 禁用支付方式
     *
     * @return 操作结果
     */
    @PostMapping("/disable")
    @ApiOperation("禁用支付方式")
    @PreAuthorize("@el.check('companyPayment:disable')")
    public ResponseEntity<ResponseDTO<Object>> disablePayment(@Valid @RequestBody CompanyPaymentRequest request) {
        List<String> payTypeCodes = request.getPayTypeCodes();
        try {
            // 调用禁用支付方式服务
            Map<String, String> disableResults = companyPaymentService.disablePayment(request.getCompanyId(), payTypeCodes);

            // 检查是否有重复禁用的情况，存在则返回错误格式
            Map<String, String> errorData = new HashMap<>();
            disableResults.forEach((key, value) -> {
                if ("该支付方式已经禁用".equals(value)) {
                    errorData.put(key, value); // 如果支付方式已经禁用，将其添加到错误数据中
                }
            });

            // 如果存在已禁用的支付方式，则返回错误响应
            if (!errorData.isEmpty()) {
                return ResponseEntity.status(500).body(ResponseDTO.error(-1, "error", errorData));
            }

            // 如果没有问题，返回成功响应
            return ResponseEntity.ok(ResponseDTO.success(disableResults));
        } catch (Exception e) {
            // 错误响应，传入错误码500和错误消息
            return ResponseEntity.status(500).body(ResponseDTO.error(500, "禁用支付方式失败: " + e.getMessage(), null));
        }
    }

    /**
     * 获取企业绑定的支付方式
     *
     * @param companyId 公司ID
     * @return 支付方式列表
     */

    @GetMapping
    @ApiOperation("获取所有支付方式及其绑定状态")
    @PreAuthorize("@el.check('companyPayment:list')")
    public ResponseEntity<Map<String, Object>> listAllPaymentMethods(
            @RequestParam Integer companyId,
            @RequestParam(defaultValue = "0") int page, // 当前页码，从0开始
            @RequestParam(defaultValue = "10") int size // 每页显示条数
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentMethodStatusDTO> pageResult = companyPaymentService.getAllPaymentMethodsWithStatusNative(companyId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());

        return ResponseEntity.ok(response);
    }

}
