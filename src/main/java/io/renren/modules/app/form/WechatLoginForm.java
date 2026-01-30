/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 微信小程序登录表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "微信小程序登录表单")
public class WechatLoginForm {
    @ApiModelProperty(value = "微信小程序登录凭证")
    @NotBlank(message="登录凭证不能为空")
    private String code;
}