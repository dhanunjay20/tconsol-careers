package com.tcon.careers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.hr-email}")
    private String hrEmail;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendApplicationConfirmation(String toEmail, String applicantName, String jobTitle, String applicationId) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping application confirmation email.");
            return;
        }

        try {
            String subject = "Application Received - " + jobTitle;
            String body = buildApplicationConfirmationEmail(applicantName, jobTitle, applicationId);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Application confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send application confirmation email: {}", e.getMessage());
        }
    }

    @Async
    public void sendNewApplicationNotification(String jobTitle, String applicantName, String applicationId) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping new application notification.");
            return;
        }

        try {
            String subject = "New Application Received - " + jobTitle;
            String body = buildNewApplicationNotificationEmail(jobTitle, applicantName, applicationId);
            sendHtmlEmail(hrEmail, subject, body);
            log.info("New application notification sent to HR: {}", hrEmail);
        } catch (Exception e) {
            log.error("Failed to send new application notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendStatusUpdateEmail(String toEmail, String applicantName, String jobTitle, String status, String notes) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping status update email.");
            return;
        }

        try {
            String subject = "Application Status Update - " + jobTitle;
            String body = buildStatusUpdateEmail(applicantName, jobTitle, status, notes);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Status update email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send status update email: {}", e.getMessage());
        }
    }

    @Async
    public void sendInterviewInvitation(String toEmail, String applicantName, String jobTitle,
                                       LocalDateTime scheduledDate, String interviewer, String type,
                                       String meetingLink, String location) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping interview invitation email.");
            return;
        }

        try {
            String subject = "Interview Scheduled - " + jobTitle;
            String body = buildInterviewInvitationEmail(applicantName, jobTitle, scheduledDate,
                                                       interviewer, type, meetingLink, location);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Interview invitation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send interview invitation email: {}", e.getMessage());
        }
    }

    @Async
    public void sendRejectionEmail(String toEmail, String applicantName, String jobTitle) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping rejection email.");
            return;
        }

        try {
            String subject = "Application Update - " + jobTitle;
            String body = buildRejectionEmail(applicantName, jobTitle);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Rejection email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send rejection email: {}", e.getMessage());
        }
    }

    @Async
    public void sendOfferEmail(String toEmail, String applicantName, String jobTitle) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping offer email.");
            return;
        }

        try {
            String subject = "Job Offer - " + jobTitle;
            String body = buildOfferEmail(applicantName, jobTitle);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Offer email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send offer email: {}", e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String userName) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping welcome email.");
            return;
        }

        try {
            String subject = "Welcome to TCON Solutions Careers Portal";
            String body = buildWelcomeEmail(userName);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    @Async
    public void sendPasswordChangedEmail(String toEmail, String userName) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping password changed email.");
            return;
        }

        try {
            String subject = "Password Changed Successfully";
            String body = buildPasswordChangedEmail(userName);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Password changed email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password changed email: {}", e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String tempPassword) {
        if (!emailEnabled) {
            log.info("Email is disabled. Skipping password reset email.");
            return;
        }

        try {
            String subject = "Password Reset - TCON Solutions";
            String body = buildPasswordResetEmail(userName, tempPassword);
            sendHtmlEmail(toEmail, subject, body);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private String buildApplicationConfirmationEmail(String applicantName, String jobTitle, String applicationId) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .footer { background: #333; color: white; padding: 20px; text-align: center; font-size: 12px; }
                    .button { background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                    .tracking-id { background: #fff; padding: 15px; border-left: 4px solid #667eea; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Application Received</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Thank you for applying for the position of <strong>%s</strong> at TCON Solutions.</p>
                        <p>We have successfully received your application and our team will review it shortly.</p>
                        <div class="tracking-id">
                            <strong>Application ID:</strong> %s<br>
                            <small>Use this ID to track your application status</small>
                        </div>
                        <p>You can track your application status using the link below:</p>
                        <a href="%s/track/%s" class="button">Track Application</a>
                        <p>We appreciate your interest in joining our team and will be in touch soon.</p>
                        <p>Best regards,<br>TCON Solutions HR Team</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 TCON Solutions. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, applicantName, jobTitle, applicationId, frontendUrl, applicationId);
    }

    private String buildNewApplicationNotificationEmail(String jobTitle, String applicantName, String applicationId) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2c3e50; color: white; padding: 20px; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { background: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>New Application Received</h2>
                    </div>
                    <div class="content">
                        <p><strong>Position:</strong> %s</p>
                        <p><strong>Applicant:</strong> %s</p>
                        <p><strong>Application ID:</strong> %s</p>
                        <p><strong>Date:</strong> %s</p>
                        <a href="%s/admin/applications/%s" class="button">View Application</a>
                    </div>
                </div>
            </body>
            </html>
            """, jobTitle, applicantName, applicationId,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
            frontendUrl, applicationId);
    }

    private String buildStatusUpdateEmail(String applicantName, String jobTitle, String status, String notes) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .status { background: #fff; padding: 15px; border-left: 4px solid #667eea; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Application Status Update</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Your application for <strong>%s</strong> has been updated.</p>
                        <div class="status">
                            <strong>New Status:</strong> %s
                            %s
                        </div>
                        <p>Thank you for your patience.</p>
                        <p>Best regards,<br>TCON Solutions HR Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, applicantName, jobTitle, status,
            notes != null ? "<br><br><strong>Notes:</strong> " + notes : "");
    }

    private String buildInterviewInvitationEmail(String applicantName, String jobTitle,
                                                LocalDateTime scheduledDate, String interviewer,
                                                String type, String meetingLink, String location) {
        String dateStr = scheduledDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a"));
        String locationInfo = meetingLink != null ?
            "<p><strong>Meeting Link:</strong> <a href=\"" + meetingLink + "\">" + meetingLink + "</a></p>" :
            (location != null ? "<p><strong>Location:</strong> " + location + "</p>" : "");

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: white; padding: 30px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .interview-details { background: #fff; padding: 20px; border-left: 4px solid #11998e; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Interview Scheduled</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Congratulations! We would like to invite you for an interview for the position of <strong>%s</strong>.</p>
                        <div class="interview-details">
                            <p><strong>Date & Time:</strong> %s</p>
                            <p><strong>Interview Type:</strong> %s</p>
                            <p><strong>Interviewer:</strong> %s</p>
                            %s
                        </div>
                        <p>Please confirm your availability for this interview.</p>
                        <p>Best regards,<br>TCON Solutions HR Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, applicantName, jobTitle, dateStr, type, interviewer, locationInfo);
    }

    private String buildRejectionEmail(String applicantName, String jobTitle) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #34495e; color: white; padding: 30px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Application Update</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Thank you for your interest in the <strong>%s</strong> position at TCON Solutions.</p>
                        <p>After careful consideration, we have decided to move forward with other candidates whose qualifications more closely match our current needs.</p>
                        <p>We appreciate the time you invested in the application process and encourage you to apply for future opportunities that match your skills and experience.</p>
                        <p>We wish you the very best in your job search and professional endeavors.</p>
                        <p>Best regards,<br>TCON Solutions HR Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, applicantName, jobTitle);
    }

    private String buildOfferEmail(String applicantName, String jobTitle) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Congratulations!</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>We are delighted to extend an offer for the position of <strong>%s</strong> at TCON Solutions!</p>
                        <p>We were impressed by your qualifications and believe you will be a valuable addition to our team.</p>
                        <p>Our HR team will be in touch shortly with the formal offer letter and next steps.</p>
                        <p>Welcome to TCON Solutions!</p>
                        <p>Best regards,<br>TCON Solutions HR Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, applicantName, jobTitle);
    }

    private String buildWelcomeEmail(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2ecc71; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { background: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to TCON Solutions!</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Welcome to the TCON Solutions Careers Portal!</p>
                        <p>We are excited to have you on board. You can now apply for jobs, track your applications, and manage your profile.</p>
                        <p>To get started, please log in to your account using the button below:</p>
                        <a href="%s/login" class="button">Log In to Your Account</a>
                        <p>If you have any questions, feel free to reply to this email or contact our support team.</p>
                        <p>Best regards,<br>TCON Solutions Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, frontendUrl);
    }

    private String buildPasswordChangedEmail(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #34495e; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Changed</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>Your password has been changed successfully.</p>
                        <p>If you did not request this change, please contact our support team immediately.</p>
                        <p>Best regards,<br>TCON Solutions Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
    }

    private String buildPasswordResetEmail(String userName, String tempPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #e74c3c; color: white; padding: 20px; text-align: center; }
                    .content { background: #f9f9f9; padding: 30px; }
                    .button { background: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset</h1>
                    </div>
                    <div class="content">
                        <h2>Dear %s,</h2>
                        <p>We received a request to reset your password.</p>
                        <p>Your temporary password is: <strong>%s</strong></p>
                        <p>Please log in using this temporary password and change your password to something more secure.</p>
                        <p>If you did not request a password reset, please ignore this email.</p>
                        <p>Best regards,<br>TCON Solutions Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, tempPassword);
    }
}
