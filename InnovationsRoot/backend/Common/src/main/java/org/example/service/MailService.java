package org.example.service;

import org.example.mail.MailSender;
import org.example.model.Employee;
import org.example.model.Innovation;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final String LEAD_MAIL = "savic.jana15@gmail.com";

    private final MailSender mailSender = MailSender.createMailSender();

    public void sendMailToEmployee(Innovation innovation, String recipient) {

        String subject = "Your innovation " + innovation.getTitle() + " is " + innovation.getInnovationStatus();
        String comment = innovation.getComment() != null ? innovation.getComment() : "";
        String body = "Lead left this comment: \n" + comment;
        mailSender.send(recipient, subject, body);
    }

    public void sendMailForNewInnovation(Innovation i, Employee employee) {
        String subject = "New innovation \"" + i.getTitle() + "\" was created by " + employee.getName() + " " + employee.getLastName();
        String body = "";
        mailSender.send(LEAD_MAIL, subject, body);

    }
}
