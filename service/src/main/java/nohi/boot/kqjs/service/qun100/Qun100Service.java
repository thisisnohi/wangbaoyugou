package nohi.boot.kqjs.service.qun100;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.dto.page.Page;
import nohi.boot.common.utils.DateUtils;
import nohi.boot.demo.utils.OkHttpUtils;
import nohi.boot.demo.utils.PageUtils;
import nohi.boot.kqjs.config.Qun100Config;
import nohi.boot.kqjs.consts.Qun100Consts;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.dto.qun100.*;
import nohi.boot.kqjs.entity.KqMembers;
import nohi.boot.kqjs.entity.KqQun100;
import nohi.boot.kqjs.entity.PubPara;
import nohi.boot.kqjs.service.qun100.db.KqMembersServiceImpl;
import nohi.boot.kqjs.service.qun100.db.KqQun100ServiceImpl;
import nohi.boot.kqjs.service.qun100.db.PubParaServiceImpl;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>群报数</p>
 * @date 2025/01/24 14:02
 **/
@Service
@Slf4j
public class Qun100Service {
    @Autowired
    Qun100Config qun100Config;

    @Autowired
    PubParaServiceImpl pubParaService;
    @Autowired
    KqMembersServiceImpl kqMembersService;
    @Autowired
    KqQun100ServiceImpl kqQun100Service;

    public static final String MONTH_DAY = "M/d";

    /**
     * 获取formId
     */
    public String getFormId() {
        PubPara para = pubParaService.selectOneByParaName(Qun100Consts.PUB_PARA_QUN100_FORMID);
        if (null != para) {
            return para.getParaValue();
        }
        return null;
    }

    /**
     * 获取Token
     * 判断token是否5分钟内过期，如果是则刷新
     *
     * @return Token
     */
    public String getToken() {
        PubPara para = pubParaService.selectOneByParaName(Qun100Consts.PUB_PARA_QUN100_TOKEN);
        String token = null;
        if (!this.tokenValid(para)) {
            token = this.refreshToken(para);
        } else {
            token = para.getParaValue();
        }
        return token;
    }

    // 判断token是否有效
    public boolean tokenValid(PubPara para) {
        if (null == para || StringUtils.isBlank(para.getParaValue()) || StringUtils.isBlank(para.getRemark1())) {
            return false;
        }
        // 如果截止时间易于当前时间+5分钟，则认为不可用
        if (para.getRemark1().compareTo(DateUtils.format(DateUtils.addSeconds(DateUtils.getNow(), 4 * 60), DateUtils.HYPHEN_TIME)) <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 刷新TOKEN
     */
    public String refreshToken(PubPara para) {

        TokenResp token = this.refreshToken();
        boolean isNew = false;
        // 存储
        if (null == para || StringUtils.isBlank(para.getParaName())) {
            para = new PubPara();
            para.setId(UUID.randomUUID().toString());
            isNew = true;
        }
        para.setParaName(Qun100Consts.PUB_PARA_QUN100_TOKEN);
        para.setParaValue(token.getAccessToken());
        para.setRemark1(DateUtils.format(DateUtils.addSeconds(DateUtils.getNow(), token.getExpireInSecs()), DateUtils.HYPHEN_TIME));

        // 保存
        if (isNew) {
            pubParaService.insertSelective(para);
        } else {
            pubParaService.updateSelectiveById(para);
        }

        return para.getParaValue();
    }

    public TokenResp refreshToken() {
        String subTitle = "[refreshToken]";
        // 获取token
        String url = qun100Config.getServiceUrl() + "/openapi/v1/refreshAccessToken?appId=" + qun100Config.getAppid() + "&secret=" + qun100Config.getSecret();
        try {
            OkHttpUtils utils = OkHttpUtils.getInstance();
            Response resp = utils.getData(url);

            if (!resp.isSuccessful()) {
                log.error("{} 接口异常[{}],{}", subTitle, resp.code(), resp.message());
                throw new RuntimeException("调用接口异常:" + resp.code());
            }
            // 获取响应报文
            String respBody = resp.body().string();
            log.info("{} respBody:{}", subTitle, respBody);
            // 转换为对象
            JSONObject object = JSONObject.parseObject(respBody);
            // 判断状态
            if (!Qun100Consts.RespCode.SUC.getCode().equals(object.getString("code"))) {
                log.error("{} resp:{}", subTitle, object.get("message"));
            }

            TokenResp respData = JSONObject.parseObject(object.get("data").toString(), TokenResp.class);
            log.info("{} respData:{}", subTitle, respData);
            return respData;
        } catch (Exception e) {
            log.error("{} 异常:{}", subTitle, e.getMessage(), e);
            throw new RuntimeException("刷新TOKEN异常:" + e.getMessage());
        }
    }


    /**
     * 同步formdata
     */
    public void synFormData(Date startDate, Date endDate) {
        String subTitle = "[synFormData]";
        log.info("{} DATE [{}] to [{}]", subTitle, startDate, endDate);
        // 如果日期为空，则查当天
        if (startDate == null && endDate == null) {
            startDate = new Date();
            endDate = startDate;
        } else if (startDate == null) {
            startDate = endDate;
        } else if (endDate == null) {
            endDate = startDate;
        }
        String startDateStr = DateUtils.format(startDate, DateUtils.HYPHEN_DATE);
        String endDateStr = DateUtils.format(endDate, DateUtils.HYPHEN_DATE);
        String startTime = startDateStr + " 00:00:00";
        String endTime = endDateStr + " 23:59:59";
        log.info("{} TIME [{}] to [{}]", subTitle, startTime, endTime);
        FormDataReq req = new FormDataReq();
        req.setStartTime(startTime);
        req.setEndTime(endTime);
        req.setFormId(this.getFormId());
        // 获取所有formdata
        FormDataRespData allData = this.getAllFormData(req);

        // 保存
        this.saveFormData(startDateStr, endDateStr, allData);
    }

    public void saveFormData(String startDate, String endDate, FormDataRespData allData) {
        // 成员列表ID
        Set<String> memSet = Sets.newHashSet();
        // 工作日
        TreeSet<String> workDateSet = new TreeSet<>();

        // 清理所有数据
        int updateCount = kqQun100Service.deleteDataByDateRange(startDate, endDate);
        log.info("清理[{}-{}]数据[{}]条", startDate, endDate, updateCount);

        // 转换数据为数据库实例
        for (FormDataRespDataListItem item : allData.getList()) {
            // 工作日
            Date workDate = item.getCreateTime();
            String workDateStr = DateUtils.format(workDate, DateUtils.HYPHEN_DATE);
            workDateSet.add(workDateStr);
            KqMembers mem = new KqMembers();
            mem.setSno(item.getSno());
            mem.setFuid(item.getFuid());
            mem.setIcon(item.getIcon());
            mem.setNickname(item.getNickname());
            // 用户名待解析列表数据
            mem.setUsername(null);

            KqQun100 kq = new KqQun100();
            kq.setWorkDate(workDate);
            kq.setCreatedTs(item.getCreateTime());
            // 考勤数据
            for (int i = 0; i < item.getCatalogs().size(); i++) {
                FormDataRespCataLog catalog = item.getCatalogs().get(i);
                String type = catalog.getType();
                String value = catalog.getValue();
                // 姓名
                if ("WORD".equalsIgnoreCase(type)) {
                    mem.setUsername(value);
                    kq.setUsername(value);
                } else if (i == 1) {
                    // 门禁
                    try {
                        // 时分秒
                        String time = value.split(" ")[1];
                        kq.setCardTimeDoor(DateUtils.parseDateStrictly(workDateStr + " " + time, "yyyy-MM-dd HH:mm", DateUtils.HYPHEN_TIME, DateUtils.HYPHEN_DATE));
                    } catch (Exception e) {
                        log.error("门禁日期转换[{}] 异常:{}", value, e.getMessage());
                    }
                } else if (i == 2) {
                    // app
                    try {
                        // 时分秒
                        String time = value.split(" ")[1];
                        kq.setCardTimeApp(DateUtils.parseDateStrictly(workDateStr + " " + time, "yyyy-MM-dd HH:mm", DateUtils.HYPHEN_TIME, DateUtils.HYPHEN_DATE));
                    } catch (Exception e) {
                        log.error("APP日期转换[{}] 异常:{}", value, e.getMessage());
                    }
                } else {
                    // 备注
                    kq.setRemark1(value);
                }
            }

            // 如果不包含，则同步数据
            if (!memSet.contains(mem.getFuid())) {
                // 同步数据
                mem = kqMembersService.syncMem(mem);
                // 保存ID
                memSet.add(mem.getFuid());
            }
            // 保存打卡数据
            kqQun100Service.save(kq);
        }
    }

    /**
     * 获取所有Formdata
     */
    public FormDataRespData getAllFormData(FormDataReq req) {
        String subTitle = "[getAllFormData]";
        req.setPageNo(1);
        req.setPageSize(10);
        FormDataRespData allData = new FormDataRespData();
        allData.setTotal(0);
        allData.setList(Lists.newArrayList());

        int pageSize = req.getPageSize();
        // 当前页
        int currentPage = 1;

        // 获取token
        String token = this.getToken();
        log.info("{} 获取token:{}", subTitle, token);
        do {
            // 设置页
            req.setPageNo(currentPage);
            // 调用数据
            FormDataRespData respData = this.getFormData(token, req);
            allData.setTotal(respData.getTotal());
            allData.getList().addAll(respData.getList());

            currentPage++;
            // 判断是否存在下一页
            int nextTotal = (currentPage - 1) * pageSize;
            if (nextTotal >= allData.getTotal()) {
                log.info("{} 下一页开始数[{}]超过总数[{}]", subTitle, nextTotal, allData.getTotal());
                break;
            }
        } while (true);

        return allData;
    }

    /**
     * 获取formdata
     */
    public FormDataRespData getFormData(String token, FormDataReq req) {
        String subTitle = "[getFormData]";
        log.info("{} token:{}", subTitle, token);
        // 调用接口
        String json = JSONObject.toJSONString(req);
        try {
            OkHttpUtils utils = OkHttpUtils.getInstance();
            Response resp = utils.postJsonReturnResponse(qun100Config.getServiceUrl() + "/openapi/v1/form/data?accessToken=" + token, json);
            log.info("resp:{}", resp);

            if (!resp.isSuccessful()) {
                log.error("请求异常：{} {}", resp.code(), resp);
                throw new RuntimeException("调用接口异常:" + resp.code());
            }
            String responseBody = resp.body().string();
            log.info("{} responseBody:{}", subTitle, responseBody);
            // 转换为对象
            JSONObject object = JSONObject.parseObject(responseBody);
            // 判断状态
            if (!Qun100Consts.RespCode.SUC.getCode().equals(object.getString("code"))) {
                log.error("{} resp:{}", subTitle, object.get("message"));
                throw new RuntimeException("调用接口失败:" + resp.code());
            }
            return JSONObject.parseObject(object.get("data").toString(), FormDataRespData.class);
        } catch (Exception e) {
            log.error("{} 异常:{}", subTitle, e.getMessage());
            throw new RuntimeException("获取FormData异常:" + e.getMessage());
        }
    }

    /**
     * 月考勤数据
     *
     * @param info 查询对象
     * @param page 分页
     * @return 结果
     */
    public RespMeta<MonthData> query(KqQueryDto info, Page page) {
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

        // 获取表头： 月份
        List<Map<String, ?>> columnList = this.columnList(start, end);
        columnList.add(0, this.columnMap("USERNAME", "姓名"));

        // 响应对象
        RespMeta<MonthData> resp = new RespMeta<>();
        MonthData data = new MonthData();
        data.setColumnList(columnList);

        List<?> list = kqQun100Service.queryDetail(info);
        data.setDataList(PageUtils.getData(list));

        resp.setData(data);
        return resp;
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
            Map<String, Object> map = this.columnMap(date, date);
            columnList.add(map);
            map.put("isLeaf", "N");
            if (start.getDayOfWeek().getValue() > 5) {
                map.put("TABLE_HEAD_CSS", "WEEKEND");
            }
            List<Map<String, ?>> itemList = Lists.newArrayList();
            map.put("subList", itemList);
            // 出勤天数
            itemList.add(this.columnMap("门禁", "门禁"));
            itemList.add(this.columnMap("APP", "APP"));

            // 增加一天
            start = start.plus(1, ChronoUnit.DAYS);
        }
        return columnList;
    }

    private Map<String, Object> columnMap(String value, String lable) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("value", value);
        map.put("label", lable);
        map.put("isLeaf", "Y");
        return map;
    }
}
