package ls.tech.modules.companyPayment.repository;

import ls.tech.modules.companyPayment.domain.CompanyPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: coffee_con
 * @ClassName: CompanyPayRepository
 * @description: 企业支付绑定仓库接口
 */
@Repository
public interface CompanyPayRepository extends JpaRepository<CompanyPayment, Integer> {

    List<CompanyPayment> findByCompanyId(Integer companyId);

    Page<CompanyPayment> findByCompanyId(Integer companyId, Pageable pageable);

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
     * @param companyId 公司ID
     * @return 绑定记录的列表
     */
    List<CompanyPayment> findByCompanyIdAndPayTypeCodeIn(Integer companyId, List<String> payTypeCodes);

    /**
     * 根据公司ID获取企业支付信息 DTO 列表
     * @param companyId 公司ID
     * @return 原生查询结果列表
     */
    @Query(value = "SELECT cp.id, cp.company_id, cp.pay_type_code, pt.name, cp.create_time, cp.modify_time, cp.operator, cp.status, pt.sort " +
            "FROM cof_company_pay cp " +
            "JOIN cof_pay_type pt ON cp.pay_type_code = pt.code " +
            "WHERE cp.company_id = :companyId",
            nativeQuery = true)
    List<Object[]> findCompanyPayDTOsByCompanyIdNative(@Param("companyId") Integer companyId);

    /**
     * 根据公司代码查找支付方式并返回 CompanyPayDTO 列表
     *
     * @param companyCode 公司代码
     * @return CompanyPayDTO 列表
     */
    @Query(value = "SELECT cp.id, cp.company_id, cp.pay_type_code, pt.name, cp.create_time, cp.modify_time, cp.operator, cp.status, cp.sort " +
            "FROM cof_company_pay cp " +
            "JOIN cof_company_info ci ON cp.company_id = ci.id " +
            "JOIN cof_pay_type pt ON cp.pay_type_code = pt.code " +
            "WHERE ci.company_code = :companyCode",
            nativeQuery = true)
    List<Object[]> findCompanyPayDTOsByCompanyCodeNative(@Param("companyCode") String companyCode);
    /**
     * 获取所有支付方式及其绑定状态
     * @param companyId 企业ID
     * @return 支付方式及绑定状态列表
     */
    @Query(value = "SELECT pt.code AS code, pt.name AS name, " +
            "CASE WHEN cp.id IS NOT NULL THEN 1 ELSE 0 END AS status, " +
            "pt.create_time AS createTime " +
            "FROM cof_pay_type pt " +
            "LEFT JOIN cof_company_pay cp ON pt.code = cp.pay_type_code AND cp.company_id = :companyId " +
            "WHERE pt.status = 1 " +
            "ORDER BY pt.name ASC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM cof_pay_type pt " +
                    "LEFT JOIN cof_company_pay cp ON pt.code = cp.pay_type_code AND cp.company_id = :companyId " +
                    "WHERE pt.status = 1",
            nativeQuery = true)
    Page<Object[]>  findAllPaymentMethodsWithStatusJPQL(
            @Param("companyId") Integer companyId,Pageable pageable);

}
