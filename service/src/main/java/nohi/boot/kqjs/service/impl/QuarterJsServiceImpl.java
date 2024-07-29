package nohi.boot.kqjs.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.dto.page.Page;
import nohi.boot.common.utils.DateUtils;
import nohi.boot.common.utils.ExcelUtils;
import nohi.boot.demo.utils.PageUtils;
import nohi.boot.kqjs.config.JsKqConfig;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.entity.QuarterJs;
import nohi.boot.kqjs.mapper.QuarterJsMapper;
import nohi.boot.kqjs.service.IQuarterJsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    private QuarterJsMapper mapper;
    @Autowired
    private JsKqConfig jsKqConfig;

    public static final String MONTH_DAY = "M/d";

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
                ExcelUtils.setValue(cell, titleList.get(cellIndex));
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
                ExcelUtils.setValue(row.getCell(0), item.get("projectName"));
                ExcelUtils.setValue(row.getCell(1), item.get("userName"));
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
                        ExcelUtils.setValue(cell, bd);
                    } else {
                        ExcelUtils.setValue(cell, null);
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

    private Map<String, Object> columnMap(String value, String lable) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("value", value);
        map.put("label", lable);
        map.put("isLeaf", "Y");
        return map;
    }

    /**
     * 月考勤数据
     *
     * @param info 查询对象
     * @param page 分页
     * @return 结果
     */
    public RespMeta<MonthData> monthData(KqQueryDto info, Page page) {
        // 处理用户名
        if (StringUtils.isNotBlank(info.getUsername())) {
            info.setUsername("'" + Joiner.on("','").join(info.getUsername().split(",|，")) + "'");
        }

        LocalDate start = nohi.boot.common.utils.DateUtils.stringToLocalDate(info.getStartDate());
        LocalDate end = nohi.boot.common.utils.DateUtils.stringToLocalDate(info.getEndDate());
        int startMonth = start.getMonthValue();
        int endMonth = end.getMonthValue();
        if (endMonth < startMonth) {
            endMonth += 12;
        }


        // 获取列属性
        // 拼语句
        StringBuilder sb = new StringBuilder();

        List<Map<String, ?>> columnList = Lists.newArrayList();
        columnList.add(this.columnMap("USERNAME", "姓名"));

        Map<String, Object> map = this.columnMap("", "出勤天数(当天上班算1)");
        columnList.add(map);
        map.put("isLeaf", "N");
        List<Map<String, ?>> itemList = Lists.newArrayList();
        map.put("subList", itemList);
        // 出勤天数
        for (int i = startMonth; i <= endMonth; i++) {
            int mon = i > 12 ? startMonth - 12 : i;
            itemList.add(this.columnMap(mon + "月出勤", mon + "月"));

            sb.append(String.format(", max(case when T.m = '%s' then T.出勤天数 else null end) as '%s'", mon, mon + "月出勤"));
        }
        // 结算人天
        map = this.columnMap("", "结算人天");
        columnList.add(map);
        map.put("isLeaf", "N");
        itemList = Lists.newArrayList();
        map.put("subList", itemList);
        for (int i = startMonth; i <= endMonth; i++) {
            int mon = i > 12 ? startMonth - 12 : i;
            itemList.add(this.columnMap(mon + "月人天", mon + "月"));
            sb.append(String.format(", max(case when T.m = '%s' then T.人天 else null end) as '%s'", mon, mon + "月人天"));
        }
        info.setRsColsSql(sb.toString());

        // 响应对象
        RespMeta<MonthData> resp = new RespMeta<>();
        MonthData data = new MonthData();
        data.setColumnList(columnList);
        if (null != page) {
            log.info("分页查询:{}", JSONObject.toJSONString(page));
            PageHelper.startPage(page.getPageIndex(), page.getPageSize());
            List<?> list = mapper.monthData(info);
            data.setDataList(PageUtils.getData(list));

            resp.setPage(PageUtils.getPages(list, page));
            resp.setData(data);
        } else {
            List<?> list = mapper.monthData(info);
            data.setDataList(PageUtils.getData(list));

            resp.setData(data);
        }
        return resp;
    }

    /**
     * 导出月考勤数据
     *
     * @param info 查询对象
     */
    public RespMeta<String> exportMonthData(KqQueryDto info) {
        /** 查询数据 **/
        RespMeta<MonthData> respMeta = this.monthData(info, null);

        /** 导出 **/
        String filename = DateUtils.getNow().getTime() + ".xlsx";
        String template = "templates/monthData.xlsx";
        String outputFile = jsKqConfig.getFilePath() + File.separator + filename;
        RespMeta<String> exportRespMeta = new RespMeta();
        exportRespMeta.setData(outputFile);

        // 2,读取Excel模板
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
             FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            // 读取Excel模板
            Workbook templatebook = WorkbookFactory.create(is);
            Sheet templateSheet = templatebook.getSheetAt(0);

            //  表头处理
            /** 表头 **/
            // 需要处理合并单元格问题
            List<Map<String, Object>> columnList = (List<Map<String, Object>>) respMeta.getData().getColumnList();
            // excel列表属性及属性对应值
            List<Map<String, Object>> excelColItemList = Lists.newArrayList();

            Row firstRow = templateSheet.getRow(0);
            if (null == firstRow) {
                firstRow = templateSheet.createRow(0);
            }

            Row secondRow = templateSheet.getRow(1);
            if (null == secondRow) {
                secondRow = templateSheet.createRow(1);
            }

            // 按列表获取列头key
            for (Map<String, Object> columnItem : columnList) {
                // 叶子节点直接添加
                if ("Y".equalsIgnoreCase((String) columnItem.get("isLeaf"))) {
                    // 合并单元格
                    log.info("表头个数:[{}] 合并单元格:[{} {} {} {}]", excelColItemList.size(), 0, 1, excelColItemList.size(), excelColItemList.size());
                    CellRangeAddress region = new CellRangeAddress(0, 1, excelColItemList.size(), excelColItemList.size());
                    templateSheet.addMergedRegion(region);

                    log.info("第一行表头:[{}]:{}", excelColItemList.size(), columnItem.get("label"));
                    Cell thisCell = firstRow.getCell(excelColItemList.size(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 第一列不需要拷贝
                    if (!excelColItemList.isEmpty()) {
                        ExcelUtils.copyCellStyle(firstRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), thisCell);
                    }
                    ExcelUtils.setValue(thisCell, columnItem.get("label"));
                    excelColItemList.add(columnItem);
                } else {
                    // 子列表
                    List<Map<String, Object>> subList = (List<Map<String, Object>>) columnItem.get("subList");
                    if (subList.size() > 1) {
                        // 合并单元格：合并第一行
                        CellRangeAddress region = new CellRangeAddress(0, 0, excelColItemList.size(), excelColItemList.size() + subList.size() - 1);
                        templateSheet.addMergedRegion(region);
                    }
                    // 第一行单元格表头
                    log.info("第一行表头:[{}]:{}", excelColItemList.size(), columnItem.get("label"));
                    Cell thisCell = firstRow.getCell(excelColItemList.size(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 第一列不需要拷贝
                    if (!excelColItemList.isEmpty()) {
                        ExcelUtils.copyCellStyle(firstRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), thisCell);
                    }
                    ExcelUtils.setValue(thisCell, columnItem.get("label"));

                    for (Map<String, Object> subColumnItem : subList) {
                        // 第二行单元格表头
                        log.info("第二行表头:[{}]:{}", excelColItemList.size(), subColumnItem.get("label"));
                        thisCell = secondRow.getCell(excelColItemList.size(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        // 第一列不需要拷贝
                        if (!excelColItemList.isEmpty()) {
                            ExcelUtils.copyCellStyle(firstRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), thisCell);
                        }

                        ExcelUtils.setValue(thisCell, subColumnItem.get("label"));
                        excelColItemList.add(subColumnItem);
                    }
                }
            }

            log.info("表头:{}", JSONObject.toJSONString(excelColItemList));

            // 导出数据
            List<Map<String, Object>> list = (List<Map<String, Object>>) respMeta.getData().getDataList();

            // 行偏移量：偏移表头
            int rowOffSet = 2;
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> dataItem = list.get(i);
                // excel第二行，start with 0
                // 行索引，前两行表表头，所以需要 + rowOffSet
                int rowIndex = i + rowOffSet;
                Row row = templateSheet.getRow(rowIndex);
                if (null == row) {
                    row = templateSheet.createRow(rowIndex);
                }

                // 大于第一行时拷贝样式
//                if (i > 0) {
//                    ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, rowOffSet), row);
//                }
                for (int colIndex = 0; colIndex < excelColItemList.size(); colIndex++) {
                    Map<String, Object> colTitleItem = excelColItemList.get(colIndex);
                    String value = (String) colTitleItem.get("value");
                    ExcelUtils.setValue(row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), dataItem.get(value));
                }

                templateSheet.setForceFormulaRecalculation(true);
            }

            // 生成文件
            templatebook.write(fos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exportRespMeta.setResCode("1");
            exportRespMeta.setResMsg("导出文件异常:" + e.getMessage());
        }

        return exportRespMeta;
    }

    /**
     * 月考勤数据
     *
     * @param info 查询对象
     * @return 结果
     */
    public RespMeta<MonthData> monthDataDetail(KqQueryDto info) {
        // 处理用户名
        if (StringUtils.isNotBlank(info.getUsername())) {
            info.setUsername("'" + Joiner.on("','").join(info.getUsername().split(",|，")) + "'");
        }

        if (StringUtils.isNotBlank(info.getProject())) {
            info.setProject("'" + Joiner.on("','").join(info.getProject().split(",|，")) + "'");
        }

        // 日期范围
        LocalDate start = nohi.boot.common.utils.DateUtils.stringToLocalDate(info.getStartDate());
        LocalDate end = nohi.boot.common.utils.DateUtils.stringToLocalDate(info.getEndDate());
        log.info("日期范围:[{}-{}]", start, end);
        /** 获取列表头 **/
        List<Map<String, ?>> columnList = this.columnList(start, end);
        columnList.add(0, this.columnMap("USERNAME", "姓名"));
        if (info.isByProject()) {
            columnList.add(0, this.columnMap("PROJECT", "项目"));
        }
        columnList.add(this.columnMap("TOTAL", "汇总"));

        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.monthDataDetail(info);

        // 按用户转换数据
        List<Map<String, ?>> rsDataList = this.convertData(allDataList, info.isByProject());

        // 响应对象
        RespMeta<MonthData> resp = new RespMeta<>();
        MonthData data = new MonthData();
        data.setColumnList(columnList);
        // 数据对象
        data.setDataList(rsDataList);

        resp.setData(data);
        return resp;
    }

    /**
     * 月考勤数据明细
     *
     * @param info 查询对象
     * @return 结果
     */
    public RespMeta<String> exportMonthDataDetail(KqQueryDto info) {
        /** 查询数据 **/
        RespMeta<MonthData> respMeta = this.monthDataDetail(info);

        /** 导出 **/
        String filename = DateUtils.getNow().getTime() + ".xlsx";
        String template = "templates/monthDataDetail.xlsx";
        String outputFile = jsKqConfig.getFilePath() + File.separator + filename;
        RespMeta<String> exportRespMeta = new RespMeta();
        exportRespMeta.setData(outputFile);

        // 2,读取Excel模板
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
             FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            // 读取Excel模板
            Workbook templatebook = WorkbookFactory.create(is);
            Sheet templateSheet = templatebook.getSheetAt(0);

            // 需要处理合并单元格问题
            List<Map<String, Object>> columnList = (List<Map<String, Object>>) respMeta.getData().getColumnList();
            Row firstRow = templateSheet.getRow(0);
            if (null == firstRow) {
                firstRow = templateSheet.createRow(0);
            }
            log.info("表头:{}", JSONObject.toJSONString(columnList));

            /** 表头 **/
            // 按列表获取列头key
            int cellIndex = 0;
            for (Map<String, Object> columnItem : columnList) {
                Cell cell = firstRow.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                if (cellIndex > 1) {
                    // 拷贝样式
                    cell.setCellStyle(firstRow.getCell(1).getCellStyle());
                    templateSheet.setColumnWidth(cellIndex, templateSheet.getColumnWidth(1));
                }
                ExcelUtils.setValue(cell, columnItem.get("label"));
                cellIndex++;
            }

            // 导出数据
            List<Map<String, Object>> list = (List<Map<String, Object>>) respMeta.getData().getDataList();

            // 行偏移量：偏移表头
            int rowOffSet = 1;
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> dataItem = list.get(i);
                // excel第二行，start with 0
                // 行索引，前两行表表头，所以需要 + rowOffSet
                int rowIndex = i + rowOffSet;
                Row row = templateSheet.getRow(rowIndex);
                if (null == row) {
                    row = templateSheet.createRow(rowIndex);
                }

                // 大于第一行时拷贝样式
//                if (i > 0) {
//                    ExcelUtils.setRowStyle(ExcelUtils.getRow(templateSheet, rowOffSet), row);
//                }
                for (int colIndex = 0; colIndex < columnList.size(); colIndex++) {
                    Map<String, Object> colTitleItem = columnList.get(colIndex);
                    String value = (String) colTitleItem.get("value");
                    ExcelUtils.setValue(row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), dataItem.get(value));
                }

                templateSheet.setForceFormulaRecalculation(true);
            }

            // 生成文件
            templatebook.write(fos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exportRespMeta.setResCode("1");
            exportRespMeta.setResMsg("导出文件异常:" + e.getMessage());
        }
        return exportRespMeta;
    }

    /**
     * 转换考勤数据
     *
     * @param allDataList 考勤数据
     * @param byProject   是否按项目统计
     */
    private List<Map<String, ?>> convertData(List<Map<String, ?>> allDataList, boolean byProject) {
        // 行数据
        Map<String, Map<String, Object>> allUserData = Maps.newHashMap();

        for (Map<String, ?> dateItem : allDataList) {
            String userName = (String) dateItem.get("USERNAME");
            String project = (String) dateItem.get("PROJECT");
            String key = byProject ? project + "-" + userName : userName;
            Map<String, Object> userData = allUserData.getOrDefault(key, Maps.newHashMap());
            allUserData.put(key, userData);

            // 存放表一天
            LocalDate date = nohi.boot.common.utils.DateUtils.date2LocalDate((Date) dateItem.get("WORK_DATE"));
            BigDecimal daysJs = (BigDecimal) dateItem.get("DAYS_JS");
            BigDecimal total = (BigDecimal) userData.getOrDefault("TOTAL", BigDecimal.ZERO);
            total = total.add(daysJs);

            userData.put(nohi.boot.common.utils.DateUtils.localDateFormat(date, MONTH_DAY) + "_INFO", dateItem);
            userData.put("TOTAL", total);
            userData.put("USERNAME", userName);
            userData.put("PROJECT", project);
            userData.put(nohi.boot.common.utils.DateUtils.localDateFormat(date, MONTH_DAY), daysJs);
        }

        List<Map<String, ?>> rsList = Lists.newArrayList();
        allUserData.entrySet().stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())).forEach(item -> {
            rsList.add(item.getValue());
        });

        return rsList;
    }

    /**
     * 根据日期获取动态列表表头
     *
     * @param start 开始日期
     * @param end   结束日期
     */
    private List<Map<String, ?>> columnList(LocalDate start, LocalDate end) {
        List<Map<String, ?>> columnList = Lists.newArrayList();
        for (; !start.isAfter(end); ) {
            String date = nohi.boot.common.utils.DateUtils.localDateFormat(start, MONTH_DAY);
            Map<String, Object> map = this.columnMap(date, date);
            if (start.getDayOfWeek().getValue() > 5) {
                map.put("TABLE_HEAD_CSS", "WEEKEND");
            }
            columnList.add(map);
            // 增加一天
            start = start.plus(1, ChronoUnit.DAYS);
        }
        return columnList;
    }

    /**
     * 判断人、工作日是否重复
     */
    public RespMeta<List<Map<String, ?>>> userWorkRepeat(KqQueryDto info) {
        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.userWorkRepeat(info);
        // 响应对象
        RespMeta<List<Map<String, ?>>> resp = new RespMeta<>();
        // 数据对象
        resp.setData(allDataList);
        return resp;
    }

    /**
     * 结算考勤有，月份考勤无
     */
    public RespMeta<List<Map<String, ?>>> jsWithOutApp(KqQueryDto info) {
        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.jsWithOutApp(info);
        // 响应对象
        RespMeta<List<Map<String, ?>>> resp = new RespMeta<>();
        // 数据对象
        resp.setData(allDataList);
        return resp;
    }

    /**
     * 月份考勤有，结算考勤无
     */
    public RespMeta<List<Map<String, ?>>> appWithOutJs(KqQueryDto info) {
        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.appWithOutJs(info);
        // 响应对象
        RespMeta<List<Map<String, ?>>> resp = new RespMeta<>();
        // 数据对象
        resp.setData(allDataList);
        return resp;
    }

    /**
     * app与结算考勤不一致
     */
    public RespMeta<List<Map<String, ?>>> daysDiff(KqQueryDto info) {
        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.daysDiff(info);
        // 响应对象
        RespMeta<List<Map<String, ?>>> resp = new RespMeta<>();
        // 数据对象
        resp.setData(allDataList);
        return resp;
    }
}
