package com.iyd.dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private String content;
    private String imageUrl;
    private String subject;
    private String stage;
    private String type;
}
