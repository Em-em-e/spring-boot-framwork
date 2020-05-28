package com.vehicle300.demo.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * resttemplate请求，支持重试
 *
 * @author : LL
 * @date : 2020/5/27
 */
public class CallableRestTemplate implements Callable<String> {

    private static RestTemplate restTemplate = new RestTemplate();
    /**
     * 接口地址
     */
    private static final String REQUEST_URL = "https://tool.bitefu.net/shouji";
    /**
     * 失败重试次数
     */
    private static final int RETRY_NUM = 3;

    private String mobile;

    public CallableRestTemplate(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String call() {
        JSONObject resp = new JSONObject();
        resp.put("status", 0);
        resp.put("info", "请求重试失败!");
        for (int i = 0; i < RETRY_NUM; i++) {
            try {
                return request(mobile);
            } catch (Exception e) {
//                e.printStackTrace();
                //log..请求失败，正在重试
            }
        }
        return resp.toJSONString();
    }

    private String request(String mobile) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json;charset=utf-8");
        headers.set("Accept", "application/json;charset=utf-8");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //响应字符编码
        List<HttpMessageConverter<?>> lists = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : lists) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }

//        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity<String> resultEntity = restTemplate.exchange(REQUEST_URL + "?mobile=" + mobile, HttpMethod.GET, entity, String.class);
//        ResponseEntity<String> resultEntity =restTemplate.getForEntity(REQUEST_URL + "?mobile=" + mobile,String.class);
        HttpStatus resultStatus = resultEntity.getStatusCode();
        if (!resultStatus.isError()) {
            System.out.println(resultEntity.getBody());
            String body = null;
            if (!StringUtils.isEmpty(resultEntity.getBody())) {
                try {
                    body = URLDecoder.decode(resultEntity.getBody(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println(body);
            }
            return body;
        } else {
            throw new RuntimeException("请求失败！");
        }
    }
}
