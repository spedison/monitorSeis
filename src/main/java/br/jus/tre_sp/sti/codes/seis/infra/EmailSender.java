package br.jus.tre_sp.sti.codes.seis.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final String host;
    private final String port;
    private final String username;
    private final String password;

    public EmailSender(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public boolean sendEmail(String toEmail, String subject, String messageBody) {
        // Configuração de propriedades do servidor SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        List<String> toEmails = null;
        if (toEmail.contains("[ \t;:]")){
            toEmails = Arrays
                    .stream(toEmail.split("[ \t;:]"))
                    .map(String::trim)
                    .filter(s->!s.isBlank())
                    .collect(Collectors.toList());
        }

        // Criação de uma sessão autenticada
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Construção da mensagem de e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            if (toEmails == null) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            }else{
                Address[] recipientAddresses = new Address[toEmails.size()];
                for (int i = 0; i < toEmails.size(); i++) {
                    recipientAddresses[i] = new InternetAddress(toEmails.get(i));
                }
                message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            }
            message.setSubject(subject);
            message.setText(messageBody);

            // Envio do e-mail
            Transport.send(message);

            log.info("E-mail enviado com sucesso!");
            return true;
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail: " + e.getMessage());
            return false;
        }
    }
}
