package com.schoollab.service.impl;

import com.schoollab.controller.request.SendMailRequestBody;
import com.schoollab.dto.SendMailResponseDto;
import com.schoollab.service.SendMailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class SendMailServiceImpl implements SendMailService {
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private Configuration config;

//    @Override
//    public SendMailResponseDto sendMail(SendMailRequestBody req, Map<String, Object> model) {
//        SendMailResponseDto responseDto = new SendMailResponseDto();
//        MimeMessage message = sender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");
////            helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
//            Template t = null;
//            if(req.getType().equals("verify_account")){
//                t = config.getTemplate("email-template.ftl");
//            }else if(req.getType().equals("forgot_password")){
//                t = config.getTemplate("forgot-password-template.ftl");
//            }
//            t.setEncoding("utf-8");
//            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
//
//            helper.setTo(req.getTo());
//            helper.setText(html, true);
//            helper.setSubject(req.getSubject());
////            helper.setFrom(req.getFrom());
//            sender.send(message);
//
//            responseDto.setMessage("mail send to : "+req.getTo());
//            responseDto.setStatus(Boolean.TRUE);
//
//        } catch (MessagingException | IOException | TemplateException e) {
//            responseDto.setMessage("Send mail failure: "+ e.getMessage());
//            responseDto.setStatus(Boolean.FALSE);
//        }
//        return responseDto;
//    }

    @Override
    public SendMailResponseDto sendMail(SendMailRequestBody req, Map<String, Object> model) {
        SendMailResponseDto responseDto = new SendMailResponseDto();
        MimeMessage mimeMessage = sender.createMimeMessage();
        try {
            mimeMessage.setSubject(req.getSubject(), "UTF-8");
//            mimeMessage.setFrom(new InternetAddress(resourceProperties.getAwsSenderMail(), resourceProperties.getAwsSenderAlias()));
            mimeMessage.setRecipients(Message.RecipientType.TO, req.getTo());
//            mimeMessage.setRecipients(Message.RecipientType.CC, convertToAddress(mailCC));
//            mimeMessage.setRecipients(Message.RecipientType.BCC, convertToAddress(mailBCC));
            Template t = null;
            if(req.getType().equals("verify_account")){
                t = config.getTemplate("email-template.ftl");
            }else if(req.getType().equals("forgot_password")){
                t = config.getTemplate("forgot-password-template.ftl");
            }
            t.setEncoding("utf-8");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessage.setContent(html, "text/html; charset=UTF-8");
//            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");
//            helper.addAttachment("logo.png", new ClassPathResource("logo.png"));

//            helper.setTo(req.getTo());
//            helper.setText(html, true);
//            helper.setSubject(req.getSubject());
//            helper.setFrom(req.getFrom());
            sender.send(mimeMessage);

            responseDto.setMessage("mail send to : "+req.getTo());
            responseDto.setStatus(Boolean.TRUE);

        } catch (MessagingException | IOException | TemplateException e) {
            responseDto.setMessage("Send mail failure: "+ e.getMessage());
            responseDto.setStatus(Boolean.FALSE);
        }
        return responseDto;
    }
}
