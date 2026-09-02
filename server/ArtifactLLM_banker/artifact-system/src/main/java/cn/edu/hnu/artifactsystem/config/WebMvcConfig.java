package cn.edu.hnu.artifactsystem.config;

import cn.edu.hnu.artifactsystem.interceptor.JwtAuthenticationInterceptor;
import cn.edu.hnu.artifactsystem.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于配置拦截器等Web相关设置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册JWT认证拦截器
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/user/login",
                    "/api/user/register",
                    "/api/user/email/code",
                    "/api/user/captcha",
                    "/api/knowledge/**",
                    "/api/relics/**",
                    "/error",
                    "/static/**"
                );

        // 再注册权限拦截器（在JWT认证之后执行）
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/user/login",
                    "/api/user/register",
                    "/api/user/email/code",
                    "/api/user/captcha",
                    "/api/knowledge/**",
                    "/api/relics/**",
                    "/error",
                    "/static/**"
                );
    }
}
