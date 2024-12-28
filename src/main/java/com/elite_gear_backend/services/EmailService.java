package com.elite_gear_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.exceptions.AppException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Async
    public void send(String to, String subject, String link, String name, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String emailBody = buildEmail(name, link, message);

            helper.setText(emailBody, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@elite-gear.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
            throw new AppException("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildEmail(String name, String link, String message) {
        return """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:24px;margin:0;color:#0b0c0c">                  
                <div style="font-size:15px;margin:0;color:black;padding:15px;">
                    Hi \t""" + name + """
                    <br/>
                    <br/>"""    
                    + message +"""
                    <br/>
                    <br/>
                    <blockquote style="margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px;font-size:19px;line-height:25px;background-color:#f9f9f9;">
                        <p style="margin:0 0 20px 0;font-size:20px;line-height:25px;">
                        """+
                            "<a href='"+ link + "'style='font-weight:bold;''>Confirm Now</a>"+ 
                        "</p>"+ 
                    "</blockquote>"+ 
                    "<p style='margin:15px 0 0 0;font-size:15px;color:#666;'>"+ 
                        "Link will expire in 5 minutes. If this action hasn't been done by you, please ignore this email." + 
                    "</p>"+ 
                "</div>"+
            "</div>";
    }
}
