/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class WechatUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${wechat.appid:}")
    private String appId;

    @Value("${wechat.appsecret:}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    private static final String JS_CODE_2_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 获取微信会话信息
     * @param jsCode 小程序登录凭证
     * @return 微信会话信息
     */
    public Map<String, Object> getSessionInfo(String jsCode) {
        try {
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    JS_CODE_2_SESSION_URL, appId, appSecret, jsCode);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            logger.info("微信API响应: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

            // 检查是否有错误
            if (result.containsKey("errcode")) {
                throw new RuntimeException("微信API错误: " + result.get("errmsg"));
            }

            return result;
        } catch (Exception e) {
            logger.error("获取微信会话信息失败", e);
            throw new RuntimeException("获取微信会话信息失败: " + e.getMessage());
        }
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }
}