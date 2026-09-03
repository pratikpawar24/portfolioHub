package com.portfoliohub.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class HealthController {

    @Operation(
            summary = "Application ping",
            description = "Stable application-owned endpoint used by clients and smoke checks to verify API reachability."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "API is reachable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PingResponse.class),
                            examples = @ExampleObject(value = "{\"service\":\"portfoliohub-backend\",\"status\":\"ok\"}")
                    )
            )
    })
    @GetMapping("/ping")
    public PingResponse ping() {
        return new PingResponse("portfoliohub-backend", "ok");
    }

    public record PingResponse(String service, String status) {
    }
}
