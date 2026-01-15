package com.example.myapp.controller;

import com.example.myapp.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("/chat.{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public String chat(@DestinationVariable String roomId, ChatMessageDto message) {
        return message.getContent();
    }
}
