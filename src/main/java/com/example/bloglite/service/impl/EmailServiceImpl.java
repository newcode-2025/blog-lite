package com.example.bloglite.service.impl;

import com.example.bloglite.config.MailProperties;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTML 邮件发送服务实现
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    public EmailServiceImpl(
            JavaMailSender javaMailSender,
            TemplateEngine templateEngine,
            MailProperties mailProperties
    ) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendHtml(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) {
        validateMailConfiguration();
        validateRecipient(to);

        if (!StringUtils.hasText(templateName)
                || templateName.contains("..")
                || templateName.startsWith("/")) {
            throw new BizException(40000, "邮件模板名称不正确");
        }

        try {
            Context context = new Context();
            context.setVariables(variables == null ? Map.of() : variables);
            String html = templateEngine.process(
                    "mail/" + templateName,
                    context
            );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(buildSender());
            helper.setTo(to.trim());
            helper.setSubject(
                    StringUtils.hasText(subject) ? subject : "blog-lite 邮件"
            );
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new BizException(50000, "邮件发送失败", exception);
        }
    }

    private void validateMailConfiguration() {
        if (!mailProperties.isEnabled()) {
            throw new BizException(40900, "邮件功能未启用");
        }

        if (!StringUtils.hasText(mailProperties.getFrom())) {
            throw new BizException(50000, "发件人邮箱未配置");
        }
    }

    private void validateRecipient(String to) {
        if (!StringUtils.hasText(to) || !to.contains("@")) {
            throw new BizException(40000, "收件邮箱格式不正确");
        }
    }

    private InternetAddress buildSender() throws MessagingException {
        try {
            String fromName = StringUtils.hasText(
                    mailProperties.getFromName()
            )
                    ? mailProperties.getFromName()
                    : "blog-lite";
            return new InternetAddress(
                    mailProperties.getFrom().trim(),
                    fromName,
                    StandardCharsets.UTF_8.name()
            );
        } catch (UnsupportedEncodingException exception) {
            throw new MessagingException("发件人名称编码失败", exception);
        }
    }
}