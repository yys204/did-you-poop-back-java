package io.renren.common.utils;

import io.renren.modules.app.utils.JwtUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Token用户信息解析工具类
 * 用于解析JWT token并获取用户信息
 * 注意：本系统中有两套认证机制：
 * 1. 系统模块使用Shiro + 数据库存储的token
 * 2. APP模块使用JWT token（本工具类主要用于APP模块）
 */
@Component
public class TokenUserInfoUtil {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ShiroService shiroService;

    /**
     * 通过JWT token解析获取用户信息
     * @param token JWT token
     * @return 用户实体信息，如果token无效则返回null
     */
    public SysUserEntity getUserInfoByToken(String token) {
        // 首先验证token是否有效
        io.jsonwebtoken.Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null) {
            return null; // token无效
        }

        // 检查token是否过期
        if (jwtUtils.isTokenExpired(claims.getExpiration())) {
            return null; // token已过期
        }

        // 从token中获取用户ID
        Long userId = Long.parseLong(claims.getSubject());

        // 通过用户ID查询用户详细信息
        return shiroService.queryUser(userId);
    }

    /**
     * 从JWT token中提取用户ID
     * @param token JWT token
     * @return 用户ID，如果token无效则返回null
     */
    public Long getUserIdFromToken(String token) {
        io.jsonwebtoken.Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
            return null;
        }

        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 验证JWT token是否有效
     * @param token JWT token
     * @return true表示有效，false表示无效
     */
    public boolean isValidToken(String token) {
        io.jsonwebtoken.Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null) {
            return false;
        }

        return !jwtUtils.isTokenExpired(claims.getExpiration());
    }
}