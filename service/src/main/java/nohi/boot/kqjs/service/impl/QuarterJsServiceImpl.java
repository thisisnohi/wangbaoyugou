package nohi.boot.kqjs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.utils.DateUtils;
import nohi.boot.common.utils.ExcelUtils;
import nohi.boot.kqjs.entity.QuarterJs;
import nohi.boot.kqjs.mapper.QuarterJsMapper;
import nohi.boot.kqjs.service.IQuarterJsService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 季度结算 服务实现类
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
@Service
@Slf4j
public class QuarterJsServiceImpl extends ServiceImpl<QuarterJsMapper, QuarterJs> implements IQuarterJsService {

    /**
     * 查询项目人员信息
     *
     * @param workDateFrom 开始日期
     * @param workDateTo   结束日期
     */
    public List<Map<String, Object>> projectWorkSheet(String workDateFrom, String workDateTo) {
        List<Map<String, Object>> projectSheetList = Lists.newArrayList();
        // 查询所有数据
        QuarterJs info = new QuarterJs();
        info.setWorkDayStart(workDateFrom);
        info.setWorkDayEnd(workDateTo);
        List<QuarterJs> list = this.baseMapper.selectByExample(info);
        log.info("项目人员信息记录:{}", list.size());

        Map<String, Map<String, Object>> keyMap = Maps.newHashMap();
        for (QuarterJs item : list) {
            String key = item.getProject() + "-" + item.getUsername();
            Map<String, Object> map = keyMap.getOrDefault(key, Maps.newHashMap());
            keyMap.put(key, map);
            map.put("projectName", item.getProject());
            map.put("userName", item.getUsername());
            map.put(item.getWorkDate().toString(), item.getDaysJs());
        }
        keyMap.entrySet().stream().sorted((a, b) -> {
            if (null == a || null == a.getKey()) {
                return -1;
            } else if (null == b || null == b.getKey()) {
                return 1;
            } else {
                return a.getKey().compareTo(b.getKey());
            }
        }).forEach(item -> {
            projectSheetList.add(item.getValue());
        });
        return projectSheetList;
    }

    /**
     * 导出
     *
     * @param workDateFrom 开始日期
     * @param workDateTo   结束日期
     */
    public void export(String workDateFrom, String workDateTo) {
        // 获取查询列表
        List<Map<String, Object>> projectSheetList = this.projectWorkSheet(workDateFrom, workDateTo);
        //
        String filename = "ABC.xlsx";
        String template = "templates/project_worksheet.xlsx";
        String outputFile = Paths.get(System.getProperty("user.dir")) + File.separator + filename;

        log.info("template:{} outputFile:{}", template, outputFile);

        //2,读取Excel模板
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
             FileOutputStream fos = new FileOutputStream(outputFile);
             ) {
            // 读取Excel模板
            Workbook templatebook = WorkbookFactory.create(is);
            Sheet templateSheet = templatebook.getSheetAt(0);

            // title
            List<String> titleList = this.getTitle(workDateFrom, workDateTo);
            log.debug("titleList:{}", titleList);
            Row titleRow = templateSheet.getRow(0);
            for (int cellIndex = 0; cellIndex < titleList.size(); cellIndex++) {
                Cell cell = titleRow.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cellIndex > 2) {
                    // 拷贝样式
                    cell.setCellStyle(titleRow.getCell(2).getCellStyle());
                }
                setValue(cell, titleList.get(cellIndex));
            }
            // 总天数：title - 5 (项目号、项目名、员工号、员工名、汇总)
            int totalDays = titleList.size() - 3;
            // 开始日期
            LocalDate startDate = DateUtils.stringToLocalDate(workDateFrom);
            log.debug("开始日期:{},总天数:{}", startDate, totalDays);

            // 导出数据
            for (int i = 0; i < projectSheetList.size(); i++) {
                Map item = projectSheetList.get(i);
                // excel第二行，start with 0
                Row row = templateSheet.getRow(i + 1);

                // 大于第一行时拷贝样式
                if (i > 0) {
                    if (row == null) {
                        row = templateSheet.createRow(i + 1);
                    }
                    ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, 1), row);
                }
                setValue(row.getCell(0), item.get("projectName"));
                setValue(row.getCell(1), item.get("userName"));
                // 日期
                String columnLetter = null;
                LocalDate tmp = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
                for (int dayIndex = 0; dayIndex < totalDays; dayIndex++) {
                    Cell cell = row.getCell(2 + dayIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i == 0) {
                        // 拷贝样式
                        cell.setCellStyle(row.getCell(2).getCellStyle());
                    }
                    Object wd = item.get(tmp.toString());
                    if (null != wd && !(wd instanceof String)) {
                        BigDecimal bd = (BigDecimal) wd;
                        setValue(cell, bd);
                    } else {
                        setValue(cell, null);
                    }
                    tmp = tmp.plusDays(1);
                    columnLetter = CellReference.convertNumToColString(2 + dayIndex);
                }
                // 设置汇总
                if (i == 0) {
                    // 最后一列为汇总
                    Cell cell = row.getCell(titleList.size() - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 拷贝样式
                    cell.setCellStyle(row.getCell(2).getCellStyle());
                    cell.setCellFormula("SUM(C2:" + columnLetter + "2)");
                }
                templateSheet.setForceFormulaRecalculation(true);
            }

            // 生成文件
            templatebook.write(fos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * Excel标题
     */
    public List<String> getTitle(String workDateFrom, String workDateTo) {
        List<String> title = Lists.newArrayList();
        title.addAll(Lists.newArrayList("项目名", "员工名"));
        // 根据开始结束日期计算列数
        LocalDate start = DateUtils.stringToLocalDate(workDateFrom);
        LocalDate end = DateUtils.stringToLocalDate(workDateTo);
        while (!end.isBefore(start)) {
            title.add(DateUtils.localDateFormat(start, "M.d"));
            start = start.plusDays(1);
        }
        title.add("汇总");
        return title;
    }

    /**
     * 取得值
     *
     * @return
     */
    public static void setValue(Cell cell, Object rs) {
        if (rs == null) {
            cell.setCellValue("");
            return;
        }
        if (rs instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) rs).doubleValue());
        } else if (rs instanceof Date) {
            cell.setCellValue((Date) rs);
        } else if (rs instanceof Timestamp) {
            cell.setCellValue((Timestamp) rs);
        } else if (rs instanceof Integer) {
            cell.setCellValue((Integer) rs);
        } else {
            cell.setCellValue(rs.toString());
        }
    }
}
