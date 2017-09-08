package ua.com.lavi.komock.smtp

import org.junit.After
import org.junit.Before
import org.junit.Test
import ua.com.lavi.komock.Waiter
import ua.com.lavi.komock.ext.getFirstHeader
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */
class SmtpServerTest {
    private val SERVER_HOST = "127.0.0.1"
    private val SERVER_PORT = 2525

    private val from: String = "source@gmail.com"
    private val to: String = "target@gmail.com"
    private val subject: String = "Subject"
    private val charset = "text/html; charset=utf8"
    private val body = "This is a body"

    private var smtpServer: SmtpServer = SmtpServer(SmtpServerProperties().withPort(SERVER_PORT))

    @Before
    fun setUp() {
        smtpServer.start()
    }

    @After
    fun tearDown() {
        smtpServer.stop()
    }

    @Test
    fun should_send_simple_text_plain() {
        sendEmail()
        val messages = smtpServer.getMessages()
        Waiter.untilNotEmpty(messages, 1000)
        assertEquals(1, messages.size)
        val message: MimeMessage = messages[0]

        assertTrue(message.content.toString().contains(body))
        assertEquals(from, message.getFirstHeader("From"))
        assertEquals(to, message.getFirstHeader("To"))
        assertEquals(subject, message.getFirstHeader("Subject"))
        assertEquals(charset, message.getFirstHeader("Content-Type"))
    }

    @Test
    fun should_send_multipart_with_attachment() {
        sendMultipartEmailWithAttach()
        val messages = smtpServer.getMessages()
        Waiter.untilNotEmpty(messages, 1000)
        assertEquals(1, messages.size)
        val message = messages[0]
        val byteParts = getByteParts(message)
        assertTrue(message.contentType.contains("multipart/mixed"))
        assertEquals(from, message.getFirstHeader("From"))
        assertEquals(to, message.getFirstHeader("To"))
        assertEquals(subject, message.getFirstHeader("Subject"))
        assertTrue(Arrays.equals(byteParts[0], body.toByteArray()))
        assertTrue(String(byteParts[1]) == File("build.gradle").readText())
    }

    private fun getByteParts(message: MimeMessage): MutableList<ByteArray> {
        val byteMessages: MutableList<ByteArray> = arrayListOf()
        val multipart = message.content as Multipart
        val multipartCount = multipart.count
        (0..multipartCount - 1)
                .map { multipart.getBodyPart(it) }
                .map { it.inputStream.readBytes() }
                .toCollection(byteMessages)
        return byteMessages
    }

    private fun sendEmail() {
        val properties = properties()
        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(from))
        message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
        message.subject = subject
        message.setContent(body, charset)
        Transport.send(message)
    }

    private fun sendMultipartEmailWithAttach() {
        val properties = properties()
        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(from))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
        message.subject = subject
        var messageBodyPart: BodyPart = MimeBodyPart()
        messageBodyPart.setText(body)
        val multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)
        messageBodyPart = MimeBodyPart()
        val filename = "build.gradle"
        messageBodyPart.setDataHandler(DataHandler(FileDataSource(filename)))
        messageBodyPart.setFileName(filename)
        multipart.addBodyPart(messageBodyPart)
        message.setContent(multipart)
        Transport.send(message)
    }

    private fun properties(): Properties {
        val properties = System.getProperties()
        properties.setProperty("mail.smtp.host", SERVER_HOST)
        //properties.setProperty("mail.debug", true.toString())
        properties.setProperty("mail.smtp.port", SERVER_PORT.toString())
        return properties
    }

}