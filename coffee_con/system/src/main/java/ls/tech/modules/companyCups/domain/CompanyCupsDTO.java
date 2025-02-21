package ls.tech.modules.companyCups.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: CompanyDTO
 * @author: skl
 * @create: 2025-01-04 09:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCupsDTO {
    private int id;
    private Integer companyId;//企业id
    private String  companyCode;//企业bianm

    @Size(max = 100, message = "企业名称")
    private String companyName; // 企业名称

//    @NotNull(message = "包月杯量")
    private Integer freeCupsCount=0;//包月杯量

//    @NotNull(message = "企业状态 1:正常 0：删除 2:注销")
    private Integer status;

    private Integer period;//杯量周期 1: 自然月

    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private LocalDateTime lastOrderTime;

//    @NotNull(message = "操作人员")
//    private Integer operator;


    private Integer actualCups=0; // 实际杯数
    private Integer selfPaidCups=0; // 自费杯数
    private Integer remainingCups=0;//剩余杯数
}