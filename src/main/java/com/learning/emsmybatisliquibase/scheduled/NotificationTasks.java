package com.learning.emsmybatisliquibase.scheduled;

import com.learning.emsmybatisliquibase.dao.TimelineDao;
import com.learning.emsmybatisliquibase.dto.NotificationDto;
import com.learning.emsmybatisliquibase.entity.ReviewType;
import com.learning.emsmybatisliquibase.entity.TimelineStatus;
import com.learning.emsmybatisliquibase.service.TimelineService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTasks {

    private final TimelineDao timelineDao;

    private final TimelineService timelineService;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${default.send.email}")
    String defaultEmail;

    @Value("${email.template.name.before.review.start}")
    String beforeReviewStartEmail;

    @Value("${email.template.subject.before.review.start}")
    String beforeReviewStartSubject;

    @Scheduled(cron = "0 0 0 25 3,6,9,12 *")
    public void sendBeforeStartNotification() {
        var calendar = Calendar.getInstance();

        var month = calendar.get(Calendar.MONTH) + 1;
        System.out.println(month);
        switch (month) {
            case 3:
                sendNotificationBeforeStart(ReviewType.Q1);
                break;
            case 6:
                sendNotificationBeforeStart(ReviewType.Q2);
                break;
            case 9:
                sendNotificationBeforeStart(ReviewType.Q3);
                break;
            case 12:
                sendNotificationBeforeStart(ReviewType.Q4);
                break;
            default:
                break;
        }
    }

    private void sendNotificationBeforeStart(ReviewType reviewType) {
        var notifications = timelineDao.getTimelineIdsByReviewType(reviewType);

        var employeeUuids = notifications.stream()
                .map(NotificationDto::getUuid)
                .toList();
        timelineService.updateTimelineStatus(employeeUuids, reviewType, TimelineStatus.STARTED);

        Thread thread = new Thread(() -> notifications.forEach(employee -> {
            log.info("Sending notification before start email to colleague {}", employee.getUuid());
            try {
                MimeMessage message = mailSender.createMimeMessage();

                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(defaultEmail, beforeReviewStartSubject);
                helper.setTo(employee.getEmail());

                helper.setSubject(beforeReviewStartSubject);

                Context context = new Context();
                context.setVariable("name", employee.getFirstName() + " " + employee.getLastName());
                context.setVariable("reviewStartDate", employee.getStartTime());
                context.setVariable("reviewType", reviewType);

                helper.setText(templateEngine.process(beforeReviewStartEmail, context), true);
                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Error Sending Notification before start email to colleague {}", employee.getUuid(), e);
            }
        }));
        thread.start();
    }

}
