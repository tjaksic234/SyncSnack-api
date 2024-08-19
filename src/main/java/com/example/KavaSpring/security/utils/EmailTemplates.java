package com.example.KavaSpring.security.utils;

import org.springframework.beans.factory.annotation.Value;

public class EmailTemplates {

    @Value("${FRONTEND_URL}")
    private static String FRONTEND_URL;

    public static String confirmationEmail(String recipient, String verificationUrl) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email Confirmation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            padding-bottom: 20px;\n" +
                "        }\n" +
                "        .header img {\n" +
                "            width: 150px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #888888;\n" +
                "        }\n" +
                "        a {\n" +
                "            color: #1a73e8;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #1a73e8;\n" +
                "            color: #ffffff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <img src=\"https://kodelab.s3.eu-north-1.amazonaws.com/profilePhotos/logo.png?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEKb%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCmV1LW5vcnRoLTEiRzBFAiEA0TVQlzGfU7ALkklYMYRG8k8Cmu%2BTMw3lkT%2BEG4pa29ICICXyh3OgRGSBK0aFf9DajZ1PNeTlSZSUAWjSHrSDmzlWKu0CCJ%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEQABoMNjU0NjU0MzQ0ODUyIgxSTx6vL8PdNeOGiskqwQI2PetTKx874pC0ctZoHFUR%2FMNMYibDm7RNF9zsjiL11KUKtDOvpryJduyOL4TbBxjztuXBXc5QnDliYUFWWrdVsL7BzClltXCXQVOW3aMFJ8U2e9Am13Ug02g5cIZNGiwZR2gNLsSAkfcA2kMV7DOy7jsfrZWWi82cAYj7fK%2BqVckIbP1kXu2vfAhpaIEhTv8sQOhgHCK9304uLMQbfAmQBEJNrEZG%2B4cLMSQTDeUsLbVuUJaUc9U1103yvjhNScLz9iZccre7dYkQvjB23p2ttCNa3xs1HwycZyajs8hUX65e68wmoAy80Nh9UNyzqfQWXnsHfbdfT9bWpQCO2%2B2suvcY31%2BN9Ixka8SPOpCx%2F00ZNH2WraVfmG4ufYarj6bqIQRryuk70Fs43W128SLQMIpLGaEwc3TxnHq0%2Bbdo2GUwhOnrtQY6swJUCAmwl7XjAPLJAeXWTLSBnwAQWUgxUpWsovnCG6Xz2z9BE6KFubySJcIB8o4tN%2BcgMt2qBhXYIVgUng0M5URHt9m%2BHfcBSZ5XhdzsqntGy1KAelMWXFeL7RdqsBFuj3N8NtmuhBlLX%2FSg8ZLxWm6Q%2FKjeSChqRYySuF%2FWYndn%2FbrcIGOBpnlS%2BfuhB9YfxwIIbpid9gbjtKgw0ctoHoIgyKpddivaK4ks2kE7Xw07l7SM%2BB8l2WpsOfioI2T8ksUX85VUuuetj8TSEOjDhxIhpECGJ9QZ2sGLMwLqUwaTTY58pF7bfVPG%2B2riI3BkcoNmx2qW5SXpXcoXQ84NyEm9rj2dhIzZXznITogqov%2BMkaSKwPWtoeE5LUhvzBOueJTcJkvQeM8z8Lvyiluerr1ipxkV&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240813T061006Z&X-Amz-SignedHeaders=host&X-Amz-Expires=7200&X-Amz-Credential=ASIAZQ3DQR2KBFT6YQEV%2F20240813%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Signature=83b7dcb6a10f4f2fa478086151530e3d1886e144ec7d950828759d969cc3a16f\" alt=\"Company Logo\">\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h1>Email Confirmation</h1>\n" +
                "            <p>Dear " + EmailUtils.extractName(recipient) + ",</p>\n" +
                "            <p>Thank you for registering with SyncSnack. To complete your registration and verify your email address, please click the button below:</p>\n" +
                "            <a href=\"" + verificationUrl + "\" class=\"button\">Verify Email</a>\n" +
                "            <p>If you have any questions or need further assistance, please do not hesitate to <a href=\"mailto:support@example.com\">contact us</a>.</p>\n" +
                "            <p>Best regards,</p>\n" +
                "            <p>The SyncSnack Team</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 SyncSnack. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public static String emailVerified(String userId) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email Verified</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            padding: 20px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #888888;\n" +
                "        }\n" +
                "        a {\n" +
                "            color: #1a73e8;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #4CAF50;\n" +
                "            color: #ffffff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "        .icon {\n" +
                "            font-size: 48px;\n" +
                "            color: #4CAF50;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <script>\n" +
                "        setTimeout(function() {\n" +
                "            window.location.href = '" + FRONTEND_URL + "/setprofile?userId=" + userId + "';\n" +
                "        }, 10000);\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"icon\">✓</div>\n" +
                "            <h1>Email Verified Successfully!</h1>\n" +
                "            <p>Congratulations! Your email address has been successfully verified.</p>\n" +
                "            <p>You can now enjoy full access to all features of SyncSnack.</p>\n" +
                "            <p>You will be automatically redirected to the login page in 10 seconds.</p>\n" +
                "            <a href=\"" + FRONTEND_URL + "/setprofile?userId=" + userId + "\" class=\"button\">Log In to Your Account</a>\n" +
                "            <p>If you have any questions or need assistance, please don't hesitate to <a href=\"mailto:support@syncsnack.com\">contact our support team</a>.</p>\n" +
                "            <p>Thank you for choosing SyncSnack!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 SyncSnack. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String resetPassword(String activationUrl) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reset Your Password</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                "        .container { background-color: #f9f9f9; border-radius: 5px; padding: 20px; }\n" +
                "        h1 { color: #2c3e50; }\n" +
                "        .btn { display: inline-block; background-color: #3498db; color: #ffffff !important; text-decoration: none; padding: 10px 20px; border-radius: 5px; margin-top: 20px; }\n" +
                "        .footer { margin-top: 20px; font-size: 12px; color: #777; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Reset Your Password</h1>\n" +
                "        <p>Hello,</p>\n" +
                "        <p>We received a request to reset your password. If you didn't make this request, you can ignore this email.</p>\n" +
                "        <p>To reset your password, please click the button below:</p>\n" +
                "        <a href=\"" + activationUrl + "\" class=\"btn\">Reset Password</a>\n" +
                "        <p>If the button doesn't work, you can copy and paste the following link into your browser:</p>\n" +
                "        <p>" + activationUrl + "</p>\n" +
                "        <p>This link will expire in 24 hours for security reasons.</p>\n" +
                "        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>\n" +
                "        <p>Best regards,<br>Your SyncSnack Team</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "        <p>This email was sent to you as part of our password reset process. If you didn't request a password reset, please ignore this email or contact us if you have concerns.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

}
