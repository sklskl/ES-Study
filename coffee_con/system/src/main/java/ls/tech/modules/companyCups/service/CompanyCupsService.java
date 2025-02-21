package ls.tech.modules.companyCups.service;

import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import ls.tech.modules.companyCups.domain.criteria.CofQueryCriteria;
import ls.tech.modules.companyCups.domain.criteria.CompanyCupsQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CompanyCupsService {
    /**
     * 获取企业杯量信息
     */
    Page<CompanyCupsDTO> getCompanyCups(CompanyCupsQueryCriteria criteria, Pageable pageable);

    List<CompanyCupsDTO> getCompanyCupsForExport(CompanyCupsQueryCriteria criteria);
    /**
     * 新增企业杯量
     */
    CompanyCupsDTO addCompanyCups(CompanyCupsDTO companyCupsDTO);

    /**
     * 编辑企业杯量
     */
    CompanyCupsDTO updateCompanyCups(Integer id, CompanyCupsDTO companyCupsDTO);

    //根据企业ID获取企业杯量信息
    CompanyCupsDTO getCompanyCupsByCompanyId(Integer companyId);

    /**
     * 删除企业杯量
     */
    void deleteCompanyCups(Integer id);

    //根据多个条件动态搜索企业杯量信息，支持分页
    Page<CompanyCupsDTO> searchCompanyCups(String companyName, Integer status, Integer period, Pageable pageable);

    //根据企业名称模糊搜索企业杯量信息
    List<CompanyCupsDTO> searchCompanyCupsByCompanyName(String companyName);

    void toggleStatus(Integer id, Integer status);

    // 获取企业杯量信息，支持默认当月数据和筛选
    Page<CompanyCupsDTO> getCompanyCupsFiltered(String companyName, Integer status,
                                                LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<CofCompanyInfo> searchCompanies(Integer status);

    /**
     *
     * @param companyName
     * @return
     */
    List<CofCompanyInfo> searchCompanyByName(String companyName);

    List<Map<String, Object>> queryCofCupsCountPage(CofQueryCriteria criteria, com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> page);

    Integer queryCofCupsCountTotal(CofQueryCriteria criteria);

    void downloadCofCupsCount(CofQueryCriteria criteria, HttpServletResponse response) throws IOException;
}