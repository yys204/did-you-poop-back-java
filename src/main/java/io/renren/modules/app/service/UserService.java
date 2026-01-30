package io.renren.modules.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.form.WechatLoginForm;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface UserService extends IService<UserEntity> {

	UserEntity queryByMobile(String mobile);

	/**
	 * 用户登录
	 * @param form    登录表单
	 * @return        返回用户ID
	 */
	long login(LoginForm form);

	/**
	 * 微信小程序登录
	 * @param form    微信登录表单
	 * @return        返回用户ID
	 */
	long wechatLogin(WechatLoginForm form);
}
