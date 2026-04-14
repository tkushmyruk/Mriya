package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.example.domain.nosql.OwnerType;
import java.util.List;

@Data
@Builder
public class PostCreateRequest {
    private Long ownerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OwnerType ownerType;
    private String content;
    private List<String> tags;
}