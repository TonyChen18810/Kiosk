package com.example.kiosk.Helpers;
/**
 * PhoneNumberFormat.java
 *
 * Mostly used for the extract function
 *
 * Convert between (123) 456-7890 and 1234567890 formatting
 * for phone numbers
 */
public class PhoneNumberFormat {

    public static String formatPhoneNumber(String phoneNumber) {
        StringBuilder newNum = new StringBuilder();
        char[] charNum = phoneNumber.toCharArray();
        newNum.append("(");
        for (int i = 0; i < charNum.length; i++) {
            if (i == 2) {
                newNum.append(charNum[i]);
                newNum.append(")-");
            } else if (i == 5) {
                newNum.append(charNum[i]);
                newNum.append("-");
            } else {
                newNum.append(charNum[i]);
            }
        }
        return newNum.toString();
    }

    public static String extract(String formattedPhoneNumber) {
        StringBuilder newNum = new StringBuilder();
        char[] charNum = formattedPhoneNumber.toCharArray();
        for (char c : charNum) {
            if (c != '(' && c != ')' && c != '-' && c != ' ') {
                newNum.append(c);
            }
        }
        return newNum.toString();
    }
}
