package ls.tech.modules.companyCups.repository;

import io.lettuce.core.dynamic.annotation.Param;
import ls.tech.modules.companyCups.domain.CompanyCups;
import ls.tech.modules.companyCups.domain.criteria.CofQueryCriteria;
import ls.tech.modules.companyCups.domain.criteria.CompanyCupsQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CompanyCupsRepository extends JpaRepository<CompanyCups, Integer>, JpaSpecificationExecutor<CompanyCups> {
    Optional<CompanyCups> findByCompanyId(Integer companyId);

    @Query(value = "select aa.company_id as \"companyId\"," +
            "       aa.name as \"userName\"," +
            "       aa.mobile as \"mobile\"," +
            "       (aa.cups_count * (TIMESTAMPDIFF(MONTH, :#{#criteria.createTimeYearMonth[0]}, :#{#criteria.createTimeYearMonth[1]})" +
            "        ))                                 as \"baoYueBeiliang\"," +
            "       (aa.cups_count * (TIMESTAMPDIFF(MONTH, :#{#criteria.createTimeYearMonth[0]}, :#{#criteria.createTimeYearMonth[1]}))" +
            "        - ifnull(bb.companyBaoxiaoBeiliangOrder, 0)) as \"surplusBeiliang\"," +
            "       ifnull(bb.actualBeiliangOrder, 0) as \"actualBeiliang\"," +
            "       ifnull(bb.ziFeiBeiliangOrder, 0) as \"ziFeiBeiliang\"," +
            "       cci.company_name as \"companyName\"" +
            " from cof_company_staff aa" +
            " left join cof_company_info cci on cci.id = aa.company_id" +
            " left join (" +
            "    select cpo.mobile," +
            "           cpo.company_id," +
            "           count(cpo.id) as \"actualBeiliangOrder\"," +
            "           SUM(CASE WHEN cpo.pay_type_code = '10' THEN 1 ELSE 0 END) as \"companyBaoxiaoBeiliangOrder\"," +
            "           SUM(CASE WHEN cpo.pay_type_code = '20' THEN 1 ELSE 0 END) as \"ziFeiBeiliangOrder\"" +
            "    from cof_pay_order cpo" +
            "    where cpo.trade_status in ('00', '06')" +
            "      and DATE_FORMAT(cpo.create_time, '%Y-%m-%d') between  :#{#criteria.createTimeYearMonth[0]} and :#{#criteria.createTimeYearMonth[1]} " +
            " group by cpo.company_id,cpo.mobile" +
            " ) bb on aa.mobile = bb.mobile and bb.company_id = cci.id" +
            " where aa.status = '1'" +
            " and (:#{#criteria.username} is null or aa.name like CONCAT('%',:#{#criteria.username},'%'))" +
            " and (:#{#criteria.mobile} is null or aa.mobile like CONCAT('%',:#{#criteria.mobile},'%'))" +
            " and (:#{#criteria.companyName} is null or cci.company_name like CONCAT('%',:#{#criteria.companyName},'%'))" +
            " group by aa.company_id,aa.mobile limit :#{#criteria.firstSize}, :#{#criteria.lastSize}",nativeQuery = true)
    List<Map<String, Object>> queryCofCupsCountPage(CofQueryCriteria criteria);

    @Query(value = "select count(1)" +
            " from cof_company_staff aa" +
            " left join cof_company_info cci on cci.id = aa.company_id where aa.status = '1'" +
            " and (:#{#criteria.username} is null or aa.name like CONCAT('%',:#{#criteria.username},'%'))" +
            " and (:#{#criteria.mobile} is null or aa.mobile like CONCAT('%',:#{#criteria.mobile},'%'))" +
            " and (:#{#criteria.companyName} is null or cci.company_name like CONCAT('%',:#{#criteria.companyName},'%'))",nativeQuery = true)
    Integer queryCofCupsCountTotal(CofQueryCriteria criteria);

    @Query(value = "select aa.company_id as \"companyId\"," +
            "       aa.name as \"userName\"," +
            "       aa.mobile as \"mobile\"," +
            "       (aa.cups_count * (TIMESTAMPDIFF(MONTH, :#{#criteria.createTimeYearMonth[0]}, :#{#criteria.createTimeYearMonth[1]})" +
            "        ))                                 as \"baoYueBeiliang\"," +
            "       (aa.cups_count * (TIMESTAMPDIFF(MONTH, :#{#criteria.createTimeYearMonth[0]}, :#{#criteria.createTimeYearMonth[1]}))" +
            "        - ifnull(bb.companyBaoxiaoBeiliangOrder, 0)) as \"surplusBeiliang\"," +
            "       ifnull(bb.actualBeiliangOrder, 0) as \"actualBeiliang\"," +
            "       ifnull(bb.ziFeiBeiliangOrder, 0) as \"ziFeiBeiliang\"," +
            "       cci.company_name as \"companyName\"" +
            " from cof_company_staff aa" +
            " left join cof_company_info cci on cci.id = aa.company_id" +
            " left join (" +
            "    select cpo.mobile," +
            "           cpo.company_id," +
            "           count(cpo.id) as \"actualBeiliangOrder\"," +
            "           SUM(CASE WHEN cpo.pay_type_code = '10' THEN 1 ELSE 0 END) as \"companyBaoxiaoBeiliangOrder\"," +
            "           SUM(CASE WHEN cpo.pay_type_code = '20' THEN 1 ELSE 0 END) as \"ziFeiBeiliangOrder\"" +
            "    from cof_pay_order cpo" +
            "    where cpo.trade_status in ('00', '06')" +
            "      and DATE_FORMAT(cpo.create_time, '%Y-%m-%d') between  :#{#criteria.createTimeYearMonth[0]} and :#{#criteria.createTimeYearMonth[1]} " +
            " group by cpo.company_id,cpo.mobile" +
            " ) bb on aa.mobile = bb.mobile and bb.company_id = cci.id" +
            " where aa.status = '1'" +
            " and (:#{#criteria.username} is null or aa.name like CONCAT('%',:#{#criteria.username},'%'))" +
            " and (:#{#criteria.mobile} is null or aa.mobile like CONCAT('%',:#{#criteria.mobile},'%'))" +
            " and (:#{#criteria.companyName} is null or cci.company_name like CONCAT('%',:#{#criteria.companyName},'%'))" +
            " group by aa.company_id,aa.mobile",nativeQuery = true)
    List<Map<String, Object>> queryCofCupsCount(CofQueryCriteria criteria);


    @Query(value = "SELECT " +
            "cci.company_name AS companyName, " +
            "cci.company_code AS companyCode, " +
            "ccc.free_cups_count AS freeCupsCount, " +
            "COALESCE(SUM(cpo.actual_cups), 0) AS actualCups, " +
            "COALESCE(SUM(cpo.self_paid_cups), 0) AS selfPaidCups, " +
            "COALESCE(SUM(cpo.remaining_cups), 0) AS remainingCups, " +
            "ccc.status AS status, " +
            "ccc.create_time AS createTime " +
            "FROM cof_company_cups ccc " +
            "JOIN cof_company_info cci ON ccc.company_id = cci.id " +
            "LEFT JOIN (" +
            "SELECT " +
            "company_id, " +
            "SUM(CASE WHEN trade_status IN ('00', '06') THEN 1 ELSE 0 END) AS actual_cups, " +
            "SUM(CASE WHEN trade_status IN ('00', '06') AND pay_type_code != '10' THEN 1 ELSE 0 END) AS self_paid_cups, " +
            "SUM(CASE WHEN trade_status IN ('00', '06') THEN 0 ELSE 1 END) AS remaining_cups " +
            "FROM cof_pay_order " +
            "WHERE create_time BETWEEN :#{#criteria.timeRanges[0]} AND :#{#criteria.timeRanges[1]} " +
            "GROUP BY company_id " +
            ") cpo ON ccc.company_id = cpo.company_id " +
            "WHERE (:#{#criteria.companyName} IS NULL OR cci.company_name LIKE CONCAT('%', :#{#criteria.companyName}, '%')) " +
            "AND (:#{#criteria.status} IS NULL OR ccc.status = :#{#criteria.status}) " +
            "AND ccc.status != 2 " +
            "GROUP BY cci.company_name, cci.company_code, ccc.free_cups_count, ccc.status, ccc.create_time",
            nativeQuery = true)
    List<Map<String, Object>> getCompanyCupsForExport(@Param("criteria") CompanyCupsQueryCriteria criteria);


    @Query(value = ""
            + "SELECT "
            + "  MIN(ccc.id) AS id, "  // 当同一企业多个月时，可以取最小的id
            + "  ccc.company_id AS companyId, "
            + "  cci.company_name AS companyName, "
            + "  cci.company_code AS companyCode, "
            + "  SUM(ccc.free_cups_count) AS freeCupsCount, "  // 累加多个包月记录
            + "  SUM(COALESCE(ccu.actual_cups, 0)) AS actualCups, "
            + "  SUM(COALESCE(ccu.self_paid_cups, 0)) AS selfPaidCups, "
            + "  SUM(COALESCE(ccu.unit_paid_cups, 0)) AS unitPaidCups, "
            + "  SUM(ccc.free_cups_count) - SUM(COALESCE(ccu.unit_paid_cups, 0)) AS remainingCups, "
            + "  MAX(ccc.status) AS status, "  // 这里采用 MAX，也可以根据实际需要调整
            + "  MAX(ccc.create_time) AS createTime "
            + "FROM cof_company_cups ccc "
            + "JOIN cof_company_info cci ON ccc.company_id = cci.id "
            + "LEFT JOIN ( "
            + "    SELECT "
            + "      company_id, "
            + "      COUNT(*) AS actual_cups, "
            + "      SUM(CASE WHEN pay_type_code != '10' THEN 1 ELSE 0 END) AS self_paid_cups, "
            + "      SUM(CASE WHEN pay_type_code = '10' THEN 1 ELSE 0 END) AS unit_paid_cups "
            + "    FROM cof_pay_order "
            + "    WHERE trade_status IN ('00','06','07') "
            + "      AND create_time BETWEEN STR_TO_DATE(:#{#criteria.startTime},'%Y-%m-%d %H:%i:%s') "
            + "                          AND STR_TO_DATE(:#{#criteria.endTime},'%Y-%m-%d %H:%i:%s') "
            + "    GROUP BY company_id "
            + ") ccu ON ccc.company_id = ccu.company_id "
            + "WHERE (:#{#criteria.companyName} IS NULL OR cci.company_name LIKE CONCAT(:#{#criteria.companyName},'%')) "
            + "  AND (:#{#criteria.status} IS NULL OR ccc.status = :#{#criteria.status}) "
            + "  AND ccc.create_time BETWEEN STR_TO_DATE(:#{#criteria.startTime},'%Y-%m-%d %H:%i:%s') "
            + "                          AND STR_TO_DATE(:#{#criteria.endTime},'%Y-%m-%d %H:%i:%s') "
            + "GROUP BY ccc.company_id, cci.company_name, cci.company_code "
            + "ORDER BY MAX(ccc.create_time) DESC ",

            countQuery = ""
                    + "SELECT COUNT(*) FROM ("
                    + "  SELECT ccc.company_id "
                    + "  FROM cof_company_cups ccc "
                    + "  JOIN cof_company_info cci ON ccc.company_id = cci.id "
                    + "  WHERE (:#{#criteria.companyName} IS NULL OR cci.company_name LIKE CONCAT(:#{#criteria.companyName},'%')) "
                    + "    AND (:#{#criteria.status} IS NULL OR ccc.status = :#{#criteria.status}) "
                    + "    AND ccc.create_time BETWEEN STR_TO_DATE(:#{#criteria.startTime},'%Y-%m-%d %H:%i:%s') "
                    + "                            AND STR_TO_DATE(:#{#criteria.endTime},'%Y-%m-%d %H:%i:%s') "
                    + "  GROUP BY ccc.company_id, cci.company_name, cci.company_code "
                    + ") t",

            nativeQuery = true)
    Page<Object[]> getCompanyCupsSummary(@Param("criteria") CompanyCupsQueryCriteria criteria,
                                         Pageable pageable);


    @Query(value = "SELECT ccc " +
            "FROM CompanyCups ccc " +
            "JOIN CofCompanyInfo cci ON ccc.companyId = cci.id " +
            "WHERE ccc.createTime BETWEEN :startTime AND :endTime " +
            "AND (:companyName IS NULL OR cci.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "AND (:status IS NULL OR ccc.status = :status) " +
            "ORDER BY ccc.createTime DESC")
    Page<CompanyCups> findAllByCriteria(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("companyName") String companyName,
            @Param("status") Integer status,
            Pageable pageable
    );
    boolean existsByCompanyId(Integer companyId);


        // 统计实际杯数（支付订单中所有订单数）
        @Query(value = "SELECT company_id, COUNT(*) " +
                "FROM cof_pay_order " +
                "WHERE trade_status IN ('00','06') " +
                "  AND create_time BETWEEN :startDate AND :endDate " +
                "GROUP BY company_id", nativeQuery = true)
        List<Object[]> countActualCups(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

        // 统计自费杯数（支付订单中 pay_type_code <> '10' 的订单数）
        @Query(value = "SELECT company_id, COUNT(*) " +
                "FROM cof_pay_order " +
                "WHERE pay_type_code <> '10' " +
                "  AND trade_status IN ('00','06') " +
                "  AND create_time BETWEEN :startDate AND :endDate " +
                "GROUP BY company_id", nativeQuery = true)
        List<Object[]> countSelfPaidCups(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

        // 累加包月杯量（累计 free_cups_count，可根据时间区间过滤）
        @Query(value = "SELECT company_id, SUM(free_cups_count) " +
                "FROM cof_company_cups " +
                "WHERE create_time BETWEEN :startDate AND :endDate " +
                "GROUP BY company_id", nativeQuery = true)
        List<Object[]> sumFreeCups(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    @Query(value = "SELECT company_id, COUNT(*) " +
            "FROM cof_company_cups " +
            "WHERE create_time BETWEEN :startDate AND :endDate " +
            "GROUP BY company_id", nativeQuery = true)
    List<Object[]> countFreeCupsRecords(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);


    // 查询企业基本信息，同时联表获取企业编码和企业名称，并根据传入条件过滤
    @Query(value =
            "SELECT cc.id AS id, " +
                    "       cc.company_id AS companyId, " +
                    "       info.company_code AS companyCode, " +
                    "       info.company_name AS companyName, " +
                    "       cc.free_cups_count AS freeCupsCount, " +
                    "       cc.status AS cupsStatus, " +
                    "       po.orderTime AS orderTime, " +
                    "       cc.modify_time AS modifyTime, " +
                    "       cc.create_time AS cupsCreateTime " +   // ← 这里把 cups 本身创建时间也查出来
                    "FROM cof_company_cups cc " +
                    "JOIN cof_company_info info ON cc.company_id = info.id " +
                    "LEFT JOIN ( " +
                    "    SELECT company_id, MAX(create_time) AS orderTime " +
                    "    FROM cof_pay_order " +
                    "    WHERE create_time BETWEEN :startDate AND :endDate " +
                    "    GROUP BY company_id " +
                    ") po ON cc.company_id = po.company_id " +
                    "WHERE cc.free_cups_count > 0 " +
                    "AND cc.status <> 2  "+
                    "  AND (:companyName IS NULL OR :companyName = '' OR info.company_name LIKE CONCAT('%', :companyName, '%')) " +
                    "  AND (:status IS NULL OR cc.status = :status)",
            nativeQuery = true)
    List<Object[]> findCupsRecords(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("companyName") String companyName,
                                   @Param("status") Integer status);


}
