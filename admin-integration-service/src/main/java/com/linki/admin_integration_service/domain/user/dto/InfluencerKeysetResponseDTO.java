package com.linki.admin_integration_service.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InfluencerKeysetResponseDTO {
    private List<InfluencerUserResponseDTO> list;
    private boolean hasNext;
    private int size;
    private String nextCursor;
    private String currentCursor;
    private int requestedSize;
} 