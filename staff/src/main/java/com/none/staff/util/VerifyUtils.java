package com.none.staff.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtils {

    /**
     * 检测是不是数字
     */
    public static boolean isNum(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

    public static boolean isExpenseNumber(String str) {
        Pattern pattern = Pattern.compile("^E[0-9]{9}$");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }
}
