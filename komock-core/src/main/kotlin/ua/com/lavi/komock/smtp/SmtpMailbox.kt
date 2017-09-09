package ua.com.lavi.komock.smtp

import org.slf4j.LoggerFactory
import javax.mail.Address
import javax.mail.internet.MimeMessage

/**
 * Created by Oleksandr Loushkin on 10.09.17.
 */
class SmtpMailbox {

    private val log = LoggerFactory.getLogger(this.javaClass)

    val mailBoxes: MutableMap<String, MutableList<MimeMessage>> = HashMap()
    val notifiers: MutableMap<String, SmtpNotifier> = HashMap()

    fun deliver(mimeMessage: MimeMessage) {
        val recipients: List<Address> = mimeMessage.allRecipients.asList()

        for (recipient in recipients) {
            val recipientBox = recipient.toString()
            val mailsList = getMailsList(recipientBox)
            mailsList.add(mimeMessage)
            mailBoxes[recipientBox] = mailsList
            log.info("Message has been delivered to: $recipientBox")
            notifiers[recipientBox]?.onMail(mimeMessage)
        }
    }

    fun getMessages(recipient: String): List<MimeMessage> {
        return mailBoxes[recipient] ?: return emptyList()
    }

    fun registerNotifier(recipient: String, notifier: SmtpNotifier) {
        notifiers[recipient] = notifier
    }

    private fun getMailsList(recipientBox: String): MutableList<MimeMessage> {
        var list = mailBoxes[recipientBox]
        if (list == null) {
            list = ArrayList()
        }
        return list
    }
}