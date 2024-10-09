package com.learning.emsmybatisliquibase.service.impl;

import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.NotificationDto;
import com.learning.emsmybatisliquibase.entity.Employee;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.service.NotificationService;
import com.learning.emsmybatisliquibase.service.TimelineService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TimelineDao timelineDao;
    private final TimelineService timelineService;

    @Value("${default.send.email}")
    String defaultEmail;

    @Value("${email.template.name.successful.onboard}")
    String emailTemplateNameSuccessfulOnboard;

    @Value("${email.template.successful.onboard}")
    String emailTemplateSuccessfulOnboard;

    @Value("${email.template.name.before.review.start}")
    String beforeReviewStartEmail;

    @Value("${email.template.subject.before.review.start}")
    String beforeReviewStartSubject;

    @Override
    public void sendSuccessfulEmployeeOnBoard(Employee employee, String password) {
        Thread thread = new Thread(() -> {
            try {
                MimeMessageHelper helper = createMimeMessageHelper(defaultEmail, employee.getEmail(), emailTemplateSuccessfulOnboard);

                Context context = new Context();
                context.setVariable("name", employee.getFirstName() + " " + employee.getLastName());
                context.setVariable("email", employee.getEmail());
                context.setVariable("phoneNumber", employee.getPhoneNumber());
                context.setVariable("password", password);

                helper.setText(templateEngine.process(emailTemplateNameSuccessfulOnboard, context), true);
                mailSender.send(helper.getMimeMessage());
            } catch (MessagingException e) {
                log.error("Error sending successful onboarding email for employee with UUID: {}", employee.getUuid(), e);
            }
        });
        thread.start();
    }

    @Override
    public void sendNotificationBeforeStart(ReviewType reviewType) {
        var notifications = timelineDao.getTimelineIdsByReviewType(reviewType);

        var employeeUuids = notifications.stream()
                .map(NotificationDto::getUuid)
                .toList();
        timelineService.updateTimelineStatus(employeeUuids, reviewType, TimelineStatus.STARTED);

        Thread thread = new Thread(() -> notifications.forEach(employee -> {
            log.info("Sending notification before start email to colleague {}", employee.getUuid());
            try {
                MimeMessageHelper helper = createMimeMessageHelper(defaultEmail, employee.getEmail(), beforeReviewStartSubject);

                Context context = new Context();
                context.setVariable("name", employee.getFirstName() + " " + employee.getLastName());
                context.setVariable("reviewStartDate", employee.getStartTime());
                context.setVariable("reviewType", reviewType);

                helper.setText(templateEngine.process(beforeReviewStartEmail, context), true);
                mailSender.send(helper.getMimeMessage());
            } catch (MessagingException e) {
                log.error("Error Sending Notification before start email to colleague {}", employee.getUuid(), e);
            }
        }));
        thread.start();
    }

    private MimeMessageHelper createMimeMessageHelper(String fromEmail, String toEmail, String subject)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        return helper;
    }
}