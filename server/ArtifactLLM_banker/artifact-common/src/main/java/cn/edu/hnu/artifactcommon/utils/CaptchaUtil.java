
package cn.edu.hnu.artifactcommon.utils;

import cn.edu.hnu.artifactcommon.constant.SystemConstant;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtil {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 验证码字符集
     */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 验证码长度
     */
    private static final int CAPTCHA_LENGTH = 4;

    /**
     * 验证码过期时间（分钟）
     */
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;

    /**
     * 生成验证码
     * @param uuid 验证码唯一标识
     * @return Base64编码的验证码图片
     * @throws IOException IO异常
     */
    public String generateCaptcha(String uuid) throws IOException {
        // 生成随机验证码
        String captcha = generateRandomCaptcha();

        // 将验证码存储到Redis，设置过期时间
        String redisKey = SystemConstant.REDIS_KEY_CAPTCHA + uuid;
        redisUtil.set(redisKey, captcha, CAPTCHA_EXPIRE_MINUTES * 60L);

        // 生成验证码图片
        BufferedImage image = createCaptchaImage(captcha);

        // 将图片转换为Base64字符串
        return imageToBase64(image);
    }

    /**
     * 验证验证码
     * @param uuid 验证码唯一标识
     * @param captcha 用户输入的验证码
     * @return 验证结果
     */
    public boolean validateCaptcha(String uuid, String captcha) {
        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(captcha)) {
            return false;
        }

        String redisKey = SystemConstant.REDIS_KEY_CAPTCHA + uuid;
        Object storedCaptchaObj = redisUtil.get(redisKey);
        
        if (storedCaptchaObj == null) {
            return false;
        }
        
        String storedCaptcha = storedCaptchaObj.toString();

        // 验证成功后删除验证码
        redisUtil.del(redisKey);

        // 不区分大小写比较
        return storedCaptcha.equalsIgnoreCase(captcha);
    }

    /**
     * 生成随机验证码字符串
     * @return 验证码字符串
     */
    private String generateRandomCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }

        return captcha.toString();
    }

    /**
     * 创建验证码图片
     * @param captcha 验证码字符串
     * @return 验证码图片
     */
    private BufferedImage createCaptchaImage(String captcha) {
        int width = 120;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // 设置背景色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // 设置字体
        graphics.setFont(new Font("Arial", Font.BOLD, 20));

        // 绘制验证码
        Random random = new Random();
        for (int i = 0; i < captcha.length(); i++) {
            // 设置随机颜色
            graphics.setColor(new Color(
                random.nextInt(110), 
                random.nextInt(110), 
                random.nextInt(110)
            ));

            // 绘制字符
            graphics.drawString(
                String.valueOf(captcha.charAt(i)), 
                20 * i + 10, 
                25
            );
        }

        // 绘制干扰线
        for (int i = 0; i < 8; i++) {
            graphics.setColor(new Color(
                random.nextInt(256), 
                random.nextInt(256), 
                random.nextInt(256)
            ));

            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);

            graphics.drawLine(x1, y1, x2, y2);
        }

        graphics.dispose();

        return image;
    }

    /**
     * 将图片转换为Base64字符串
     * @param image 图片
     * @return Base64字符串
     * @throws IOException IO异常
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        javax.imageio.ImageIO.write(image, "png", outputStream);

        byte[] imageBytes = outputStream.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}