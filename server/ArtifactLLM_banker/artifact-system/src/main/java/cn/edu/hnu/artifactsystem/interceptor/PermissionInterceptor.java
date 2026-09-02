package cn.edu.hnu.artifactsystem.interceptor;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactsystem.annotation.RequirePermission;
import cn.edu.hnu.artifactsystem.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限拦截器
 * 用于验证用户是否拥有访问特定方法的权限
 * 注意：此拦截器应在JwtAuthenticationInterceptor之后执行
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    // 用户类型与权限映射关系（简化版，实际应从数据库或配置读取）
    private static final Map<String, List<String>> USER_TYPE_PERMISSIONS = new HashMap<>();

    static {
        USER_TYPE_PERMISSIONS.put("admin", Arrays.asList(
            "user:manage", "role:manage", "permission:manage",
            "relic:manage", "model:manage", "system:config"
        ));
        USER_TYPE_PERMISSIONS.put("researcher", Arrays.asList(
            "relic:view", "relic:add", "relic:edit",
            "model:view", "model:add", "model:edit"
        ));
        USER_TYPE_PERMISSIONS.put("normal", Arrays.asList(
            "relic:view", "model:view"
        ));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取方法上的权限注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission methodAnnotation = handlerMethod.getMethodAnnotation(RequirePermission.class);

        // 如果方法上没有注解，检查类上是否有注解
        if (methodAnnotation == null) {
            methodAnnotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }

        // 如果没有权限注解，直接放行
        if (methodAnnotation == null) {
            return true;
        }

        // 从请求属性中获取用户名（由JwtAuthenticationInterceptor设置）
        String username = (String) request.getAttribute("username");
        if (username == null || username.isEmpty()) {
            return unauthorized(response, "用户未登录");
        }

        // 获取用户信息
        var user = userService.findByUsername(username);
        if (user == null) {
            return unauthorized(response, "用户不存在");
        }

        // 获取注解中定义的权限代码
        String[] permissions = methodAnnotation.value();
        if (permissions.length == 0) {
            // 没有指定权限，直接放行
            return true;
        }

        // 获取用户权限列表（基于用户类型）
        List<String> userPermissions = USER_TYPE_PERMISSIONS.getOrDefault(
            user.getRole(), 
            Arrays.asList()
        );

        // 验证权限
        RequirePermission.Logical logical = methodAnnotation.logical();
        boolean hasPermission = false;

        if (logical == RequirePermission.Logical.AND) {
            // AND逻辑：需要拥有所有权限
            hasPermission = userPermissions.containsAll(Arrays.asList(permissions));
        } else {
            // OR逻辑：拥有任一权限即可
            hasPermission = Arrays.stream(permissions)
                .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            // 权限不足
            return unauthorized(response, "权限不足");
        }

        return true;
    }

    /**
     * 返回未授权响应
     */
    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Result<Void> result = Result.error(403, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
        return false;
    }
}
