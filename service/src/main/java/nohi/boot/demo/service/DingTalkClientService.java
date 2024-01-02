package nohi.boot.demo.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.config.MpConfig;
import nohi.boot.demo.consts.DingTalkConsts;
import nohi.boot.demo.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author NOHI
 * @program: nohi-dd-miniprogram-server
 * @description:
 * @create 2021-01-04 21:11
 **/
@Service
@Slf4j
public class DingTalkClientService {
    @Autowired
    private MpConfig mpConfig;

    public OapiGettokenResponse getToken(String url, OapiGettokenRequest req) throws ApiException {
        DefaultDingTalkClient client = new DefaultDingTalkClient(mpConfig.getDingTalkServer() + url);
        log.debug("获取Token请求[{}]，报文:{}", url, JsonUtils.toJson(req));
        OapiGettokenResponse response = client.execute(req);
        log.debug("获取Token请求[{}]，响应:{}", url, JsonUtils.toJson(response));
        return response;
    }

    public <T> T execute(String url, BaseTaobaoRequest req, Class<T> clz) throws ApiException {
        DefaultDingTalkClient client = new DefaultDingTalkClient(mpConfig.getDingTalkServer() + url);
        log.debug("请求[{}]，报文:{}", url, JsonUtils.toJson(req));
        TaobaoResponse response = client.execute(req);
        log.debug("请求[{}]，响应:{}", url, JsonUtils.toJson(response));
        if (!DingTalkConsts.RespCode.SUC.getCode().equals(response.getErrorCode())) {
            throw new ApiException(response.getErrorCode() + ":" + response.getMsg());
        }
        return (T) response;
    }
}
