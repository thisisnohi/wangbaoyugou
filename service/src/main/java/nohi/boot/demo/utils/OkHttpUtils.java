package nohi.boot.demo.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author NOHI
 * @date 2022/8/25 22:23
 **/
@Slf4j
public class OkHttpUtils {
    public final static int READ_TIMEOUT = 100;
    public final static int CONNECT_TIMEOUT = 60;
    public final static int WRITE_TIMEOUT = 60;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * FORM 表单
     */
    public static final String MEDIA_TYPE_FORM = "application/x-www-form-urlencoded";


    private static final String CONTENT_TYPE = "Content-Type";

    private static final byte[] LOCKER = new byte[0];
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;


    private OkHttpUtils() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //todo 由于是静态工具类，只会创建client一次，如果以后需要不同请求不同超时时间，不能这样使用

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((java.security.KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //设置读取超市时间
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        //设置超时连接时间
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //设置写入超时时间
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        //支持HTTPS请求，跳过证书验证
        // 20230812 jdk9+后不支持此方法
        clientBuilder.sslSocketFactory(sslSocketFactory, trustManager);

        clientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        //添加拦截器
        clientBuilder.addInterceptor(new OkHttpLogInterceptor());
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = clientBuilder.addNetworkInterceptor(logInterceptor).build();

    }

    /**
     * 单例模式获取NetUtils
     *
     * @return
     */
    public static OkHttpUtils getInstance() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }


    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public Response getData(String url) {
        return getData(url, null);
    }


    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public Response getData(String url, Map<String, String> headerMap) {
        //1 构造Request
        Request.Builder builder = new Request.Builder().get().url(url);

        Request request = builder.build();

        //2 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * post form表单 请求，同步方式，提交数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @param bodyParams
     * @return
     */
    /*public Response postFormData(String url, Map<String, String> bodyParams) {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).addHeader(CONTENT_TYPE, HttpContentTypeEnum.FORM.contentTypeValue).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }*/

    /**
     * post方式
     *
     * @param url        请求url
     * @param bodyParams requestbody
     * @param headerMap  请求头信息
     * @return
     * @throws IOException
     */
    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public Response postData(String url, Map<String, Object> bodyParams, Map<String, String> headerMap) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams, headerMap);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder().post(body).url(url);
        addHeaders(headerMap, requestBuilder);
        Request request = requestBuilder.build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * put请求方式
     *
     * @param url
     * @param bodyParams
     * @return
     */
    public Response putData(String url, Map<String, Object> bodyParams, Map<String, String> headerMap) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams, headerMap);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder().put(body).url(url);
        addHeaders(headerMap, requestBuilder);
        Request request = requestBuilder.build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }


    /**
     * @param url
     * @param bodyParams
     * @return
     */
    public Response delData(String url, Map<String, Object> bodyParams, Map<String, String> headerMap) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams, headerMap);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder().delete(body).url(url);
        addHeaders(headerMap, requestBuilder);
        Request request = requestBuilder.build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    /**
     * post的请求参数，构造RequestBody
     *
     * @param bodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, Object> bodyParams, Map<String, String> headerMap) throws Exception {
        //判断请求头中是否存在 content-type字段
        if (headerMap == null || !headerMap.containsKey(CONTENT_TYPE)) {
            throw new Exception("请求头信息配置中无 Content-Type配置，请先配置");
        }

        String contentType = headerMap.get(CONTENT_TYPE);
        if (MEDIA_TYPE_FORM.equals(contentType)) {
            //表单提交 就是key-value 都是字符串型
            //转换
            Map<String, String> strBodyParamMap = new HashMap<>(8);
            if (bodyParams != null && !bodyParams.isEmpty()) {
                bodyParams.forEach((key, value) -> {
                    if (value != null) {
                        strBodyParamMap.put(key, (String) value);
                    }
                });
            }
            return buildRequestBodyByMap(strBodyParamMap);
        } else {
            //json
            return buildRequestBodyByJson(JSONObject.toJSONString(bodyParams));
        }

    }

    /**
     * 表单方式提交构建
     *
     * @param bodyParams
     * @return
     */
    private RequestBody buildRequestBodyByMap(Map<String, String> bodyParams) {
        RequestBody body = null;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, bodyParams.get(key));
                log.info(" 请求参数：{}，请求值：{} ", key, bodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;
    }

    /**
     * json方式提交构建
     *
     * @param jsonStr
     * @return
     */
    private RequestBody buildRequestBodyByJson(String jsonStr) {
        return RequestBody.create(jsonStr, JSON);
    }


    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return
     */
    public SSLSocketFactory createSslSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }


    /**
     * 针对json post处理
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String postJson(String url, String json) throws IOException {
        Response response = postJsonReturnResponse(url, json);
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 针对json post处理
     */
    public Response postJsonReturnResponse(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        return mOkHttpClient.newCall(request).execute();
    }

    public String postJson(String url, Map<String, String> headerMap, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        addHeaders(headerMap, requestBuilder);
        Response response = mOkHttpClient.newCall(requestBuilder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            String msg = response.body().string();
            if (null != msg) {
                return msg;
            }
            throw new IOException("Unexpected code " + response);
        }
    }

    public void postJsonAsyn(String url, String json, final MyNetCall myNetCall) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myNetCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                myNetCall.success(call, response);

            }
        });
    }

    /**
     * 自定义网络回调接口
     */
    public interface MyNetCall {
        /**
         * 成功
         *
         * @param call     函数
         * @param response 响应
         * @throws IOException 异常
         */
        void success(Call call, Response response) throws IOException;

        /**
         * 失败
         *
         * @param call 函数
         * @param e    异常
         */
        void failed(Call call, IOException e);
    }

    /**
     * 添加header信息
     *
     * @param headerMap
     * @param builder
     * @return
     */
    private static Request.Builder addHeaders(Map<String, String> headerMap, Request.Builder builder) {
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach((key, value) -> builder.addHeader(key, value));
        }
        return builder;
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
