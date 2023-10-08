package org.andante.activity.controller.email;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EmailSender {

    private static final String NEWSLETTER_TEMPLATE = "newsletter";
    private static final String NEWSLETTER_TITLE = "Thank you for joining Andante Newsletter!";

    @Value("${spring.mail.username}")
    private String sender;

    @Value("classpath:images/Andante.png")
    private Resource logo;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @SneakyThrows({MessagingException.class})
    public void sendNewsletter(String assignee) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariable("sender", sender);

        String template = templateEngine.process(NEWSLETTER_TEMPLATE, context);

        helper.setFrom(sender);
        helper.setTo(assignee);
        helper.setSubject(NEWSLETTER_TITLE);
        helper.setText(template, true);
        helper.addInline("logo", logo);

        mailSender.send(message);
    }
}
