package com.example.myapp.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String senderId;
    private String content;
    private String roomId;
}
