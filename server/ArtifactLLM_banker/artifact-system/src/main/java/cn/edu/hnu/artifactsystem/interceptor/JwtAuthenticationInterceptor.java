package cn.edu.hnu.artifactsystem.interceptor;

import cn.edu.hnu.artifactcommon.constant.SystemConstant;
import cn.edu.hnu.artifactcommon.context.UserContext;
import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactcommon.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT认证拦截器
 * 用于验证JWT令牌，并将用户信息存储到请求属性中
 */
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 排除登录、注册、验证码等公开接口
        String path = request.getRequestURI();
        if (path.startsWith("/api/user/login") || 
            path.startsWith("/api/user/register") || 
            path.startsWith("/api/user/captcha") ||
            path.startsWith("/error")) {
            return true;
        }

        // 从请求头获取Token
        String token = request.getHeader(SystemConstant.JWT_HEADER);
        if (token != null && token.startsWith(SystemConstant.JWT_PREFIX)) {
            token = token.substring(SystemConstant.JWT_PREFIX.length());
        }

        if (token == null || token.isEmpty()) {
            return unauthorized(response, "未登录或令牌已过期");
        }

        try {
            // 验证Token
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                return unauthorized(response, "无效的令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 将用户名存储到请求属性中，供后续使用
            request.setAttribute("username", username);
            UserContext.setCurrentUsername(username);
            UserContext.setCurrentUserId(userId);
            return true;
        } catch (Exception e) {
            return unauthorized(response, "令牌验证失败");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    /**
     * 返回未授权响应
     */
    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<Void> result = Result.error(401, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
        return false;
    }
}



