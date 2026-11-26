package com.greenfactory.gfecmp.controller;

import com.greenfactory.gfecmp.common.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 登录接口（技术验证版：固定账号，不做真实鉴权）。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(String username, String password, String captcha) {
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return ApiResult.fail("请输入用户名与密码");
        }
        if (!"admin".equals(request.username().trim()) || !"123456".equals(request.password())) {
            return ApiResult.fail("用户名或密码错误（演示账号：admin / 123456）");
        }
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("token", "demo-token-" + System.currentTimeMillis());
        user.put("username", request.username());
        user.put("displayName", "王工");
        user.put("role", "系统管理员");
        user.put("company", "华东绿色制造示范工厂");
        return ApiResult.ok(user);
    }
}
