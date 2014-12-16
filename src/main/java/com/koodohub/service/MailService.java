package com.koodohub.service;

import com.koodohub.KoodoHubConfiguration;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final static Properties EMAIL_PROPERTIES = System.getProperties();
    private final String emailFrom;
    private static final String ACCOUNT_ACTIVATION_SUBJECT="Account activation";
    private final boolean sendGridEmail;

    public MailService(KoodoHubConfiguration configuration) {
        EMAIL_PROPERTIES.setProperty("mail.smtp.host", "localhost");
        emailFrom = configuration.getEmailFrom();
        sendGridEmail = configuration.getGridEmail();
    }

    public void sendActivationEmail(final String baseUri,
                                    final String email,
                                    String username,
                                    String activationToken) {
        log.debug("Sending activation e-mail to '{}'", email);
        if (sendGridEmail) {
            sendGridEmail(baseUri, email, username, activationToken);
        } else {
            try {
                Session session = Session.getDefaultInstance(EMAIL_PROPERTIES);
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailFrom));
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(email));
                message.setSubject(ACCOUNT_ACTIVATION_SUBJECT);
                message.setContent(getActivationEmail(baseUri, email, username, activationToken),
                        "text/html");
                // TODO use separate thread
                Transport.send(message);
            } catch (MessagingException mex) {
                log.warn("E-mail could not be sent to user '{}', exception is: {}",
                        email, mex.getMessage(), mex);
            }
        }
        log.info("Activation email sent to {}", email);

    }

    //used by heroku
    private void sendGridEmail(final String baseUri,
                               final String emailTo,
                               String username,
                               String activationToken) {
        SendGrid sendgrid = new SendGrid("app32531575@heroku.com", "mmcvqt2v");
        SendGrid.Email email = new SendGrid.Email();
        email.addTo(emailTo);
        email.setFrom(emailFrom);
        email.setSubject(ACCOUNT_ACTIVATION_SUBJECT);
        email.setText(getActivationEmail(baseUri, emailTo, username, activationToken));

        try {
            SendGrid.Response response = sendgrid.send(email);
        } catch (SendGridException e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}",
                    email, e.getMessage(), e);
        }
    }

    private String getActivationEmail(String baseUri, String email, String username, String activationToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<h1>Koodo Hub</h1>")
            .append("Hi ").append(username).append(",")
            .append("<p>")
            .append("Welcome to the Koodo Hub where kids watch, do, share projects and meet friends! ")
            .append("Click on the link below to activate your account:")
            .append("</p>")
            .append("<a href=\"")
            .append(baseUri).append("member/activate/").append(email)
            .append("/").append(activationToken)
            .append("\">Activate</a>");
        String emailBody = stringBuilder.toString();
        log.debug("email: {}", emailBody);
        return emailBody;
    }
}
