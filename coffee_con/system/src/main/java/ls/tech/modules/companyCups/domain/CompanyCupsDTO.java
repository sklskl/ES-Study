package ls.tech.modules.companyCups.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: CompanyDTO
 * @author: skl
 * @create: 2025-01-04 09:44
 */
@Data
public class CompanyCupsDTO {
    private Integer id;

    @NotNull(message = "Company ID cannot be null")
    private Integer companyId;

    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName; // 新增字段

    @NotNull(message = "Free cups count cannot be null")
    private Integer freeCupsCount;

    @NotNull(message = "Status cannot be null")
    private Integer status;

    private Integer period;

    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    @NotNull(message = "Operator cannot be null")
    private Integer operator;
}