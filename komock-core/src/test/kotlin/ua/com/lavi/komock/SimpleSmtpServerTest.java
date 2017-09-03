package ua.com.lavi.komock;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import ua.com.lavi.smtpgate.netty.SmtpServer;
import ua.com.lavi.smtpgate.netty.SmtpServerProperties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */
public class SimpleSmtpServerTest {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 2525;

    private static final String from = "source@gmail.com";
    private static final String to = "target@gmail.com";
    private static final String subject = "Subject";
    private SmtpServer smtpServer;

    @Before
    public void setUp() throws Exception {

/*        simpleSmtpServer = new SimpleSmtpServer(SERVER_PORT);
        simpleSmtpServer.start();*/
        smtpServer = new SmtpServer(new SmtpServerProperties().withPort(SERVER_PORT));
        smtpServer.start();

    }

    @Test
    public void testEmail() {
        sendEmailWithAttach();
        List<String> messages = smtpServer.getMessages();
/*        List<SmtpMessage> receivedEmail = simpleSmtpServer.getReceivedEmail();
        assertEquals(1, receivedEmail.size());
        assertEquals("This is actual message", receivedEmail.get(0).getBody());
        assertEquals(from, receivedEmail.get(0).getHeader("From"));
        assertEquals(to, receivedEmail.get(0).getHeader("To"));
        assertEquals(subject, receivedEmail.get(0).getHeader("Subject"));
        assertEquals("text/html; charset=utf8", receivedEmail.get(0).getHeader("Content-Type"));

        sendEmail();

        receivedEmail = simpleSmtpServer.getReceivedEmail();
        assertEquals(2, receivedEmail.size());*/
        System.out.println("test end");

    }

    private void sendEmail() {

        Properties properties = getProperties();
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent("This is actual message", "text/html; charset=utf8");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    @NotNull
    private Properties getProperties() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", SERVER_HOST);
        // properties.setProperty("mail.debug", String.valueOf(true));
        properties.setProperty("mail.smtp.port", String.valueOf(SERVER_PORT));
        return properties;
    }

    private void sendEmailWithAttach() {
        try {
            // Create a default MimeMessage object.
            Properties properties = getProperties();
            Session session = Session.getDefaultInstance(properties);
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("This is message body");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "build.gradle";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}