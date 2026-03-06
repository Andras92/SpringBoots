package com.buci.DaniTanit.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PublishDto {
    private Long id;
    private Long bookId;
    private Long authorId;
    private LocalDateTime publishDate;
}
