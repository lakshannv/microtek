package model;

import hibernate.Customer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static String getValidatedName(String name) {
        name = name.trim();
        String[] words = name.split(" ");
        String processedName = "";
        for (String word : words) {
            if (word.length() <= 1) {
                word = word.toUpperCase();
            } else {
                word = word.substring(0, 1).toUpperCase() + word.substring(1);
            }
            processedName = processedName + " " + word;
        }
        return processedName.trim();
    }

    public static boolean isValidName(String name) {
        boolean b = false;
        if (!name.trim().isEmpty()) {
            Pattern p = Pattern.compile("[^A-Za-z0-9 -/]");
            Matcher m = p.matcher(name);
            b = !m.find();
        }
        return b;
    }

    public static boolean isValidCustomerName(String name) {
        boolean b = false;
        if (!name.trim().isEmpty()) {
            Pattern p = Pattern.compile("[^A-Za-z ]");
            Matcher m = p.matcher(name);
            b = !m.find();
        }
        return b;
    }

    public static boolean isValidEmail(String email) {

        Pattern ptn = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = ptn.matcher(email);

        return matcher.find();
    }

    public static String getNextProductImage(ArrayList<String> imgList, int i) {
        String img = "assets/img/products/def.png";
        if (!imgList.isEmpty()) {
            try {
                i = i - 1;
                img = "assets/img/products/" + imgList.get(i);
            } catch (Exception e) {
                img = "assets/img/products/" + imgList.get(0);
            }
        }
        return img;
    }

    public static String getCustomertImage(Customer c) {
        String img = "assets/img/avatars/avt.png";
        if (c.getImage() != null) {
            if (!c.getImage().isEmpty()) {
                img = "assets/img/avatars/" + c.getImage();
            }
        }
        return img;
    }

    final static String acceptableThirdDigits = "0125678";

    public static String getValidatedMobile(String mobile) throws Exception {
        boolean isValid = false;
        mobile = mobile.trim();
        String validatedMobile = "";
        if (mobile.length() == 10) {
            if (mobile.charAt(0) == '0') {
                try {
                    Integer.parseInt(mobile);
                    char[] a = acceptableThirdDigits.toCharArray();
                    for (int i = 0; i < a.length; i++) {
                        if (mobile.charAt(2) == a[i]) {
                            isValid = true;
                            validatedMobile = mobile;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isValid = false;
                }
            }
        } else if (mobile.length() == 9) {
            try {
                Integer.parseInt(mobile);
                char[] a = acceptableThirdDigits.toCharArray();
                for (int i = 0; i < a.length; i++) {
                    if (mobile.charAt(1) == a[i]) {
                        isValid = true;
                        validatedMobile = "94" + mobile;
                        break;
                    }
                }
            } catch (Exception e) {
                isValid = false;
            }

        } else if (mobile.length() == 11) {
            if (mobile.substring(0, 2).equals("94")) {
                mobile = mobile.substring(2);
                try {
                    Integer.parseInt(mobile);
                    char[] a = acceptableThirdDigits.toCharArray();
                    for (int i = 0; i < a.length; i++) {
                        if (mobile.charAt(1) == a[i]) {
                            isValid = true;
                            validatedMobile = "0" + mobile;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isValid = false;
                }
            }
        } else if (mobile.length() == 12) {
            if (mobile.substring(0, 3).equals("+94")) {
                mobile = mobile.substring(3);
                try {
                    Integer.parseInt(mobile);
                    char[] a = acceptableThirdDigits.toCharArray();
                    for (int i = 0; i < a.length; i++) {
                        if (mobile.charAt(1) == a[i]) {
                            isValid = true;
                            validatedMobile = "0" + mobile;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isValid = false;
                }
            }
        }
        if (!isValid) {
            throw new Exception();
        }
        return validatedMobile;
    }

    public static String getNth(int d) {
        if (d > 3 && d < 21) {
            return "<sup>th</sup>";
        }
        switch (d % 10) {
            case 1:
                return "<sup>st</sup>";
            case 2:
                return "<sup>nd</sup>";
            case 3:
                return "<sup>rd</sup>";
            default:
                return "<sup>th</sup>";
        }
    }
    
        public static String getNthSmall(int d) {
        if (d > 3 && d < 21) {
            return "<sup><small>th</small></sup>";
        }
        switch (d % 10) {
            case 1:
                return "<sup><small>st</small></sup>";
            case 2:
                return "<sup><small>nd</small></sup>";
            case 3:
                return "<sup><small>rd</small></sup>";
            default:
                return "<sup><small>th</small></sup>";
        }
    }

    public static String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(s.getBytes("UTF-8"));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hashedBytes.length; i++) {
                sb.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    private static int min = 1000000;
    private static int max = 9999999;

    public static String generateOTP() {
        return String.valueOf((int) (Math.random() * (max - min + 1) + min));
    }

    public static int getFulfilmentWithin(int within, String timeUnit) {
        switch (timeUnit) {
            case "Days":
                return 1 * within;
            case "Weeks":
                return 7 * within;
            case "Months":
                return 30 * within;
            default:
                return 7 * within;
        }
    }
    
        public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static void main(String[] args) {
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startT = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endT = cal.getTime();
        System.out.println(startT);
        System.out.println(endT);*/
        
        System.out.println("ok-".split("-").length);
    }
}
