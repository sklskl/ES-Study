package ls.tech.modules.companyPayment.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: CompanyPayment
 * @author: skl
 * @create: 2025-01-03 10:23
 */
@Data
@Entity
@Table(name = "cof_company_pay",uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "pay_type_code"}))
public class CompanyPayment {
        private static final long serialVersionUID = 1L;
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        @ApiModelProperty(value = "主键ID")
        private Integer id;

        @Column(name = "company_id", nullable = false)
        @NotNull
        @ApiModelProperty(value = "企业ID")
        private Integer companyId;

        @Column(name = "pay_type_code", nullable = false, length = 10)
        @NotBlank
        @ApiModelProperty(value = "支付方式编码")
        private String payTypeCode;

        @Column(name = "sort")
        @ApiModelProperty(value = "排序字段")
        private Integer sort;

        @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
        @ApiModelProperty(value = "状态 (1：正常；0：删除)")
        private Integer status;

        @Column(name = "operator", length = 20)
        @ApiModelProperty(value = "操作员信息")
        private String operator;

        @CreationTimestamp
        @Column(name = "create_time", updatable = false)
        @ApiModelProperty(value = "创建时间")
        private LocalDateTime createTime;


        @Column(name = "modify_time")
        @ApiModelProperty(value = "修改时间")
        private LocalDateTime modifyTime;

        public void copy(CompanyPayment source) {
            BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

}
