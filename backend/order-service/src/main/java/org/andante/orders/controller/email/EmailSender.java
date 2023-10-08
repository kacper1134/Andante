package org.andante.orders.controller.email;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.andante.orders.logic.model.OrderOutput;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EmailSender {

    private static final String ORDER_PLACED_TITLE = "Your order %d have been successfully placed within Andante";
    private static final String PDF_FILENAME = "order-%d.pdf";

    private static final String ORDER_TEMPLATE = "order";

    @Value("classpath:images/Andante.png")
    private Resource logo;

    @Value("${spring.mail.username}")
    private String sender;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @SneakyThrows({MessagingException.class, IOException.class})
    public void sendOrderSummaryMail(OrderOutput orderOutput, InputStreamSource invoice) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        Context context = new Context();
        context.setVariable("sender", sender);
        context.setVariable("id", orderOutput.getId());

        String template = templateEngine.process(ORDER_TEMPLATE, context);

        helper.setFrom(sender);
        helper.setTo(orderOutput.getClient().getEmailAddress());
        helper.setSubject(String.format(ORDER_PLACED_TITLE, orderOutput.getId()));
        helper.setText(template, true);
        helper.addInline("logo", logo);

        helper.addAttachment(String.format(PDF_FILENAME, orderOutput.getId()), new ByteArrayResource(IOUtils.toByteArray(invoice.getInputStream())));

        mailSender.send(message);
    }
}
