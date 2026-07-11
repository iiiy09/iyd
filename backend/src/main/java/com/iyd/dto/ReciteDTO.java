package com.iyd.dto;

import lombok.Data;

@Data
public class ReciteDTO {
    private String originalText;
    private String userAnswer;
    private String handwrittenImage;
    private String reciteVideo;
    private Integer checkType;
    private String subject;
}
