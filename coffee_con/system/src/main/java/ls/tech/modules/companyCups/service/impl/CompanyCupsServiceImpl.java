package ls.tech.modules.companyCups.service.impl;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyCups.domain.CompanyCups;
import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import ls.tech.modules.companyCups.domain.criteria.CofQueryCriteria;
import ls.tech.modules.companyCups.domain.criteria.CompanyCupsQueryCriteria;
import ls.tech.modules.companyCups.repository.CofCompanyInfoRepository;
import ls.tech.modules.companyCups.repository.CofPayOrderRepository;
import ls.tech.modules.companyCups.repository.CompanyCupsRepository;
import ls.tech.modules.companyCups.service.CompanyCupsService;
import ls.tech.modules.exception.ResourceNotFoundException;
import ls.tech.utils.FileUtil;
import ls.tech.utils.RedisPool;
import ls.tech.utils.RedisUtils;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * @program: coffee_con
 * @ClassName: CompanyCupsServiceImpl
 * @description: 企业杯量管理服务实现类
 * @author: skl
 * @create: 2025-01-04 09:42
 */
@Service
@Slf4j
public class CompanyCupsServiceImpl implements CompanyCupsService {

    @Autowired
    private CompanyCupsRepository companyCupsRepository;
    @Autowired
    private CofPayOrderRepository cofPayOrderRepository;

    @Autowired
    private CofCompanyInfoRepository cofCompanyInfoRepository;
    @Autowired
    private  RedisUtils redisUtils;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    private CompanyCupsDTO toDTO(CompanyCups cups) {
        CompanyCupsDTO dto = new CompanyCupsDTO();
        dto.setId(cups.getId());
        dto.setCompanyId(cups.getCompanyId());
        CofCompanyInfo companyInfo = cups.getCompanyInfo();
        if (cups.getCompanyInfo() != null) {
            dto.setCompanyName(cups.getCompanyInfo().getCompanyName());
        } else {
            // 通过 companyId 再次获取企业信息
            CofCompanyInfo company = getCompanyInfoById(cups.getCompanyId());
            if (company != null) {
                dto.setCompanyName(company.getCompanyName());
            }
        }

        dto.setFreeCupsCount(cups.getFreeCupsCount());
        dto.setStatus(cups.getStatus());
        dto.setPeriod(cups.getPeriod());
        dto.setCreateTime(cups.getCreateTime());
        dto.setModifyTime(cups.getModifyTime());

        return dto;
    }


    private void checkCompanyStatus(Integer companyId) {
        if (companyId != null) {
            CofCompanyInfo company = getCompanyInfoById(companyId);
            if (company.getStatus() == 0) {
                throw new IllegalArgumentException("企业状态为关闭，无法进行此操作。");
            }
        } else {
            throw new IllegalArgumentException("企业ID不能为空。");
        }
    }

    private CofCompanyInfo getCompanyInfoById(Integer companyId) {
        return cofCompanyInfoRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + companyId + " 的企业信息。"));
    }
    @Override
    public Page<CompanyCupsDTO> getCompanyCups(CompanyCupsQueryCriteria criteria, Pageable pageable) {
        // 1. 设置时间范围（若未传则默认当月）
        criteria.setDefaultTimeRangeIfEmpty();
        LocalDateTime startDate = criteria.getStartDateTime();
        LocalDateTime endDate = criteria.getEndDateTime();
        LocalDate startMonth = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        LocalDate endMonth   = LocalDate.of(endDate.getYear(),   endDate.getMonth(),   1);

        // between(2025-01-01, 2025-02-01) = 1，再 +1 = 2
        long monthCount = ChronoUnit.MONTHS.between(startMonth, endMonth) + 1;
        // 如果要避免负数（万一传入的 endDate 在 startDate 之前），可加一个保护：
        if(monthCount < 1) {
            monthCount = 1;
        }

        // 2. 查询支付订单数据：实际杯数
        List<Object[]> actualList = companyCupsRepository.countActualCups(startDate, endDate);
        Map<Integer, Integer> actualMap = new HashMap<>();
        for (Object[] row : actualList) {
            Integer compId = ((Number) row[0]).intValue();
            Integer count = ((Number) row[1]).intValue();
            actualMap.put(compId, count);
        }

        // 3. 查询支付订单数据：自费杯数
        List<Object[]> selfPaidList = companyCupsRepository.countSelfPaidCups(startDate, endDate);
        Map<Integer, Integer> selfPaidMap = new HashMap<>();
        for (Object[] row : selfPaidList) {
            Integer compId = ((Number) row[0]).intValue();
            Integer count = ((Number) row[1]).intValue();
            selfPaidMap.put(compId, count);
        }

        // 4. 查询套餐记录
        List<Object[]> cupsRecords = companyCupsRepository.findCupsRecords(
                startDate, endDate,
                criteria.getCompanyName(),
                criteria.getStatus()
        );

        // 组装 DTO
        List<CompanyCupsDTO> dtoList = new ArrayList<>();
        for (Object[] row : cupsRecords) {
            CompanyCupsDTO dto = new CompanyCupsDTO();
            dto.setId             ( safeInt(row[0]) );
            dto.setCompanyId      ( safeInt(row[1]) );
            dto.setCompanyCode    ( safeStr(row[2]) );
            dto.setCompanyName    ( safeStr(row[3]) );
            int freeCupsCount = safeInt(row[4]) == null ? 0 : safeInt(row[4]);
            freeCupsCount = (int) (freeCupsCount * monthCount);
            dto.setFreeCupsCount(freeCupsCount);
            dto.setStatus         ( safeInt(row[5]) );
            LocalDateTime lastOrderTime = convertToLocalDateTime(row[6]);
            dto.setLastOrderTime(lastOrderTime);         // 最近支付订单时间
            LocalDateTime modifyTime    = convertToLocalDateTime(row[7]);
            dto.setModifyTime(modifyTime);
            LocalDateTime cupsCreateTime= convertToLocalDateTime(row[8]);
            dto.setCreateTime(cupsCreateTime);           // cof_company_cups记录创建时间

            // 计算统计数据
            int actualCups = actualMap.getOrDefault(dto.getCompanyId(), 0);
            int selfPaidCups = selfPaidMap.getOrDefault(dto.getCompanyId(), 0);
            dto.setActualCups(actualCups);
            dto.setSelfPaidCups(selfPaidCups);

            // 剩余杯数 = freeCupsCount - (actualCups - selfPaidCups)
            int remain = dto.getFreeCupsCount() - (actualCups - selfPaidCups);
            dto.setRemainingCups(remain);

            dtoList.add(dto);
        }

        // 5. 如果要按“最近订单时间”倒序：
        dtoList.sort((d1, d2) -> {
            LocalDateTime t1 = d1.getLastOrderTime();
            LocalDateTime t2 = d2.getLastOrderTime();
            if(t1 == null && t2 == null) return 0;
            if(t1 == null) return 1;
            if(t2 == null) return -1;
            return t2.compareTo(t1);
        });

        // 6. 手动分页
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), dtoList.size());
        List<CompanyCupsDTO> pageContent = dtoList.subList(startIndex, endIndex);

        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }


    private String safeStr(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private Integer safeInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number)obj).intValue();
        }
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 将传入的对象转换为 LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof String) {
            return LocalDateTime.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (value instanceof Number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) value).longValue()), ZoneId.systemDefault());
        }
        throw new IllegalArgumentException("不支持的时间类型: " + value.getClass());
    }

    private Integer convertToInt(Object val) {
        return convertToInt(val, null);
    }

    private Integer convertToInt(Object val, Integer defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private String convertToStr(Object val) {
        return val == null ? null : val.toString();
    }



    @Override
    public List<CompanyCupsDTO> getCompanyCupsForExport(CompanyCupsQueryCriteria criteria) {
        // 1. 设置默认时间范围
        criteria.setDefaultTimeRangeIfEmpty();
        LocalDateTime startDate = criteria.getStartDateTime();
        LocalDateTime endDate   = criteria.getEndDateTime();

        // 1.1 如果需要计算跨月 *freeCupsCount，则可在此计算
        LocalDate startMonth = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        LocalDate endMonth   = LocalDate.of(endDate.getYear(), endDate.getMonth(), 1);
        long monthCount = ChronoUnit.MONTHS.between(startMonth, endMonth) + 1;
        if (monthCount < 1) {
            monthCount = 1;
        }

        // 2. 查询实际杯数 & 自费杯数
        List<Object[]> actualList = companyCupsRepository.countActualCups(startDate, endDate);
        Map<Integer, Integer> actualMap = listToMap(actualList);
        List<Object[]> selfPaidList = companyCupsRepository.countSelfPaidCups(startDate, endDate);
        Map<Integer, Integer> selfPaidMap = listToMap(selfPaidList);

        // 3. 查询套餐记录
        List<Object[]> cupsRecords = companyCupsRepository.findCupsRecords(
                startDate, endDate, criteria.getCompanyName(), criteria.getStatus()
        );

        // 4. 组装 DTO（无分页）
        List<CompanyCupsDTO> dtoList = new ArrayList<>();
        for (Object[] row : cupsRecords) {
            CompanyCupsDTO dto = new CompanyCupsDTO();
            // 约定SELECT字段顺序
            dto.setId             ( safeInt(row[0]) );
            dto.setCompanyId      ( safeInt(row[1]) );
            dto.setCompanyCode    ( safeStr(row[2]) );
            dto.setCompanyName    ( safeStr(row[3]) );

            // 包月杯数可按 monthCount 进行乘法
            int freeCupsCount = safeInt(row[4]) == null ? 0 : safeInt(row[4]);
            freeCupsCount = (int) (freeCupsCount * monthCount);
            dto.setFreeCupsCount(freeCupsCount);

            dto.setStatus         ( safeInt(row[5]) );
            dto.setLastOrderTime  ( convertToLocalDateTime(row[6]) );
            dto.setModifyTime     ( convertToLocalDateTime(row[7]) );
            dto.setCreateTime     ( convertToLocalDateTime(row[8]) );

            // 实际杯数 & 自费杯数
            int actualCups   = actualMap.getOrDefault(dto.getCompanyId(), 0);
            int selfPaidCups = selfPaidMap.getOrDefault(dto.getCompanyId(), 0);
            dto.setActualCups(actualCups);
            dto.setSelfPaidCups(selfPaidCups);

            // 剩余杯数 = freeCupsCount - (actualCups - selfPaidCups)
            int remain = freeCupsCount - (actualCups - selfPaidCups);
            dto.setRemainingCups(remain);

            dtoList.add(dto);
        }

        // 5. 如果需要按“最近订单时间”倒序
        dtoList.sort((d1, d2) -> {
            LocalDateTime t1 = d1.getLastOrderTime();
            LocalDateTime t2 = d2.getLastOrderTime();
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });

        return dtoList;
    }


    private Map<Integer, Integer> listToMap(List<Object[]> list) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] row : list) {
            Integer compId = ((Number) row[0]).intValue();
            Integer count  = ((Number) row[1]).intValue();
            map.put(compId, count);
        }
        return map;
    }



    @Override
    public CompanyCupsDTO addCompanyCups(CompanyCupsDTO companyCupsDTO) {
        // 1. 校验企业状态是否为关闭
        checkCompanyStatus(companyCupsDTO.getCompanyId());
        if (companyCupsRepository.existsByCompanyId(companyCupsDTO.getCompanyId())) {
            throw new IllegalArgumentException("该企业已存在杯量记录，无法重复添加。");
        }

        // 2. 获取企业信息
        CofCompanyInfo company = getCompanyInfoById(companyCupsDTO.getCompanyId());
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        if (company.getStatus() == 0) {
            throw new IllegalArgumentException("企业状态为关闭，无法添加杯量。");
        }

        // 3. 创建 CompanyCups 实体
        CompanyCups cups = convertToEntity(companyCupsDTO);
        cups.setCreateTime(LocalDateTime.now());
        cups.setModifyTime(LocalDateTime.now());
        Integer status = companyCupsDTO.getStatus();
        if (status == null || (status != 0 && status != 1)) {
            status = 1; // 默认值为 1
        }
        cups.setStatus(status);

        cups.setCompanyInfo(company);  // 这里关联了 CofCompanyInfo 实体对象，避免直接在数据库增加外键

        CompanyCups saved = companyCupsRepository.save(cups);

        // 缓存redis
        String companyCupsSetKey = "Company-Set-Cups-" + DateUtil.format(new Date(),"yyyyMM") + "-" + saved.getCompanyId();
        Jedis jedis = RedisPool.getJedisInstance();
        try {
            jedis.select(9);
            jedis.set(companyCupsSetKey, saved.getFreeCupsCount() +"");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedisPool.release(jedis);
        }
        log.info("企业新增杯量缓存：" + companyCupsSetKey + "：" + saved.getFreeCupsCount());

        return toDTO(saved);
    }


    @Override
    public CompanyCupsDTO updateCompanyCups(Integer id, CompanyCupsDTO companyCupsDTO) {
        CompanyCups existing = companyCupsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的杯量记录。"));
        checkCompanyStatus(existing.getCompanyId());
        // 校验企业状态是否为关闭
        CofCompanyInfo company = getCompanyInfoById(existing.getCompanyId());
        if (company.getStatus() == 0) {
            throw new InvalidOperationException("企业状态为关闭，无法编辑杯量。");
        }

        existing.setFreeCupsCount(companyCupsDTO.getFreeCupsCount());
        existing.setStatus(companyCupsDTO.getStatus());
        existing.setModifyTime(LocalDateTime.now());

        CompanyCups saved = companyCupsRepository.save(existing);
        CompanyCupsDTO updatedDTO = toDTO(saved);
        updatedDTO.setCompanyName(company.getCompanyName()); // 设置企业名称
        updatedDTO.setCompanyCode(company.getCompanyCode()); // 确保返回企业编码
        updatedDTO.setId(company.getId());
        // 缓存redis
        String companyCupsSetKey = "Company-Set-Cups-" + DateUtil.format(new Date(),"yyyyMM") + "-" + saved.getCompanyId();
        Jedis jedis = RedisPool.getJedisInstance();
        try {
            jedis.select(9);
            jedis.set(companyCupsSetKey, saved.getFreeCupsCount() +"");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedisPool.release(jedis);
        }

        log.info("企业变更杯量缓存：" + companyCupsSetKey + "：" + saved.getFreeCupsCount());

        return updatedDTO;
    }


    @Override
    public void deleteCompanyCups(Integer id) {
        CompanyCups existing = companyCupsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyCups not found with id " + id));
        existing.setStatus(2); // 设置状态为 2（已删除）
        companyCupsRepository.save(existing); // 保存更新
    }

    @Override
    public Page<CompanyCupsDTO> searchCompanyCups(String companyName, Integer status, Integer period, Pageable
            pageable) {
        return null;
    }

    @Override
    public List<CompanyCupsDTO> searchCompanyCupsByCompanyName(String companyName) {
        return List.of();
    }

    @Override
    @Transactional
    public void toggleStatus(Integer id, Integer status) {
        CompanyCups existing = companyCupsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的杯量记录。"));
        existing.setStatus(status);
        existing.setModifyTime(LocalDateTime.now());
        companyCupsRepository.save(existing);
    }

    @Override
    public Page<CompanyCupsDTO> getCompanyCupsFiltered(String companyName, Integer status, LocalDateTime
            start, LocalDateTime end, Pageable pageable) {
        return null;
    }

    @Override
    public List<CofCompanyInfo> searchCompanies(Integer status) {
        return cofCompanyInfoRepository.findByStatus(1);
    }

    @Override
    public List<CofCompanyInfo> searchCompanyByName(String companyName) {
        return cofCompanyInfoRepository.findByCompanyName(companyName);

    }

    @Override
    public List<Map<String, Object>> queryCofCupsCountPage(CofQueryCriteria criteria, com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> page) {
        criteria.setFirstSize((page.getCurrent() - 1) * page.getSize());
        criteria.setLastSize(page.getSize());
        if (criteria.getCreateTimeYearMonth() == null || criteria.getCreateTimeYearMonth().size() == 0) {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            // 获取当前日期向前一个月的日期
            LocalDate oneMonthAgo = today.minusMonths(1);
            // 创建一个空的Timestamp列表
            List<String> timestamps = new ArrayList<>();
            // 将LocalDate转换为Timestamp并添加到列表中
            timestamps.add(String.valueOf(oneMonthAgo.atStartOfDay()));
            timestamps.add(String.valueOf(today.atStartOfDay()));
            criteria.setCreateTimeYearMonth(timestamps);
        }else{
            List<String> resultList = new ArrayList<>();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 解析年月字符串为 YearMonth 对象
            YearMonth yearMonthObjFirst = YearMonth.parse(criteria.getCreateTimeYearMonth().get(0), inputFormatter);
            YearMonth yearMonthObjLast = YearMonth.parse(criteria.getCreateTimeYearMonth().get(1), inputFormatter);

            // 获取该月的第一天
            LocalDate firstDayOfMonthF = yearMonthObjFirst.atDay(1);
            LocalDate firstDayOfMonthL = yearMonthObjLast.plusMonths(1).atDay(1);

            // 格式化为 "yyyy-MM-dd" 并添加到结果列表
            resultList.add(firstDayOfMonthF.format(outputFormatter));
            resultList.add(firstDayOfMonthL.format(outputFormatter));

            criteria.setCreateTimeYearMonth(resultList);
        }

        List<Map<String, Object>> infoList = companyCupsRepository.queryCofCupsCountPage(criteria);
        if (infoList.size() == 0) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> dealInfos = new ArrayList<>(infoList.size());
        for (Map<String, Object> cupsCountMap : infoList) {
            Map<String, Object> dealMap = new HashMap<>();
            dealMap.put("id", cupsCountMap.get("id"));
            dealMap.put("companyName", cupsCountMap.get("companyName"));
            dealMap.put("mobile", cupsCountMap.get("mobile"));
            dealMap.put("userName", cupsCountMap.get("userName"));
            //包月
            dealMap.put("baoYueBeiliang", cupsCountMap.get("baoYueBeiliang"));
            //剩余
            dealMap.put("surplusBeiliang", cupsCountMap.get("surplusBeiliang"));
            //实际
            dealMap.put("actualBeiliang", cupsCountMap.get("actualBeiliang"));
            //自费
            dealMap.put("ziFeiBeiliang", cupsCountMap.get("ziFeiBeiliang"));

            dealInfos.add(dealMap);
        }
        return dealInfos;
    }

    @Override
    public Integer queryCofCupsCountTotal(CofQueryCriteria criteria) {
        return this.companyCupsRepository.queryCofCupsCountTotal(criteria);
    }

    @Override
    public void downloadCofCupsCount(CofQueryCriteria criteria, HttpServletResponse response) throws IOException {
        if (criteria.getCreateTimeYearMonth() == null || criteria.getCreateTimeYearMonth().size() == 0) {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            // 获取当前日期向前一个月的日期
            LocalDate oneMonthAgo = today.minusMonths(1);
            // 创建一个空的Timestamp列表
            List<String> timestamps = new ArrayList<>();
            // 将LocalDate转换为Timestamp并添加到列表中
            timestamps.add(String.valueOf(oneMonthAgo.atStartOfDay()));
            timestamps.add(String.valueOf(today.atStartOfDay()));
            criteria.setCreateTimeYearMonth(timestamps);
        }else{
            List<String> resultList = new ArrayList<>();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 解析年月字符串为 YearMonth 对象
            YearMonth yearMonthObjFirst = YearMonth.parse(criteria.getCreateTimeYearMonth().get(0), inputFormatter);
            YearMonth yearMonthObjLast = YearMonth.parse(criteria.getCreateTimeYearMonth().get(1), inputFormatter);

            // 获取该月的第一天
            LocalDate firstDayOfMonthF = yearMonthObjFirst.atDay(1);
            LocalDate firstDayOfMonthL = yearMonthObjLast.plusMonths(1).atEndOfMonth();

            // 格式化为 "yyyy-MM-dd" 并添加到结果列表
            resultList.add(firstDayOfMonthF.format(outputFormatter));
            resultList.add(firstDayOfMonthL.format(outputFormatter));

            criteria.setCreateTimeYearMonth(resultList);
        }
        List<Map<String, Object>> downloadList = new ArrayList<>();
        List<Map<String, Object>> infoList = companyCupsRepository.queryCofCupsCount(criteria);
        if (infoList.size() == 0) {
            return;
        }
        int i = 1;
        for (Map<String, Object> orderMap : infoList) {
            Map<String, Object> dealMap = new LinkedHashMap<>();
            dealMap.put("序号", i);
            dealMap.put("企业名称", orderMap.get("companyName"));
            dealMap.put("企业编号", orderMap.get("companyId"));
            dealMap.put("姓名", orderMap.get("userName"));
            dealMap.put("手机号", orderMap.get("mobile"));
            dealMap.put("包月杯量", orderMap.get("baoYueBeiliang"));
            Object surplusBeiliangObj = orderMap.get("surplusBeiliang");
            dealMap.put("剩余包月杯量", Integer.parseInt(surplusBeiliangObj.toString()));
            dealMap.put("实际杯数", orderMap.get("actualBeiliang"));
            Object ziFeiBeiliangObj = orderMap.get("ziFeiBeiliang");
            dealMap.put("自费杯数", Integer.parseInt(ziFeiBeiliangObj.toString()));
            downloadList.add(dealMap);
            i++;
        }
        FileUtil.downloadExcel(downloadList, response);
    }

    private CompanyCups convertToEntity(CompanyCupsDTO dto) {
        CompanyCups companyCups = new CompanyCups();
        companyCups.setCompanyId(dto.getCompanyId());
        companyCups.setFreeCupsCount(dto.getFreeCupsCount());
        companyCups.setStatus(dto.getStatus());
        companyCups.setPeriod(dto.getPeriod());
        return companyCups;
    }

    @Override
    public CompanyCupsDTO getCompanyCupsByCompanyId(Integer companyId) {
        return null;
    }
}
