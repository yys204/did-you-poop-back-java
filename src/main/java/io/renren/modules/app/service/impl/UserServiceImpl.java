/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.validator.Assert;
import io.renren.modules.app.dao.UserDao;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.form.LoginForm;
import io.renren.modules.app.form.WechatLoginForm;
import io.renren.modules.app.service.UserService;
import io.renren.modules.app.utils.WechatUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

	@Autowired
	private WechatUtils wechatUtils;

	@Override
	public UserEntity queryByMobile(String mobile) {
		return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", mobile));
	}

	@Override
	public long login(LoginForm form) {
		UserEntity user = queryByMobile(form.getMobile());
		Assert.isNull(user, "手机号或密码错误");

		//密码错误
		if(!user.getPassword().equals(DigestUtils.sha256Hex(form.getPassword()))){
			throw new RRException("手机号或密码错误");
		}

		return user.getUserId();
	}

	@Override
	public long wechatLogin(WechatLoginForm form) {
		// 调用微信API获取session信息
		Map<String, Object> sessionInfo = wechatUtils.getSessionInfo(form.getCode());
		String openid = (String) sessionInfo.get("openid");

		// 根据openid查询用户是否存在
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("wx_openid", openid); // 使用微信openid字段查询
		UserEntity user = baseMapper.selectOne(queryWrapper);

		// 如果用户不存在，则创建新用户
		if (user == null) {
			user = new UserEntity();
			user.setWxOpenid(openid); // 设置微信openid
			//["便便星人","蹲坑达人","窝屎糕糕手"] 三个的一个
			String[] nicknames = {"便便星人","蹲坑达人","窝屎糕糕手"};
			int index = (int) (Math.random() * nicknames.length);
			user.setUsername(nicknames[index]); // 设置默认用户名
			user.setPassword(DigestUtils.sha256Hex(openid)); // 设置默认密码
			user.setMobile(""); // 使用微信openid作为mobile值避免冲突
			user.setWxNickname(nicknames[index]); // 设置微信昵
			user.setGender("男"); // 设置微信性别
			user.setAge("23"); // 设置微信年龄

			user.setCreateTime(new Date());

			baseMapper.insert(user);
		}

		return user.getUserId();
	}
}
