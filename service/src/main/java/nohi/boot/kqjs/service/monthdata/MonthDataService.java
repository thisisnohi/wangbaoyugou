package nohi.boot.kqjs.service.monthdata;


import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.dto.page.Page;
import nohi.boot.demo.utils.DateUtils;
import nohi.boot.demo.utils.PageUtils;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.mapper.AppKqMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>月考勤</p>
 * @date 2024/07/24 16:11
 **/
@Slf4j
@Service
public class MonthDataService {

    @Autowired
    private AppKqMapper mapper;

    public static final String MONTH_DAY = "M/d";

    /**
     * 大楼门禁、移动APP数据对比
     *
     * @param info 查询对象
     * @param page 分页
     * @return 结果
     */
    public RespMeta<List<?>> monthDataCompare(KqQueryDto info, Page page) {
        // 处理用户名
        if (StringUtils.isNotBlank(info.getUsername())) {
            info.setUsername("'" + Joiner.on("','").join(info.getUsername().split(",|，")) + "'");
        }

        RespMeta<List<?>> resp = new RespMeta<>();
        if (null != page) {
            log.info("分页查询:{}", JSONObject.toJSONString(page));
            PageHelper.startPage(page.getPageIndex(), page.getPageSize());
            List<?> list = mapper.monthDataCompare(info);
            resp.setPage(PageUtils.getPages(list, page));
            resp.setData(PageUtils.getData(list));
        } else {
            List<?> list = mapper.monthDataCompare(info);
            resp.setData(list);
        }
        return resp;
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

        LocalDate start = DateUtils.stringToLocalDate(info.getStartDate());
        LocalDate end = DateUtils.stringToLocalDate(info.getEndDate());
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
        // 考勤天数
        map = this.columnMap("", "考勤天数(按半天/1天计算)与假请核对");
        columnList.add(map);
        map.put("isLeaf", "N");
        itemList = Lists.newArrayList();
        map.put("subList", itemList);
        for (int i = startMonth; i <= endMonth; i++) {
            int mon = i > 12 ? startMonth - 12 : i;
            itemList.add(this.columnMap(mon + "月考勤", mon + "月"));

            sb.append(String.format(", max(case when T.m = '%s' then T.考勤天数 else null end) as '%s'", mon, mon + "月考勤"));
        }
        // 结算分钟数
        map = this.columnMap("", "结算分钟数");
        columnList.add(map);
        map.put("isLeaf", "N");
        itemList = Lists.newArrayList();
        map.put("subList", itemList);
        for (int i = startMonth; i <= endMonth; i++) {
            int mon = i > 12 ? startMonth - 12 : i;
            itemList.add(this.columnMap(mon + "月结算分钟", mon + "月"));
            sb.append(String.format(", max(case when T.m = '%s' then T.结算分钟数 else null end) as '%s'", mon, mon + "月结算分钟"));
        }
        // 结算人天
        map = this.columnMap("", "结算分结算人天钟数");
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

        // 日期范围
        LocalDate start = DateUtils.stringToLocalDate(info.getStartDate());
        LocalDate end = DateUtils.stringToLocalDate(info.getEndDate());
        log.info("日期范围:[{}-{}]", start, end);
        /** 获取列表头 **/
        List<Map<String, ?>> columnList = this.columnList(start, end);
        columnList.add(0, this.columnMap("USERNAME", "姓名"));
        columnList.add(this.columnMap("TOTAL", "汇总"));

        // 查询所有数据
        List<Map<String, ?>> allDataList = mapper.monthDataDetail(info);

        // 按用户转换数据
        List<Map<String, ?>> rsDataList = this.convertData(allDataList);

        // 响应对象
        RespMeta<MonthData> resp = new RespMeta<>();
        MonthData data = new MonthData();
        data.setColumnList(columnList);
        // 数据对象
        data.setDataList(rsDataList);

        resp.setData(data);
        return resp;
    }

    private List<Map<String,?>> convertData(List<Map<String,?>> allDataList) {
        Map<String, Map<String,Object>> allUserData = Maps.newHashMap();

        for (Map<String, ?> dateItem : allDataList) {
            String userName = (String) dateItem.get("USERNAME");
            Map<String,Object> userData = allUserData.getOrDefault(userName, Maps.newHashMap());
            allUserData.put(userName, userData);

            // 存放表一天
            LocalDate date =  DateUtils.date2LocalDate((Date) dateItem.get("WORK_DATE"));
            BigDecimal daysJs = (BigDecimal) dateItem.get("DAYS_JS");
            BigDecimal total = (BigDecimal) userData.getOrDefault("TOTAL", BigDecimal.ZERO);
            total = total.add(daysJs);

            userData.put(DateUtils.localDateFormat(date, MONTH_DAY) + "_INFO", dateItem);
            userData.put(DateUtils.localDateFormat(date, MONTH_DAY) + "_STATUS", dateItem.get("KQ_STATUS"));
            userData.put("TOTAL", total);
            userData.put("USERNAME", userName);
            userData.put(DateUtils.localDateFormat(date, MONTH_DAY), daysJs);
        }

        List<Map<String,?>> rsList = Lists.newArrayList();
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
            String date = DateUtils.localDateFormat(start, MONTH_DAY);
            Map<String, Object>  map = this.columnMap(date, date);
            if (start.getDayOfWeek().getValue() > 5) {
                map.put("TABLE_HEAD_CSS", "WEEKEND");
            }
            columnList.add(map);
            // 增加一天
            start = start.plus(1, ChronoUnit.DAYS);
        }
        return columnList;
    }
}
