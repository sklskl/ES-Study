package ls.tech.modules.companyCups.rest;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import ls.tech.annotation.AnonymousAccess;
import ls.tech.annotation.Log;
import ls.tech.modules.ResponseDTO;
import ls.tech.modules.companyCups.domain.CofCompanyInfo;
import ls.tech.modules.companyCups.domain.CompanyCupsDTO;
import ls.tech.modules.companyCups.domain.criteria.CofQueryCriteria;
import ls.tech.modules.companyCups.domain.criteria.CompanyCupsQueryCriteria;
import ls.tech.modules.companyCups.service.CompanyCupsService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: coffee_con
 * @ClassName: CompanyCupsController
 * @author: skl
 * @create: 2025-01-04 09:47
 */
@RestController
@RequestMapping("/api/company-cups")
@Api(tags = "咖啡机杯量管理")
@Slf4j
public class CompanyCupsController {

    @Autowired
    private CompanyCupsService companyCupsService;

    @GetMapping
    @PreAuthorize("@el.check('companyCups:list')")
    @ApiOperation(value = "查询企业杯量")
    public Map<String, Object> getCompanyCups(CompanyCupsQueryCriteria queryCriteria, // 自定义的查询条件
                                                                                    Pageable pageable) {                   // Spring Data 分页对象
        // 如果没有提供时间区间，则默认查询当月的数据
        queryCriteria.setDefaultTimeRangeIfEmpty();

        // 让 Service 根据 queryCriteria + pageable 分页查询
        Page<CompanyCupsDTO> cupsPage = companyCupsService.getCompanyCups(queryCriteria, pageable);

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("content", cupsPage.getContent());
        response.put("totalElements", cupsPage.getTotalElements());
        return response;
    }



    /**
     * 新增企业杯量
     */
    @PostMapping
    @PreAuthorize("@el.check('companyCups:add')")
    @ApiOperation(value = "新增企业杯量")
    public ResponseDTO<CompanyCupsDTO> addCompanyCups(@Valid @RequestBody CompanyCupsDTO companyCupsDTO) {
        CompanyCupsDTO created = companyCupsService.addCompanyCups(companyCupsDTO);
        return ResponseDTO.success(created);
    }

    /**
     * 编辑企业杯量
     */
    @PutMapping
    @ApiOperation(value = "编辑企业杯量")
    @PreAuthorize("@el.check('companyCups:edit')")
    public ResponseDTO<CompanyCupsDTO> updateCompanyCups(@Valid @RequestBody CompanyCupsDTO companyCupsDTO) {
        Integer id = companyCupsDTO.getId();
        CompanyCupsDTO updated = companyCupsService.updateCompanyCups(id, companyCupsDTO);
        return ResponseDTO.success(updated);
    }

    /**
     * 删除企业杯量
     */
    @DeleteMapping
    @ApiOperation(value = "删除企业杯量")
    @PreAuthorize("@el.check('companyCups:delete')")
    public ResponseDTO<String> deleteCompanyCups(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("要删除的ID列表不能为空");
        }
        for (Integer id : ids) {
            companyCupsService.deleteCompanyCups(id);
        }
        return ResponseDTO.success("批量删除成功");
    }


    //下载
    @GetMapping("/download")
    @PreAuthorize("@el.check('companyCups:list')")
    @ApiOperation(value = "下载企业杯量")
    public void exportCompanyCups(
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "timeRanges", required = false) List<String> timeRanges,
            HttpServletResponse response
    ) throws IOException {
        // 1. 创建查询条件
        CompanyCupsQueryCriteria criteria = new CompanyCupsQueryCriteria();
        criteria.setCompanyName(companyName);
        criteria.setStatus(status);
        criteria.setTimeRanges(timeRanges);

        // 2. 调用“不分页”的查询方法，获取全部记录
        List<CompanyCupsDTO> dtos = companyCupsService.getCompanyCupsForExport(criteria);

        // 3. 使用 Apache POI/Streaming API 导出
        Workbook workbook = new SXSSFWorkbook(); // 使用流式 API
        Sheet sheet = workbook.createSheet("企业杯量");

        // 3.1 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("企业名称");
        header.createCell(1).setCellValue("企业编码");
        header.createCell(2).setCellValue("包月杯数");
        header.createCell(3).setCellValue("当前剩余包月杯数");
        header.createCell(4).setCellValue("实际杯数");
        header.createCell(5).setCellValue("自费杯数");
        header.createCell(6).setCellValue("状态");
        header.createCell(7).setCellValue("创建时间");

        // 表头样式（加粗）
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        for (int i = 0; i <= 7; i++) {
            header.getCell(i).setCellStyle(headerStyle);
        }

        // 日期格式样式
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        // 3.2 填充数据
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (CompanyCupsDTO dto : dtos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getCompanyName() != null ? dto.getCompanyName() : "");
            row.createCell(1).setCellValue(dto.getCompanyCode() != null ? dto.getCompanyCode() : "");
            row.createCell(2).setCellValue(dto.getFreeCupsCount() == null ? 0 : dto.getFreeCupsCount());
            row.createCell(3).setCellValue(dto.getRemainingCups() == null ? 0 : dto.getRemainingCups());
            row.createCell(4).setCellValue(dto.getActualCups() == null ? 0 : dto.getActualCups());
            row.createCell(5).setCellValue(dto.getSelfPaidCups() == null ? 0 : dto.getSelfPaidCups());

            String statusDescription = getStatusDescription(dto.getStatus());
            row.createCell(6).setCellValue(statusDescription);

            if (dto.getCreateTime() != null) {
                Cell createTimeCell = row.createCell(7);
                createTimeCell.setCellValue(dto.getCreateTime().format(dateFormatter));
                createTimeCell.setCellStyle(dateStyle);
            } else {
                row.createCell(7).setCellValue("");
            }
        }

        // 4. 设置响应头并写出到浏览器
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("company_cups_export.xlsx", StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error("生成 Excel 文件时发生错误: {}", e.getMessage(), e);
            throw new IOException("生成 Excel 文件时发生错误。", e);
        } finally {
            workbook.close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose(); // 删除临时文件
            }
        }
    }

    private String getStatusDescription(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1:
                return "正常";
            case 0:
                return "无效";
            case 2:
                return "已删除"; // 如果需要，也可以处理 status = 2 的情况
            default:
                return "未知";
        }
    }
    @PostMapping("/toggle-status")
    @AnonymousAccess
    @ApiOperation(value = "切换状态")
    public ResponseDTO<String> toggleStatus
        (@RequestParam Integer id, @RequestParam Integer status) {
        companyCupsService.toggleStatus(id, status);
        return ResponseDTO.success("状态切换成功");
    }

    /**
     * 查找企业接口
     * @param status 企业状态
     * @return 企业信息列表
     */
    @GetMapping("/search")
    @ApiOperation(value = "查找企业")
    @PreAuthorize("@el.check('companyCups:list')")
    public List<CofCompanyInfo> searchCompanies(@RequestParam(required = false) Integer status) {
        return companyCupsService.searchCompanies( status);
    }

    @GetMapping(value = "/staffCupsCount/query")
    @Log("员工杯量统计信息")
    @ApiOperation("员工杯量统计信息")
    @PreAuthorize("@el.check('cofOrder:query')")
    public ResponseEntity<Object> queryCofCupsCount(CofQueryCriteria criteria, com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> page) {
        Map<String, Object> resultMap = ImmutableMap.of(
                "content", companyCupsService.queryCofCupsCountPage(criteria, page),
                "totalElements", companyCupsService.queryCofCupsCountTotal(criteria)
        );
        return ResponseEntity.ok(resultMap);
    }

    @Log("员工杯量统计信息导出")
    @ApiOperation("员工杯量统计信息导出")
    @GetMapping(value = "/staffCupsCount/query/download")
    @PreAuthorize("@el.check('cofOrder:query')")
    public void downloadCofCupsCount(HttpServletResponse response, CofQueryCriteria criteria) {
        try {
            companyCupsService.downloadCofCupsCount(criteria, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}