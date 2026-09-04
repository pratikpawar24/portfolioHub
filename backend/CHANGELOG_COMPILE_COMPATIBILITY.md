# Compile Compatibility Fix

Fixes reported by the developer against Spring Boot 4 / Spring Framework 7:

- Replace deprecated `HttpStatus.UNPROCESSABLE_ENTITY` with `HttpStatus.UNPROCESSABLE_CONTENT`.
- Replace `jakarta.transaction.Transactional` with Spring's `org.springframework.transaction.annotation.Transactional` where `readOnly` is required.
- Iterate Jackson `ObjectNode.fieldNames()` with `Iterator` instead of enhanced `for`.
- Use `UrlBasedCorsConfigurationSource` as the concrete CORS source type so `registerCorsConfiguration` is available.

Apply these files over the existing backend source tree.
