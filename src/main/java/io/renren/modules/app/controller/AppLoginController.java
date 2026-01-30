/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.controller;


import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.form.WechatLoginForm;
import io.renren.modules.app.service.UserService;
import io.renren.modules.app.utils.JwtUtils;
import io.renren.modules.sys.service.SysUserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * APP登录授权
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@Api("APP登录接口")
public class AppLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SysUserTokenService sysUserTokenService;


    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation("登录")
    public R login(@RequestBody LoginForm form){
        //表单校验
        ValidatorUtils.validateEntity(form);

        //用户登录
        long userId = userService.login(form);

        //生成token
        String token = jwtUtils.generateToken(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }

    /**
     * 微信小程序登录
     * 返回JWT token，用于后续API调用的身份验证
     */
    @PostMapping("/auth/login")
    @ApiOperation("微信小程序登录")
    public R wechatLogin(@RequestBody WechatLoginForm form){
        //表单校验
        ValidatorUtils.validateEntity(form);

        //微信用户登录
        long userId = userService.wechatLogin(form);

        //生成JWT token（注意：此token为JWT格式，非数据库存储的token）
        String token = jwtUtils.generateToken(userId);

        return sysUserTokenService.createToken(userId, token);

    }

}
