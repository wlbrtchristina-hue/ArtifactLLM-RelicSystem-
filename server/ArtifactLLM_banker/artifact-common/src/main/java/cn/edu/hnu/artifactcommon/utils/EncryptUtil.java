
package cn.edu.hnu.artifactcommon.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 加密工具类
 */
public class EncryptUtil {

    /**
     * MD5加密
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * MD5加盐加密
     * @param text 明文
     * @param salt 盐
     * @return 密文
     */
    public static String md5(String text, String salt) {
        return md5(text + salt);
    }

    /**
     * 生成随机盐
     * @return 盐
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * BCrypt加密
     * @param password 明文密码
     * @return 密文
     */
    public static String bcrypt(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    /**
     * BCrypt密码验证
     * @param password 明文密码
     * @param encodedPassword 密文密码
     * @return 是否匹配
     */
    public static boolean bcryptMatch(String password, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, encodedPassword);
    }

    /**
     * SHA256加密
     * @param text 明文
     * @return 密文
     */
    public static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }
}
