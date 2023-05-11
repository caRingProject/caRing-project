package com.example.caRing.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailService {

	@Value("${spring.mail.username}")
	String sendFrom;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void sendMail (String to) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		
		try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to); // 메일 수신자
            mimeMessageHelper.setSubject("caRing 예약 요청"); // 메일 제목
            mimeMessageHelper.setFrom(sendFrom);
            String str1 = "예약 요청이 들어왔습니다";
            String str2 = "예약을 확인해 주세요";
            mimeMessage.setText(str1 + "\n" + str2);
            
            javaMailSender.send(mimeMessage);

            log.info("Success");

        } catch (MessagingException e) {
            log.info("fail");
            e.printStackTrace();
        }
	}
}
