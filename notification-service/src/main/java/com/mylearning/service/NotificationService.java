package com.mylearning.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mylearning.event.OrderPlacedEvent;
import com.mylearning.exception.MyMessagingException;
import com.mylearning.exception.PdfException;
import com.mylearning.utility.CustomMultipartFile;
import io.micrometer.observation.ObservationRegistry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.annotation.KafkaHandler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mylearning.exception.EmailException;

import java.awt.*;
import java.io.*;
import java.util.Objects;

import io.micrometer.observation.Observation;

import org.springframework.messaging.handler.annotation.Header;


/*
    @KafkaListener(topics="product-created-events-topic") specifies that this bean should listen to the “product-created-events-topic” topic in Kafka.
    @KafkaListener annotation marks a method as a Kafka message listener for specific topics.

    The @KafkaHandler annotation designates the method within a class annotated with @KafkaListener that will handle messages.
     When a message arrives, Spring will invoke the method annotated with @KafkaHandler that has a compatible parameter type.

     @KafkaHandler(isDefault = true):
        Starting from version 2.1.3, you can designate a method as the default fallback if no other @KafkaHandler method matches the payload type.
        Use this annotation when the payload has already been converted to the domain object.

        @KafkaHandler
The @KafkaHandler annotation is used in conjunction with the @KafkaListener annotation on a class level to handle different types of messages. This is useful when you have multiple message types coming to the same Kafka topic, and you want to handle each type differently.

isDefault: This attribute indicates that the annotated method is the default handler for messages that do not match any other handler methods.

     @KafkaHandler(isDefault = true)
        void handleDefault(Object payload, @Header(KafkaHeaders.RECORD_METADATA) ConsumerRecordMetadata meta) {
    String topic = meta.topic();
    // Handle the message here
}
----------------------------------------------------------------
@Service
public class OrderProducerService {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private static final String TOPIC = "order-topic";

    public void sendOrderPlacedEvent(OrderPlacedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Sent OrderPlacedEvent: " + event);
    }
}

@Service
@KafkaListener(topics = "order-topic", groupId = "order-group")
public class OrderConsumerService {

    @KafkaHandler
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Received OrderPlacedEvent: " + event);
        // Process the OrderPlacedEvent
    }

    @KafkaHandler(isDefault = true)
    public void consumeDefault(Object message) {
        System.out.println("Received Message of unknown type: " + message);
        // Process the unknown message
    }
}

----------------------------------------------------------------
The @KafkaHandler annotation designates the method within a class annotated with @KafkaListener
that will handle messages. When a message arrives, Spring will invoke the method annotated
with @KafkaHandler that has a compatible parameter type.

For instance, in the ProductCreatedEventHandler class, the handle method is annotated with
@KafkaHandler and takes a ProductCreatedEvent as a parameter:

@KafkaHandler
public void handle(ProductCreatedEvent productCreatedEvent) {
    // Logic to process the event
}
When a ProductCreatedEvent message is received from the “product-created-events-topic”,
this handle method will be called with the message payload.

 */

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;

    private final ObservationRegistry observationRegistry;


    private String sendText="""
                                        Hi %s %s,

                                        Your order with order number %s is now placed successfully.
                                         
                                        Best Regards,
                                        Spring Shop
                                        """;

    //@KafkaListener(topics = {"order-placed"},groupId ="${spring.kafka.consumer.group-id}")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("NotificationService.listen() >> Got Message from order-placed topic {}", orderPlacedEvent);

        // Send email to the Customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber().toString()));
            messageHelper.setText(String.format(sendText,
                    orderPlacedEvent.getFirstName().toString(),
                    orderPlacedEvent.getLastName().toString(),
                    orderPlacedEvent.getOrderNumber().toString()));
        };
        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notifcation email sent!!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new EmailException("Exception occurred when sending mail to springshop@email.com" + e.getMessage());
        }
    }


    @KafkaListener(topics = {"order-placed"},groupId ="${spring.kafka.consumer.group-id}")
    public void sendEmailNotification(OrderPlacedEvent orderPlacedEvent) throws IOException, MyMessagingException {
        log.info("NotificationService.listen() >> Got Message from order-placed topic {}", orderPlacedEvent);

        // Creating PDF document for the order-placed
        File pdfFile=createDocument(orderPlacedEvent,"order.pdf");

        // Converting PDF Document to MultipartFile
        MultipartFile multipartFile = convertFileToMultipartFile(pdfFile);

        try{

            boolean multiPartStatus=multipartFile.getSize()>0;
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,multiPartStatus); // setting boolean value true if there is multiPartFile
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(orderPlacedEvent.getEmail().toString());
            mimeMessageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber().toString()));
            mimeMessageHelper.setText(String.format("""
                            Hi %s,%s
 
                            Your order with order number %s is now placed successfully.
                             
                            Best Regards
                            Spring Shop
                            """,
                    orderPlacedEvent.getFirstName().toString(),
                    orderPlacedEvent.getLastName().toString(),
                    orderPlacedEvent.getOrderNumber().toString()));

            mimeMessageHelper.addAttachment(
                    Objects.requireNonNull(multipartFile.getOriginalFilename()),
                    new ByteArrayResource(multipartFile.getBytes())
            );

            javaMailSender.send(mimeMessage);
            log.info("NotificationService.sendEmailNotification >>> Order Notifcation email sent!!");

        }catch (MailException e){
            log.error("MailException occurred when sending mail", e);
            throw new EmailException("MailException occurred when sending mail");
        } catch (MessagingException e) {
            log.error("MessagingException occurred when sending mail", e);
            throw new MyMessagingException("MessagingException occurred when sending mail");
        }

    }

    //@KafkaListener(topics = {"order-placed"},groupId ="${spring.kafka.consumer.group-id}")
    public void sendEmailNotificationKafkaHeader(ConsumerRecord<String, OrderPlacedEvent> consumerRecord, @Header(KafkaHeaders.RECORD_METADATA) ConsumerRecordMetadata meta) throws IOException, MyMessagingException {

        var topic = meta.topic();
        log.info("Header ConsumerRecordMetadata Topic :: {}", topic);
        log.info("Header ConsumerRecordMetadata Offset :: {}", meta.offset());
        log.info("Header ConsumerRecordMetadata Partition :: {}", meta.partition());

        OrderPlacedEvent orderPlacedEvent = consumerRecord.value();
        //String traceId = new String(consumerRecord.headers().lastHeader("traceId").value());
        String traceId = new String(consumerRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value());

        log.info("sendEmailNotificationKafkaHeader traceId :: {}", traceId);
        log.info("NotificationService.listen() >> Got Message from order-placed topic {}", orderPlacedEvent);

        Observation.createNotStarted("notification-service", observationRegistry)
                .contextualName("receive-message")
                .lowCardinalityKeyValue("traceId", traceId)
                .observe(() -> {
                    try {
                        // Creating PDF document for the order-placed
                        File pdfFile = createDocument(orderPlacedEvent, "order.pdf");

                        // Converting PDF Document to MultipartFile
                        MultipartFile multipartFile = convertFileToMultipartFile(pdfFile);

                        boolean multiPartStatus = multipartFile.getSize() > 0;
                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, multiPartStatus);
                        mimeMessageHelper.setFrom(fromEmail);
                        mimeMessageHelper.setTo(orderPlacedEvent.getEmail().toString());
                        mimeMessageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber()));
                        mimeMessageHelper.setText(String.format(sendText,
                                orderPlacedEvent.getFirstName(),
                                orderPlacedEvent.getLastName(),
                                orderPlacedEvent.getOrderNumber()));

                        mimeMessageHelper.addAttachment(
                                Objects.requireNonNull(multipartFile.getOriginalFilename()),
                                new ByteArrayResource(multipartFile.getBytes())
                        );

                        javaMailSender.send(mimeMessage);
                        log.info("NotificationService.sendEmailNotification >>> Order Notification email sent!!");

                    } catch (MailException | MessagingException | IOException e) {
                        log.error("Exception occurred when sending mail", e);
                    }
                });

    }

    private File createDocument(OrderPlacedEvent orderPlacedEvent, String dest){
        try {
            File pdfFile = new File(dest);
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));

            // Setting font family and color
            Font font = new Font(Font.HELVETICA, 16, Font.BOLDITALIC, Color.RED);

            doc.open();

            Paragraph para = new Paragraph("Hello! This PDF is created using OpenPDF", font);
            doc.add(para);

            Paragraph linePara = new Paragraph("-------Order-Invoice---------",font);
            doc.add(linePara);

            /*doc.add(new Paragraph("Order Placed Event",font));
            doc.add(new Paragraph("Order Number: " + orderPlacedEvent.getOrderNumber()));
            doc.add(new Paragraph("Email: " + orderPlacedEvent.getEmail()));
            doc.add(new Paragraph("First Name: " + orderPlacedEvent.getFirstName()));
            doc.add(new Paragraph("Last Name: " + orderPlacedEvent.getLastName()));
            doc.add(new Paragraph("SKU Code: " + orderPlacedEvent.getSkuCode()));
            doc.add(new Paragraph("Price: " + orderPlacedEvent.getPrice()));
            doc.add(new Paragraph("Quantity: " + orderPlacedEvent.getQuantity()));*/

            // Create a table with 4 columns
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Set spacing before the table (adjust the value as needed)
            table.setSpacingBefore(10f); // 20 points (you can use other units as well)

            // Create a bold font for headers
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);

            // Add table headers
            addTableHeader(table, "Customer Name", headerFont);
            addTableHeader(table, "Name of Product", headerFont);
            addTableHeader(table, "Amount", headerFont);
            addTableHeader(table, "Quantity", headerFont);

            // Add the data row
            table.addCell(orderPlacedEvent.getFirstName().toString() + " " + orderPlacedEvent.getLastName());
            table.addCell(orderPlacedEvent.getSkuCode().toString());
            table.addCell(orderPlacedEvent.getPrice().toString());
            table.addCell(String.valueOf(orderPlacedEvent.getQuantity()));

            //doc.topMargin();
            //doc.setMargins(2,2,2,2);
            table.spacingBefore();
            table.setHorizontalAlignment(1);
            table.spacingAfter();
            doc.add(table);
            //doc.bottomMargin();

            // Create a bold font for headers
            Font footerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Phrase footerPhrase = new Phrase("Total Amount", footerFont);
            HeaderFooter footer=new HeaderFooter(true,footerPhrase);



            var totalAmount=orderPlacedEvent.getPrice().intValueExact()*orderPlacedEvent.getQuantity();
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            // Set spacing before the table (adjust the value as needed)
            totalTable.setSpacingBefore(5f); // 20 points (you can use other units as well)
            totalTable.addCell("Total Amount");
            totalTable.addCell("Rs"+totalAmount);
            doc.add(totalTable);
            doc.setFooter(footer);

            doc.close();
            writer.close();

            return pdfFile;
        } catch (DocumentException | FileNotFoundException e) {
            log.error("PDF Document NOT CREATED");
            throw new PdfException("PDF Document NOT CREATED "+ e.getMessage());
        }

    }

    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        return new CustomMultipartFile(file, "application/pdf");
    }

    private static void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        // Add cells to the table
        PdfPCell headerCell = new PdfPCell();
        headerCell.setPhrase(new Phrase(headerTitle, font));
        headerCell.setBackgroundColor(Color.LIGHT_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerCell);
    }
}
