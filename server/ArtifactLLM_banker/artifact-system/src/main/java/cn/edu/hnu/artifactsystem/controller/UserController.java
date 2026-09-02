
package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactcommon.pojo.dto.UserDTO;
import cn.edu.hnu.artifactcommon.pojo.vo.LoginVO;
import cn.edu.hnu.artifactcommon.pojo.vo.UserVO;
import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactcommon.utils.CaptchaUtil;
import cn.edu.hnu.artifactcommon.exception.BusinessException;
import cn.edu.hnu.artifactsystem.service.IUserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private CaptchaUtil captchaUtil;

    @Resource
    private cn.edu.hnu.artifactsystem.service.IEmailService emailService;

    @Resource
    private cn.edu.hnu.artifactcommon.utils.RedisUtil redisUtil;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/email/code")
    public Result<String> sendEmailCode(@RequestParam String email) {
        if (email == null || email.isEmpty()) {
            throw new BusinessException(500, "邮箱不能为空");
        }

        String cooldownKey = "artifact:auth:last:" + email;
        if (redisUtil.hasKey(cooldownKey)) {
            return Result.error(429, "发送过于频繁，请稍后再试");
        }

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String codeKey = "artifact:auth:code:" + email;
        redisUtil.set(codeKey, code, 300);
        redisUtil.set(cooldownKey, "1", 60);

        String subject = "【ArtifactLLM】注册验证码";
        String html = "<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#333;line-height:1.8;padding:16px\">"
                + "<h2 style=\"margin:0 0 12px 0;color:#409eff\">ArtifactLLM 注册验证码</h2>"
                + "<p>您好，您正在进行账户注册。请在 5 分钟内使用以下验证码完成验证：</p>"
                + "<div style=\"font-size:24px;font-weight:bold;letter-spacing:4px;color:#111;padding:12px 16px;border:1px solid #eee;display:inline-block;border-radius:6px\">"
                + code
                + "</div>"
                + "<p style=\"margin-top:12px;color:#666\">如果非本人操作，请忽略本邮件。</p>"
                + "<hr style=\"border:none;border-top:1px solid #eee;margin:16px 0\"/>"
                + "<p style=\"color:#999;font-size:12px\">本邮件由系统自动发送，请勿回复。</p>"
                + "</div>";

        boolean sent = emailService.sendHtmlEmail(email, subject, html);
        if (sent) return Result.success("验证码发送成功");
        return Result.error(500, "验证码发送失败");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserDTO userDTO) {
        // 验证用户名长度
        if (userDTO.getUsername() != null && userDTO.getUsername().length() > 50) {
            throw new BusinessException(500, "用户名长度不能超过50个字符");
        }
        return Result.success(userService.register(userDTO));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody UserDTO userDTO) {
        return Result.success(userService.login(userDTO));
    }

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() throws IOException {
        // 生成UUID作为验证码标识
        String uuid = UUID.randomUUID().toString();

        // 生成验证码图片
        String captchaImage = captchaUtil.generateCaptcha(uuid);

        // 返回验证码图片和UUID
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("image", captchaImage);

        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        // 从请求属性中获取用户名
        String username = (String) request.getAttribute("username");

        if (username == null || username.isEmpty()) {
            throw new BusinessException(401, "未登录或令牌已过期");
        }

        return Result.success(userService.getUserByUsername(username));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 实际应用中可以在这里处理令牌黑名单等逻辑
        return Result.success("登出成功");
    }
}
