package ls.tech.modules.companyCups.repository;

import io.lettuce.core.dynamic.annotation.Param;
import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyPayment.domain.CompanyPayDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CofCompanyInfoRepository extends JpaRepository<CofCompanyInfo, Integer> {
    /**
     * 根据公司代码查询企业信息。
     * @param companyId 公司代码
     * @return 企业信息的 Optional 对象
     */
    Optional<CofCompanyInfo> findByCompanyCode(String companyId);

    @Query(value = "SELECT cp.company_id AS companyId, cp.pay_type_code AS payTypeCode, cp.create_time AS createTime, pt.name AS name " +
            "FROM company_payments cp " +
            "LEFT JOIN cof_pay_type pt ON cp.pay_type_code = pt.code " +
            "WHERE cp.company_id = :companyId", nativeQuery = true)
    List<CompanyPayDTO> findCompanyPayProjectionsByCompanyIdNative(@Param("companyId") Integer companyId);
    List<CofCompanyInfo> findByStatus(Integer status);
    @Query("SELECT c.companyName FROM CofCompanyInfo c WHERE c.status = 1")
    List<String> findCompanyNamesByStatus1();


    List<CofCompanyInfo> findByCompanyName(String companyName);
    List<CofCompanyInfo> findByStatusIn(List<Integer> statuses);
}