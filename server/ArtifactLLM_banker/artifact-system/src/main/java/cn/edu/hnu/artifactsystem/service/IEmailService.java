package cn.edu.hnu.artifactsystem.service;

/**
 * 邮件服务接口
 */
public interface IEmailService {
    
    /**
     * 发送邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendEmail(String to, String subject, String content);
    
    /**
     * 发送HTML格式邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML格式邮件内容
     * @return 是否发送成功
     */
    boolean sendHtmlEmail(String to, String subject, String htmlContent);
}