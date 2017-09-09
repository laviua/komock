package ua.com.lavi.komock.smtp

import javax.mail.internet.MimeMessage

/**
 * Created by Oleksandr Loushkin on 10.09.17.
 */
interface SmtpNotifier {
    fun onMail(mimeMessage: MimeMessage)
}