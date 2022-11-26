package com.example.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    @Autowired
    private TemplateEngine templateEngine;

    public String getMessage(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mail", context);
    }

}
