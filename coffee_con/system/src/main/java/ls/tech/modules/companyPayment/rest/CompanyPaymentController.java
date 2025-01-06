package ls.tech.modules.companyPayment.rest;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentController
 * @author: skl
 * @create: 2025-01-03 10:21
 */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import ls.tech.annotation.AnonymousAccess;
import ls.tech.modules.companyPayment.domain.CompanyPayment;
import ls.tech.modules.companyPayment.domain.CompanyPaymentRequest;
import ls.tech.modules.companyPayment.service.CompanyPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: CompanyPaymentController
 * @author: skl
 * @create: 2025-01-02 23:39
 */
@RestController
@RequestMapping("/api/company_payment")
@Api(tags = "咖啡机企业支付绑定")
@Slf4j

public class CompanyPaymentController{

    @Autowired
    private CompanyPaymentService companyPaymentService;

    /**
     * 绑定支付方式
     *
     * @param  request 支付绑定数据
     * @return 操作结果
     */
    @PostMapping("/bind")
    @ApiOperation("绑定支付方式")
    @AnonymousAccess
    public ResponseEntity<String> bindPayments(@RequestBody CompanyPaymentRequest request) {
        companyPaymentService.bindPayments(request);
        return ResponseEntity.ok("支付方式绑定成功");
    }
    /**
     * 禁用支付方式
     *
     * @return 操作结果
     */
    @PostMapping("/disable")
    @ApiOperation("禁用支付方式")
    @AnonymousAccess
    public ResponseEntity<String> disablePayment(@RequestBody CompanyPaymentRequest request) {
        companyPaymentService.disablePayment(request.getCompanyId(), request.getPayTypeCode());
        return ResponseEntity.ok("支付方式禁用成功");
    }
    /**
     * 获取企业绑定的支付方式
     *
     * @param companyId 公司ID
     * @return 支付方式列表
     */
    @GetMapping("/list")
    @ApiOperation("获取企业绑定的支付方式")
    @AnonymousAccess
    public ResponseEntity<List<CompanyPayment>> listPayments(@RequestParam Integer companyId) {
        List<CompanyPayment> payments = companyPaymentService.getPaymentsByCompanyId(companyId);
        return ResponseEntity.ok(payments);
    }
}
