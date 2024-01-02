package nohi.boot.demo.service.db;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.dao.CarMaintainHisMapper;
import nohi.boot.demo.entity.CarMaintainHis;
import nohi.boot.demo.utils.HttpClientPoolUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class CarMaintainHisService extends ServiceImpl<CarMaintainHisMapper, CarMaintainHis> {


    @Autowired
    CarMaintainHisMapper mapper;

    @Override
    public CarMaintainHisMapper getBaseMapper() {
        return this.mapper;
    }


    public String synData(String url, int page, int rows) {
        // 获取数据
        CloseableHttpClient client = HttpClientPoolUtils.getHttpClient();
        String resp = sendMsg(url, client, page, rows);
        // String resp = "{\"total\":\"342867\",\"rows\":[{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/27\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":300.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏A6K92U\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":50000.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580757\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"东风风行\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"李其慧\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":300.00,\"A_DEPTID.DEPT_NAME\":\"城南店\",\"A.BILL_ID\":580757,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏A6K92U\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"CND-KX202312270003\",\"A.STRIKE_AMT\":300.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":538.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏AUT819\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":131000.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580876\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"依兰特1.6\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"刘海玲\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":538.00,\"A_DEPTID.DEPT_NAME\":\"建宁路店\",\"A.BILL_ID\":580876,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"张敏\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"JNLD-KX202312280001\",\"A.STRIKE_AMT\":538.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/27\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":625.50,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏A2FL33\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":89425.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580792\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"途观\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"李其慧\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":625.00,\"A_DEPTID.DEPT_NAME\":\"城南店\",\"A.BILL_ID\":580792,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏A25YE5\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"CND-KX202312270005\",\"A.STRIKE_AMT\":625.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":265.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏AB038W\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":75000.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580927\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"昌河北斗星\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"刘海玲\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":265.00,\"A_DEPTID.DEPT_NAME\":\"建宁路店\",\"A.BILL_ID\":580927,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏AB038W\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"JNLD-KX202312280004\",\"A.STRIKE_AMT\":265.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":350.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏AL95W1\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":239724.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580891\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"迈腾1.8\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"陈浩\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":345.00,\"A_DEPTID.DEPT_NAME\":\"铁心桥店\",\"A.BILL_ID\":580891,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏AL95W1\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"TXQD-KX202312280001\",\"A.STRIKE_AMT\":345.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":861.05,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏F183PZ\",\"A.TMP_ISBALANCE\":\"N\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":186397.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580930\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"朗逸1.6\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"刘海玲\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":981.00,\"A_DEPTID.DEPT_NAME\":\"建宁路店\",\"A.BILL_ID\":580930,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"南通新华幕墙装饰有限公司\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":120.00,\"A.BILL_NO\":\"JNLD-KX202312280006\",\"A.STRIKE_AMT\":981.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":600.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏A12T1W\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":106419.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580897\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"斯柯达\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"梁艳\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":600.00,\"A_DEPTID.DEPT_NAME\":\"仙林店\",\"A.BILL_ID\":580897,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏A3D09V\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"XLD-KX202312280003\",\"A.STRIKE_AMT\":600.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":625.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏A81T58\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":539467.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580969\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"福特锐界\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"李其慧\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":625.00,\"A_DEPTID.DEPT_NAME\":\"城南店\",\"A.BILL_ID\":580969,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"陶先生\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"CND-KX202312280007\",\"A.STRIKE_AMT\":625.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":875.50,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏AJ021U\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":116953.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580950\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"标致308\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"陈浩\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":875.00,\"A_DEPTID.DEPT_NAME\":\"铁心桥店\",\"A.BILL_ID\":580950,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"J021U\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"TXQD-KX202312280003\",\"A.STRIKE_AMT\":875.00},{\"A_EcMaster.BX_AMT\":0.00,\"A.BILL_DATE\":\"2023/12/28\",\"A_EcMaster.BX_CL_AMT\":0.00,\"A.BILL_TYPE\":\"SV001\",\"A.AMT\":552.00,\"A_EcMaster.CARD_CL_AMT\":0.00,\"A_EcMaster.SV_STATUS\":3,\"A_VEHICLEID.LICENSE_PLATE_CODE\":\"苏A1EK22\",\"A.TMP_ISBALANCE\":\"Y\",\"A_EcMaster.CARD_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00,\"A_EcMaster.KILOMETRE\":59562.0,\"A_EcMaster.BX_GS_AMT\":0.00,\"id\":\"1§580964\",\"A_DEPTID_ACCTTYPE.ACCT_TYPE\":474,\"A_EcMaster.SB_AMT\":0.00,\"A_EcMaster.SB_CL_AMT\":0.00,\"A_VEHICLEID_ITEMID.ITEM_NAME\":\"福克斯\",\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_STAFFID.STAFF_NAME\":\"陈浩\",\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":550.00,\"A_DEPTID.DEPT_NAME\":\"铁心桥店\",\"A.BILL_ID\":580964,\"A.ORTHER_AMT\":0.00,\"A_VEHICLEID_CUSTID.CUST_NAME\":\"苏A1EK22\",\"A_EcMaster.DRIVER_TEL\":\" \",\"A_EcMaster_SVTYPE.DESCRIPTION\":null,\"A_EcMaster.GS_AMT\":0.00,\"A.BILL_NO\":\"TXQD-KX202312280006\",\"A.STRIKE_AMT\":550.00}],\"footer\":[{\"A_EcMaster.SB_CL_AMT\":0.00,\"A.STRIKE_AMT\":143207089.55,\"A_EcMaster.GS_AMT\":6380083.35,\"A_EcMaster.BX_CL_AMT\":0.00,\"A_EcMaster.CARD_CL_AMT\":50520.20,\"A_EcMaster.SB_AMT\":0.00,\"A.ORTHER_AMT\":113676.00,\"A_EcMaster.CARD_GS_AMT\":0.00,\"A.TTL_AMT\":155612767.30,\"A_EcMaster.BX_GS_AMT\":0.00,\"A.BILL_DATE\":\"合计\",\"A_EcMaster.BX_AMT\":0.00,\"A_EcMaster.CARD_AMT\":50520.20,\"A.AMT\":149580970.86,\"A_EcMaster.SB_CLHS_AMT\":0.00,\"A_EcMaster.SB_GS_AMT\":0.00}]}";
        if (StringUtils.isBlank(resp)) {
            log.error("============page[{}] resp is error", page);
            return page + "";
        }
        // log.info("resp:{}", resp);
        parseRespToEntity(resp);
        return null;
    }

    public void parseRespToEntity(String resp) {
        JSONObject object = JSONObject.parseObject(resp);
        JSONArray rows = (JSONArray) object.get("rows");

        List<CarMaintainHis> list = new ArrayList<>();
        for (Object row : rows) {
            JSONObject rowObject = (JSONObject) row;
            Map<String, String> rsMap = new HashMap<>();
            for (String key : rowObject.keySet()) {
                String newKey = key.replaceAll("\\.", "_").toUpperCase();
                // log.info("key[{}]->{} {}", key, newKey, rowObject.get(key));
                rsMap.put(newKey, null == rowObject.get(key) ? "" : rowObject.get(key).toString());
            }
            String json = JSONObject.toJSONString(rsMap);
            CarMaintainHis his = JSONObject.parseObject(json, CarMaintainHis.class);
            // log.info("his: {}", JSONObject.toJSONString(his));
            list.add(his);
        }
        log.info("保存记录[{}]条 from[{}]", list.size(), rows.size());
        // 保存
//        this.saveOrUpdateBatch(list, 1000);
        this.saveBatch(list, 1000);
    }

    private String sendMsg(String url, CloseableHttpClient httpClient, int page, int rows) {
        HttpPost http = new HttpPost(url);
        http.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED);
        //http.addHeader(HttpHeaders.REFERER, "http://114.55.63.4:888/SVHx002/RptDataListDV?ECEvent=init_query&queryname=rpt_SVHistory002&userright=B&winid=004_40_009&sessionid=5b3e5dba-c6b2-41b7-8b46-90776a62d7b6&seriod_content_no=0.08241901727498036");
        CloseableHttpResponse httpResponse = null;

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("firstload", page == 0 ? "1" : "0"));
        nvps.add(new BasicNameValuePair("page", page + ""));
        nvps.add(new BasicNameValuePair("rows", rows + ""));
        http.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));

        String msg = null;
        try {
            httpResponse = httpClient.execute(http);
            HttpEntity entity = httpResponse.getEntity();
            log.debug("[{}]响应[{}]", url, httpResponse.getCode());
            if (entity != null) {
                msg = EntityUtils.toString(entity);
            }
            return msg;
        } catch (IOException | ParseException e) {
            log.error("请求[{}]交易异常:{}", url, e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (httpResponse != null) {
                //执行httpResponse.close关闭对象会关闭连接池，
                //如果需要将连接释放到连接池，可以使用EntityUtils.consume()方法
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    log.error("释放[{}]httpResponse异常:{}", url, e.getMessage(), e);
                }
            }
        }
    }
}
