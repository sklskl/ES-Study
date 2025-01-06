package ls.tech.modules.companyCups.repository;

import ls.tech.modules.companyCups.domain.CofPayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayOrderRepository extends JpaRepository<CofPayOrder, Integer> {
    /**
     * 根据公司ID查询所有支付订单。
     *
     * @param companyId 公司ID
     * @return 支付订单列表
     */
    List<CofPayOrder> findByCompanyId(Integer companyId);

    /**
     * 根据公司ID和订单号查询支付订单。
     *
     * @param companyId   公司ID
     * @param orderNo 订单号
     * @return 支付订单的 Optional 对象
     */
    Optional<CofPayOrder> findByCompanyIdAndOrderNo(Integer companyId, String orderNo);
}