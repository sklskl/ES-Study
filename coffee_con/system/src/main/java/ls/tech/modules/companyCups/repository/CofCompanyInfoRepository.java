package ls.tech.modules.companyCups.repository;

import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CofCompanyInfoRepository extends JpaRepository<CofCompanyInfo, Integer> {
    /**
     * 根据公司代码查询企业信息。
     * @param companyCode 公司代码
     * @return 企业信息的 Optional 对象
     */
    Optional<CofCompanyInfo> findByCompanyCode(String companyCode);
}