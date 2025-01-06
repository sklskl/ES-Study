package ls.tech.modules.companyCups.service.impl;

import ls.tech.modules.companyCups.domain.CompanyCups;
import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import ls.tech.modules.companyCups.repository.CompanyCupsRepository;
import ls.tech.modules.companyCups.service.CompanyCupsService;
import ls.tech.modules.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @program: coffee_con
 * @ClassName: CompanyCupsServiceImpl
 * @description: 企业杯量管理服务实现类
 * @author: skl
 * @create: 2025-01-04 09:42
 */
@Service
public class CompanyCupsServiceImpl implements CompanyCupsService {

    @Autowired
    private CompanyCupsRepository companyCupsRepository;

    @Override
    public Page<CompanyCupsDTO> getCompanyCups(String companyName, Integer status,
                                               LocalDateTime effectiveTimeStart, LocalDateTime effectiveTimeEnd, Pageable pageable) {

        Specification<CompanyCups> spec = (root, query, cb) -> {
            // 建立与 CompanyInfo 的 Join
            Join<CompanyCups, ?> companyJoin = root.join("companyInfo");

            List<Predicate> predicates = new ArrayList<>();

            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(companyJoin.get("companyName").as(String.class)), "%" + companyName.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (effectiveTimeStart != null && effectiveTimeEnd != null) {
                predicates.add(cb.between(root.get("createTime"), effectiveTimeStart, effectiveTimeEnd));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<CompanyCups> cupsPage = companyCupsRepository.findAll(spec, pageable);
        return cupsPage.map(this::convertToDTO);
    }

    @Override
    public CompanyCupsDTO addCompanyCups(CompanyCupsDTO companyCupsDTO) {
        CompanyCups companyCups = convertToEntity(companyCupsDTO);
        companyCups.setCreateTime(LocalDateTime.now());
        companyCups.setModifyTime(LocalDateTime.now());
        CompanyCups saved = companyCupsRepository.save(companyCups);
        return convertToDTO(saved);
    }

    @Override
    public CompanyCupsDTO updateCompanyCups(Integer id, CompanyCupsDTO companyCupsDTO) {
        CompanyCups existing = companyCupsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyCups not found with id " + id));
        existing.setCompanyId(companyCupsDTO.getCompanyId());
        existing.setFreeCupsCount(companyCupsDTO.getFreeCupsCount());
        existing.setStatus(companyCupsDTO.getStatus());
        existing.setPeriod(companyCupsDTO.getPeriod());
        existing.setModifyTime(LocalDateTime.now());
        existing.setOperator(companyCupsDTO.getOperator());
        CompanyCups updated = companyCupsRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public void deleteCompanyCups(Integer id) {
        CompanyCups existing = companyCupsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyCups not found with id " + id));
        companyCupsRepository.delete(existing);
    }

    private CompanyCupsDTO convertToDTO(CompanyCups companyCups) {
        CompanyCupsDTO dto = new CompanyCupsDTO();
        dto.setId(companyCups.getId());
        dto.setCompanyId(companyCups.getCompanyId());
        dto.setCompanyName(companyCups.getCompanyInfo() != null ? companyCups.getCompanyInfo().getCompanyName() : null);
        dto.setFreeCupsCount(companyCups.getFreeCupsCount());
        dto.setStatus(companyCups.getStatus());
        dto.setPeriod(companyCups.getPeriod());
        dto.setCreateTime(companyCups.getCreateTime());
        dto.setModifyTime(companyCups.getModifyTime());
        dto.setOperator(companyCups.getOperator());
        return dto;
    }

    private CompanyCups convertToEntity(CompanyCupsDTO dto) {
        CompanyCups companyCups = new CompanyCups();
        companyCups.setCompanyId(dto.getCompanyId());
        companyCups.setFreeCupsCount(dto.getFreeCupsCount());
        companyCups.setStatus(dto.getStatus());
        companyCups.setPeriod(dto.getPeriod());
        companyCups.setOperator(dto.getOperator());
        return companyCups;
    }
}
