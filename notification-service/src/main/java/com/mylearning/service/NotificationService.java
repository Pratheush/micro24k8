package com.mylearning.service;

import com.mylearning.event.OrderPlacedEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mylearning.exception.EmailException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;
    //@KafkaListener(topics = {"order-placed"})
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("NotificationService.listen() >> Got Message from order-placed topic {}", orderPlacedEvent);

        // Send email to the Customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber().toString()));
            messageHelper.setText(String.format("""
                            Hi %s,%s
 
                            Your order with order number %s is now placed successfully.
                             
                            Best Regards
                            Spring Shop
                            """,
                    orderPlacedEvent.getFirstName().toString(),
                    orderPlacedEvent.getLastName().toString(),
                    orderPlacedEvent.getOrderNumber().toString()));
        };
        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notifcation email sent!!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to springshop@email.com", e);
        }
    }


    @KafkaListener(topics = {"order-placed"})
    public void sendEmailNotification(OrderPlacedEvent orderPlacedEvent){
        log.info("NotificationService.listen() >> Got Message from order-placed topic {}", orderPlacedEvent);

        try{

           /* boolean multiPartStatus=file.length>0;
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,multiPartStatus);*/ // setting boolean value true if there is multiPartFile

            MimeMessagePreparator mimeMessagePreparator= mimeMessage -> {
              MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
              messageHelper.setFrom(fromEmail);
              messageHelper.setTo(orderPlacedEvent.getEmail().toString());
                messageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber().toString()));
                messageHelper.setText(String.format("""
                            Hi %s,%s
 
                            Your order with order number %s is now placed successfully.
                             
                            Best Regards
                            Spring Shop
                            """,
                        orderPlacedEvent.getFirstName().toString(),
                        orderPlacedEvent.getLastName().toString(),
                        orderPlacedEvent.getOrderNumber().toString()));
            };
           /* mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(mailStructure.getSubject());
            mimeMessageHelper.setText(mailStructure.getMessage());*/

           /* for (MultipartFile multipartFile : file) {
                mimeMessageHelper.addAttachment(
                        Objects.requireNonNull(multipartFile.getOriginalFilename()),
                        new ByteArrayResource(multipartFile.getBytes())
                );
            }*/

            javaMailSender.send(mimeMessagePreparator);
            log.info("NotificationService.sendEmailNotification >>> Order Notifcation email sent!!");

        }catch (MailException e){
            log.error("Exception occurred when sending mail", e);
            throw new EmailException("Exception occurred when sending mail");
        }

    }
}
