package ls.tech.modules.companyCups.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: PayOrder
 * @author: skl
 * @create: 2025-01-04 09:31
 */

@Data
@Entity
@Table(name = "cof_pay_order")
public class CofPayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_no", nullable = false, length = 50)
    private String orderNo;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "product_name", length = 50)
    private String productName;

    @Column(name = "price")
    private Integer price;

    @Column(name = "trade_amount")
    private Integer tradeAmount;

    @Column(name = "pay_type_code", length = 10)
    private String payTypeCode;

    @Column(name = "machine_code", length = 50)
    private String machineCode;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "close_time")
    private LocalDateTime closeTime;

    @Column(name = "remark", length = 100)
    private String remark;

    @Column(name = "trade_status", nullable = false, length = 10)
    private String tradeStatus;

    @Column(name = "point_position", length = 100)
    private String pointPosition;
}

