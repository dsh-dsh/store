package com.example.store.controllers;

import com.example.store.model.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/hello")
    public void greeting(MessageDTO messageDTO) throws Exception {
        System.out.println(messageDTO);
    }
}
