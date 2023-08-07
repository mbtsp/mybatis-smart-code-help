package com.mybatis.freemarker;

import com.mybatis.utils.FileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

public class TemplateUtil {
    private static final Configuration configuration;
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    static {
        configuration = new Configuration(Configuration.getVersion());
        try {
            configuration.setClassLoaderForTemplateLoading(TemplateUtil.class.getClassLoader(), "templates");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);

    }

    public static String processToString(final String templateName, final Object root) {
        try {
            Template template = TemplateUtil.configuration.getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(root, out);
            String s = out.toString();
            s = s.replaceAll("\r\n", "\n");
            return s;
        } catch (Exception e) {
            log.error("template process catch exception", e);
            throw new RuntimeException("process freemarker template catch exception", e);
        }
    }

}
