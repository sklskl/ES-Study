package ls.tech.modules.companyPayment.repository;

import ls.tech.modules.companyPayment.domain.CompanyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: CompanyPayRepository
 * @author: skl
 * @create: 2025-01-03 10:29
 */
@Repository
public interface CompanyPayRepository extends JpaRepository<CompanyPayment, Integer> {

    List<CompanyPayment> findByCompanyId(Integer companyId);

    /**
     * 根据公司ID和支付方式编码集合删除绑定记录。
     *
     * @param companyId    公司ID
     * @param payTypeCodes 支付方式编码集合
     */
    void deleteByCompanyIdAndPayTypeCodeIn(Integer companyId, List<String> payTypeCodes);
    /**
     * 根据公司ID和支付方式编码查询绑定记录。
     *
     * @param companyId    公司ID
     * @return 绑定记录的 Optional 对象
     */
    List<CompanyPayment> findByCompanyIdAndPayTypeCodeIn(Integer companyId, List<String> payTypeCodes);


}