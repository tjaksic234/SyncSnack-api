package com.example.KavaSpring.security.utils;

public class EmailTemplates {

    public static String confirmationEmail(String recipient) {
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
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <img src=\"https://kodelab.s3.eu-north-1.amazonaws.com/profilePhotos/logo.png?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEP7%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCmV1LW5vcnRoLTEiRjBEAiBH4b6kHO4TTQmpfoAMqpr99kpUBBZ2Gem4oTdhrW2V6AIgKhugbsVBT8H1MpY28v6Gp2XVP2qXqs5Ch0tCIrdjAHUq7QII5%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARAAGgw2NTQ2NTQzNDQ4NTIiDI9D0xtI9a2DsFhv1yrBAp1M2T5YkiVCb3gJiWajpYmf5L9%2FBgV%2FjjjBERgDsNMAV6bhQ6lU2OtJQM6ehAFkCkfBpqBx3dLSPCoWwXKJq1VzSLBYGzl1LKNfs9TUGOXd12R9%2FFt%2BZVO%2BvT%2FEU%2BkWeK3aBMjxcul2ymd5oulfCjnAbX4sn0zAgTnQjTvFhfoaGAs6em%2BcKyzqVM8lM1l4BnfZsdn5hk4GtixORxZLHOoQkW%2BfrX9Zy09GuD9mk%2Bsh22aLMAnL4GoMv1y3eLIJh3PMLaj2cbjVRoOqVF1V4R8CCP1PQ%2F74c%2FA4ycONBfRllk137jPfdFeq2NnoMQl2bNCKCRA%2B6HCigwZE27E%2BpOz%2BedzECewgy6nJj9aTn0aV3WGF9Ri946cjPEmAwgAyuN6oiDwH5MQ3iqGa8h4Gfe%2BIUyNYANtzQFumISa1WcvE0jD89sa1Bjq0Aj2rDph%2B%2FCLzhXdyHCCNQ%2FzxPBC7%2BJNLXv60OmTeHsiFpynjEj5r44lWa%2F4NSxfXfP31DUX5LppQcmMzfdw3JS8pop5dKn1qRLugenHz3ePUootLwf9mM5Hp1vIMG7R6rhBuaFTCgXa5N%2BPtWx4hAp8VB7cIx82jEcazTgZ4pg2gt7XrKVanKnFSMe3KZmUV5RDznCKXC1%2FzqkHt1ohDhQY2acL0L9K%2BXFf7LysvBQPwJaVY7H4LO2KWToY1Ph4tdNsjsUl4OpqDBroj3sKrthmU%2Fp1jYWhz2BCMDdWTZx9B9RJJSpCOtBwM2%2FqsNa%2FXLsRgq%2BcDRxP6bnH7CayJqE8wvbXufuMT9ExUsE08MRf%2BRHn4Dujs2D5f3JTxhfEw1%2Bc7IkuL%2BFkbI2s%2Fi62eMVcDsx4C&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240806T102034Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIAZQ3DQR2KNALFEMFN%2F20240806%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Signature=b496dcebb4a207b8ae0ac09d1acadffe11cc211e9f6487d5967f2fa06089f861\" alt=\"Company Logo\">\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h1>Confirmation Email</h1>\n" +
                "            <p>Dear " + EmailUtils.extractName(recipient) + ",</p>\n" +
                "            <p>Thank you for your recent action on our platform. This is a confirmation email to let you know that we have received your request.</p>\n" +
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
}
