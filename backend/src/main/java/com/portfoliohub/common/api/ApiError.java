package com.portfoliohub.common.api;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        String code,
        String message,
        String requestId,
        List<Detail> details,
        OffsetDateTime timestamp
) {
    public record Detail(String field, String reason) {}
}
