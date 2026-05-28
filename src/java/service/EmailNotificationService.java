package service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for sending email notifications using JavaMail API. Compatible
 * with javax.mail-1.6.2.jar and activation-1.1.1.jar
 *
 * @author kolisang
 * @version 1.0
 */
public class EmailNotificationService {

    /**
     * Logger for recording email operations and errors
     */
    private static final Logger logger = Logger.getLogger(EmailNotificationService.class.getName());

    // Email configuration - UPDATE THESE WITH YOUR ACTUAL CREDENTIALS
    /**
     * SMTP host server address for sending emails
     */
    private static final String SMTP_HOST = "smtp.gmail.com";
    /**
     * SMTP port number (587 for TLS)
     */
    private static final int SMTP_PORT = 587;
    /**
     * Sender email address for outgoing emails
     */
    private static final String SENDER_EMAIL = "seboko.faso@gmail.com";
    /**
     * Sender email account password or app-specific password
     */
    private static final String SENDER_PASSWORD = "dfeq kier dzes yctc";
    /**
     * Display name for the sender in outgoing emails
     */
    private static final String SENDER_NAME = "ProcureGov - Ministry of Public Works";

    /**
     * Sends a password reset email to a user containing a reset link. The reset
     * link expires after 30 minutes.
     *
     * @param recipientEmail The user's email address
     * @param recipientName The user's name for personalization
     * @param resetLink The password reset link URL
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendPasswordResetEmail(String recipientEmail, String recipientName,
            String resetLink) {
        String subject = "ProcureGov - Password Reset Request";
        String content = "Dear " + recipientName + ",\n\n"
                + "We received a request to reset your password for your ProcureGov account.\n\n"
                + "Please click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link will expire in 30 minutes.\n\n"
                + "If you did not request a password reset, please ignore this email "
                + "or contact the ICT Helpdesk immediately.\n\n"
                + "Best regards,\n"
                + "ICT Helpdesk\n"
                + "Ministry of Public Works\n"
                + "Kingdom of Lesotho\n"
                + "Tel: +266 2232 1000";

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends a registration confirmation email to a newly registered supplier.
     * Includes the supplier's registration number and login instructions.
     *
     * @param recipientEmail The supplier's email address
     * @param recipientName The supplier's company or individual name
     * @param registrationNumber The generated registration number
     * @param loginUrl URL to the login page
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendRegistrationConfirmation(String recipientEmail, String recipientName,
            String registrationNumber, String loginUrl) {
        String subject = "Welcome to ProcureGov - Supplier Registration Confirmation";
        String content = "Dear " + recipientName + ",\n\n"
                + "WELCOME TO PROCUREGOV\n\n"
                + "Your supplier account has been successfully created on the ProcureGov e-Tender Management System.\n\n"
                + "Your Registration Number: " + registrationNumber + "\n\n"
                + "You may now log in to the portal using your registered email address and password.\n\n"
                + "Login here: " + loginUrl + "\n\n"
                + "Through the ProcureGov portal, you can:\n"
                + "- Browse and download open tender notices\n"
                + "- Submit sealed electronic bids\n"
                + "- Track the status of your submitted bids\n"
                + "- Receive award notifications\n\n"
                + "Important: Please ensure your company profile and contact information are kept up to date.\n\n"
                + "Best regards,\n"
                + "Procurement Office\n"
                + "Ministry of Public Works\n"
                + "Kingdom of Lesotho\n"
                + "Tel: +266 2232 1000\n"
                + "Email: procurement@mpw.gov.ls";

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends an award notification email to a supplier informing them of the
     * tender result. Sends different content depending on whether the supplier
     * won or lost the bid.
     *
     * @param recipientEmail The supplier's email address
     * @param recipientName The supplier's company or individual name
     * @param tenderReference The tender reference number
     * @param tenderTitle The tender title
     * @param won true if supplier won the bid, false otherwise
     * @param awardAmount The awarded amount (only applicable if won)
     * @param awardDate The award date
     * @param justification The award justification
     * @param awardNoticeUrl URL to the award notice page
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendAwardNotification(String recipientEmail, String recipientName,
            String tenderReference, String tenderTitle,
            boolean won, String awardAmount, String awardDate,
            String justification, String awardNoticeUrl) {

        String subject = "Tender Award Notification - " + tenderReference;
        String content;

        if (won) {
            content = "Dear " + recipientName + ",\n\n"
                    + "CONGRATULATIONS!\n\n"
                    + "We are pleased to inform you that your bid for the following tender has been SUCCESSFUL:\n\n"
                    + "Tender Reference: " + tenderReference + "\n"
                    + "Tender Title: " + tenderTitle + "\n"
                    + "Awarded Amount: M " + awardAmount + "\n"
                    + "Award Date: " + awardDate + "\n\n"
                    + "Award Justification:\n" + justification + "\n\n"
                    + "Please log in to the ProcureGov portal to view the full award notice and next steps.\n\n"
                    + "View Award Notice: " + awardNoticeUrl + "\n\n"
                    + "A contract document will be prepared and sent to you shortly.\n\n"
                    + "Thank you for participating in the government procurement process.\n\n"
                    + "Best regards,\n"
                    + "Procurement Office\n"
                    + "Ministry of Public Works\n"
                    + "Kingdom of Lesotho\n"
                    + "Tel: +266 2232 1000\n"
                    + "Email: procurement@mpw.gov.ls";
        } else {
            content = "Dear " + recipientName + ",\n\n"
                    + "TENDER AWARD NOTIFICATION\n\n"
                    + "Thank you for submitting a bid for the following tender:\n\n"
                    + "Tender Reference: " + tenderReference + "\n"
                    + "Tender Title: " + tenderTitle + "\n\n"
                    + "We regret to inform you that your bid was NOT SUCCESSFUL on this occasion.\n\n"
                    + "The tender has been awarded to another supplier based on the evaluation criteria.\n\n"
                    + "You may log in to the ProcureGov portal to view the award notice and see the winning bid amount.\n\n"
                    + "View Award Notice: " + awardNoticeUrl + "\n\n"
                    + "We appreciate your interest in working with the Ministry of Public Works and encourage you "
                    + "to participate in future tender opportunities.\n\n"
                    + "Best regards,\n"
                    + "Procurement Office\n"
                    + "Ministry of Public Works\n"
                    + "Kingdom of Lesotho\n"
                    + "Tel: +266 2232 1000\n"
                    + "Email: procurement@mpw.gov.ls";
        }

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends a simplified award notification email with minimal details. Uses
     * generic "Dear Supplier" salutation and shorter message content.
     *
     * @param recipientEmail The supplier's email address
     * @param tenderReference The tender reference number
     * @param won true if supplier won the bid, false otherwise
     * @param awardNoticeUrl URL to the award notice page
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendAwardNotification(String recipientEmail, String tenderReference,
            boolean won, String awardNoticeUrl) {

        String subject = "Tender Award Notification - " + tenderReference;
        String content;

        if (won) {
            content = "Dear Supplier,\n\n"
                    + "CONGRATULATIONS!\n\n"
                    + "Your bid for tender " + tenderReference + " has been SUCCESSFUL.\n\n"
                    + "View Award Notice: " + awardNoticeUrl + "\n\n"
                    + "Best regards,\n"
                    + "Ministry of Public Works";
        } else {
            content = "Dear Supplier,\n\n"
                    + "Thank you for your bid for tender " + tenderReference + ".\n\n"
                    + "We regret to inform you that your bid was NOT SUCCESSFUL on this occasion.\n\n"
                    + "View Award Notice: " + awardNoticeUrl + "\n\n"
                    + "Best regards,\n"
                    + "Ministry of Public Works";
        }

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends a new tender notification email to a supplier. Includes tender
     * details, category, estimated value, and closing date.
     *
     * @param recipientEmail The supplier's email address
     * @param recipientName The supplier's name
     * @param tenderReference The tender reference number
     * @param tenderTitle The tender title
     * @param category The tender category
     * @param estimatedValue The estimated value of the tender
     * @param closingDate The closing date for bid submissions
     * @param tenderUrl URL to the tender detail page
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendNewTenderNotification(String recipientEmail, String recipientName,
            String tenderReference, String tenderTitle,
            String category, String estimatedValue,
            String closingDate, String tenderUrl) {

        String subject = "New Tender Published - " + tenderReference;
        String content = "Dear " + recipientName + ",\n\n"
                + "NEW TENDER PUBLISHED\n\n"
                + "A new tender has been published that may be of interest to your company:\n\n"
                + "Tender Reference: " + tenderReference + "\n"
                + "Tender Title: " + tenderTitle + "\n"
                + "Category: " + category + "\n"
                + "Estimated Value: " + estimatedValue + "\n"
                + "Closing Date: " + closingDate + "\n\n"
                + "View Tender Details: " + tenderUrl + "\n\n"
                + "Please log in to the ProcureGov portal to download the full tender notice and submit your bid.\n\n"
                + "Best regards,\n"
                + "Procurement Office\n"
                + "Ministry of Public Works\n"
                + "Kingdom of Lesotho";

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends a bid confirmation email to a supplier after successful bid
     * submission. Includes tender details, bid amount, and submission date.
     *
     * @param recipientEmail The supplier's email address
     * @param recipientName The supplier's name
     * @param tenderReference The tender reference number
     * @param tenderTitle The tender title
     * @param bidAmount The bid amount submitted
     * @param submittedDate The date and time of submission
     * @param bidsUrl URL to view the supplier's bids
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendBidConfirmation(String recipientEmail, String recipientName,
            String tenderReference, String tenderTitle,
            String bidAmount, String submittedDate, String bidsUrl) {

        String subject = "Bid Confirmation - " + tenderReference;
        String content = "Dear " + recipientName + ",\n\n"
                + "BID CONFIRMATION\n\n"
                + "Your bid for the following tender has been successfully submitted:\n\n"
                + "Tender Reference: " + tenderReference + "\n"
                + "Tender Title: " + tenderTitle + "\n"
                + "Bid Amount: " + bidAmount + "\n"
                + "Submitted Date: " + submittedDate + "\n\n"
                + "You can track your bid status in the ProcureGov portal under 'My Bids'.\n\n"
                + "View Your Bids: " + bidsUrl + "\n\n"
                + "Best regards,\n"
                + "Ministry of Public Works";

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Sends an account lockout notification to a user after multiple failed
     * login attempts. Informs the user of the lockout duration and provides
     * contact information.
     *
     * @param recipientEmail The user's email address
     * @param recipientName The user's name
     * @param lockoutDuration Duration of lockout in minutes
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendAccountLockoutNotification(String recipientEmail, String recipientName,
            int lockoutDuration) {
        String subject = "ProcureGov - Account Security Alert";
        String content = "Dear " + recipientName + ",\n\n"
                + "SECURITY ALERT\n\n"
                + "Your ProcureGov account has been temporarily locked due to multiple "
                + "failed login attempts.\n\n"
                + "Lockout Duration: " + lockoutDuration + " minutes\n\n"
                + "If you did not attempt to log in, please contact the ICT Helpdesk immediately.\n\n"
                + "You may try logging in again after the lockout period expires, "
                + "or use the 'Forgot Password' option to reset your password.\n\n"
                + "Best regards,\n"
                + "ICT Security Team\n"
                + "Ministry of Public Works\n"
                + "Tel: +266 2232 1000";

        return sendEmail(recipientEmail, subject, content);
    }

    /**
     * Core method to send an email using JavaMail API. Configures SMTP
     * settings, authenticates with the SMTP server, and sends the message. Uses
     * TLS encryption on port 587 with connection timeout settings of 10
     * seconds.
     *
     * @param recipientEmail The recipient's email address; must not be null or
     * empty
     * @param subject The email subject line
     * @param content The email body content as plain text
     * @return true if email sent successfully, false if recipient email is
     * empty or sending fails
     */
    private static boolean sendEmail(String recipientEmail, String subject, String content) {

        // Validate recipient email
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            logger.warning("Cannot send email: Recipient email is empty");
            return false;
        }

        // Email configuration properties
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            /**
             * Provides password authentication for the SMTP session.
             *
             * @return PasswordAuthentication with sender email and password
             */
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        // Disable debug mode for production
        session.setDebug(false);

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);
            message.setSentDate(new java.util.Date());

            // Send message
            Transport.send(message);

            logger.info("Email sent successfully to: " + recipientEmail + " | Subject: " + subject);
            return true;

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send email to: " + recipientEmail + " - " + e.getMessage());
            return false;
        } catch (java.io.UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Encoding error sending email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tests the email configuration by sending a test email to a specified
     * recipient. Useful for verifying SMTP settings and credentials are
     * correct.
     *
     * @param testRecipient Email address to send the test email to
     * @return true if test email was sent successfully, false otherwise
     */
    public static boolean testEmailConfiguration(String testRecipient) {
        String subject = "ProcureGov - Email Configuration Test";
        String content = "This is a test email from the ProcureGov Tender Management System.\n\n"
                + "If you received this email, the email configuration is working correctly.\n\n"
                + "Sent at: " + new java.util.Date() + "\n\n"
                + "Ministry of Public Works, Lesotho";

        return sendEmail(testRecipient, subject, content);
    }
}
