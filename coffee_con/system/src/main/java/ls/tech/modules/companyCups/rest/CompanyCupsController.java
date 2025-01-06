package ls.tech.modules.companyCups.rest;

import ls.tech.annotation.AnonymousAccess;
import ls.tech.modules.ResponseDTO;
import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import ls.tech.modules.companyCups.service.CompanyCupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * @program: coffee_con
 * @ClassName: CompanyCupsController
 * @author: skl
 * @create: 2025-01-04 09:47
 */
@RestController
@RequestMapping("/api/company-cups")
public class CompanyCupsController {

    @Autowired
    private CompanyCupsService companyCupsService;

    @GetMapping
    @AnonymousAccess
    public ResponseDTO<Page<CompanyCupsDTO>> getCompanyCups(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String effectiveTimeStart,
            @RequestParam(required = false) String effectiveTimeEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 解析时间参数
        LocalDateTime start = (effectiveTimeStart != null && !effectiveTimeStart.isEmpty()) ? LocalDateTime.parse(effectiveTimeStart) : null;
        LocalDateTime end = (effectiveTimeEnd != null && !effectiveTimeEnd.isEmpty()) ? LocalDateTime.parse(effectiveTimeEnd) : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyCupsDTO> cupsPage = companyCupsService.getCompanyCups(companyName, status, start, end, pageable);
        return ResponseDTO.success(cupsPage);
    }

    /**
     * 新增企业杯量
     */
    @PostMapping
    public ResponseDTO<CompanyCupsDTO> addCompanyCups(@Valid @RequestBody CompanyCupsDTO companyCupsDTO) {
        CompanyCupsDTO created = companyCupsService.addCompanyCups(companyCupsDTO);
        return ResponseDTO.success(created);
    }

    /**
     * 编辑企业杯量
     */
    @AnonymousAccess
    @PutMapping("/{id}")
    public ResponseDTO<CompanyCupsDTO> updateCompanyCups(@PathVariable Integer id,
                                                         @Valid @RequestBody CompanyCupsDTO companyCupsDTO) {
        CompanyCupsDTO updated = companyCupsService.updateCompanyCups(id, companyCupsDTO);
        return ResponseDTO.success(updated);
    }

    /**
     * 删除企业杯量
     */
    @DeleteMapping("/{id}")
    @AnonymousAccess
    public ResponseDTO<String> deleteCompanyCups(@PathVariable Integer id) {
        companyCupsService.deleteCompanyCups(id);
        return ResponseDTO.success("Deleted successfully");
    }
}