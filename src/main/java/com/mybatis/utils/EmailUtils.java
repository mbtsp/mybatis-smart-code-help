package com.mybatis.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailUtils {
    private static final String HOSTNAME = "smtp.qq.com";
    private static final String CHARSET = "UTF-8";
    private static final String USER_NAME = "zoulejiu@qq.com";
    private static final String PASS_WORD = "psrdrlggoxcocacg";

    public static String coverPluginErrorHtml(String pluginName, String version, String actionId, String errorMsg) {
        return "<html dir=\"ltr\" lang=\"zh\" class=\"focus-outline-visible\"><head>\n" +
                "  <body style=\"background-color: rgb(255, 255, 255);\">\n" +
                "    <p>" + pluginName + "(" + version + ")" + "</p>\n" +
                "    <p>触发点:" + actionId + "</p>\n" +
                "<h4>异常信息</h4>\n" +
                "<p>\n" + errorMsg +
                "</p>\n" +
                "</body></html>";
    }

    public static void sendHtml(String subject, String html, String... to) {
        HtmlEmail htmlEmail = new HtmlEmail();
        try {
            htmlEmail.setHtmlMsg(html);
            htmlEmail.setHostName(HOSTNAME);
            htmlEmail.setCharset(CHARSET);
            htmlEmail.setAuthentication(USER_NAME, PASS_WORD);
            htmlEmail.setFrom(USER_NAME, "Mybatis Smart Code Help");
            htmlEmail.setSubject(subject);
            htmlEmail.setTextMsg("Your email client does not support HTML messages");
            htmlEmail.addTo(to);
            htmlEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
