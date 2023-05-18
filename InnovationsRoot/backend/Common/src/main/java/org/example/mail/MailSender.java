package org.example.mail;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public class MailSender {

    SesV2Client client;

    public MailSender (SesV2Client client) {
        this.client = client;
    }

    public static MailSender createMailSender() {
        Region region = Region.EU_NORTH_1;
        SesV2Client sesv2Client = SesV2Client.builder()
                .region(region)
                .build();

        return new MailSender(sesv2Client);
    }

    public boolean send(String recipient,
                            String subject,
                            String body
    ){

        Destination destination = Destination.builder()
                .toAddresses(recipient)
                .build();
        Content content = Content.builder()
                .data(body)
                .build();
        Content sub = Content.builder()
                .data(subject)
                .build();
        Body msgBody = Body.builder()
                .html(content)
                .build();
        Message msg = Message.builder()
                .subject(sub)
                .body(msgBody)
                .build();
        EmailContent emailContent = EmailContent.builder()
                .simple(msg)
                .build();
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .content(emailContent)
                .fromEmailAddress("zaricu22@gmail.com")
                .build();
        try {
            client.sendEmail(emailRequest);
            return true;
        } catch (SesV2Exception e) {
            return false;
        }
    }
}
