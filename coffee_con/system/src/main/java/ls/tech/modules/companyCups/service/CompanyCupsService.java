package ls.tech.modules.companyCups.service;

import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
public interface CompanyCupsService {
    Page<CompanyCupsDTO> getCompanyCups(String companyName, Integer status,
                                        LocalDateTime effectiveTimeStart, LocalDateTime effectiveTimeEnd, Pageable pageable);

    CompanyCupsDTO addCompanyCups(CompanyCupsDTO companyCupsDTO);

    CompanyCupsDTO updateCompanyCups(Integer id, CompanyCupsDTO companyCupsDTO);

    void deleteCompanyCups(Integer id);
}