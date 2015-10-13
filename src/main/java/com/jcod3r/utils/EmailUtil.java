package com.jcod3r.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailUtil {

    public static void sendSimpleEmail(String subject, String msg)
            throws EmailException {
        SimpleEmail email = new SimpleEmail();

        email.setHostName("smtp.163.com");
        email.setAuthentication("scriptboy@163.com", "yangyang");
        email.setFrom("scriptboy@163.com");
        email.addTo("root@jcod3r.com");
        email.setCharset("UTF-8");
        email.setSubject(subject);
        email.setMsg(msg);

        email.send();
    }

}