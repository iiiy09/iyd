package com.iyd.dto;

import lombok.Data;

@Data
public class NoteDTO {
    private String title;
    private String content;
    private String ocrImage;
    private Long folderId;
    private Integer noteType;
}
