package br.com.william.autovistordesktop.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Long extrairId(String token) {
        JsonNode claims = decodificarPayload(token);
        return claims.get("id").asLong();
    }

    private static JsonNode decodificarPayload(String token) {
        try {
            String payloadBase64 = token.split("\\.")[1];
            byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadBase64);
            String payloadJson = new String(payloadBytes, StandardCharsets.UTF_8);
            return MAPPER.readTree(payloadJson);
        } catch (Exception e) {
            throw new ApiException("Token inválido.");
        }
    }
}