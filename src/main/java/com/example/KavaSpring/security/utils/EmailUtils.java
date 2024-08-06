package com.example.KavaSpring.security.utils;

public class EmailUtils {

    public static String extractName(String email) {
        String localPart = email.split("@")[0];

        String firstName;
        String lastName;

        if (localPart.contains(".")) {
            String[] nameParts = localPart.split("\\.");
            if (nameParts.length >= 2) {
                firstName = capitalize(nameParts[0]);
                lastName = capitalize(nameParts[1]);
            } else {
                firstName = "User";
                lastName = capitalize(nameParts[0]);
            }
        } else {
            firstName = "User";
            lastName = capitalize(localPart);
        }

        return firstName + " " + lastName;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}

