package br.com.william.autovistordesktop.client;

import br.com.william.autovistordesktop.session.SessaoUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public <T> T post(String path, Object corpo, Class<T> tipoResposta, boolean autenticado) {
        try {
            String json = objectMapper.writeValueAsString(corpo);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));

            if (autenticado) {
                builder.header("Authorization", "Bearer " + SessaoUsuario.getInstancia().getToken());
            }

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return tratarResposta(response, tipoResposta);

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Falha de comunicação com o servidor: " + e.getMessage());
        }
    }

    public <T> T put(String path, Object corpo, Class<T> tipoResposta) {
        try {
            String json = objectMapper.writeValueAsString(corpo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessaoUsuario.getInstancia().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return tratarResposta(response, tipoResposta);

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Falha de comunicação com o servidor: " + e.getMessage());
        }
    }

    public <T> T get(String path, Class<T> tipoResposta) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Authorization", "Bearer " + SessaoUsuario.getInstancia().getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return tratarResposta(response, tipoResposta);

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Falha de comunicação com o servidor: " + e.getMessage());
        }
    }

    private <T> T tratarResposta(HttpResponse<String> response, Class<T> tipoResposta) {
        try {
            int status = response.statusCode();

            if (status >= 200 && status < 300) {
                if (tipoResposta == Void.class || response.body().isBlank()) {
                    return null;
                }
                return objectMapper.readValue(response.body(), tipoResposta);
            }

            var erro = objectMapper.readTree(response.body());
            String mensagem = erro.has("mensagem") ? erro.get("mensagem").asText() : "Erro desconhecido.";
            throw new ApiException(mensagem, status);

        } catch (IOException e) {
            throw new ApiException("Erro ao interpretar resposta do servidor.");
        }
    }

    public byte[] getArquivo(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Authorization", "Bearer " + SessaoUsuario.getInstancia().getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }

            throw new ApiException("Erro ao baixar arquivo (status " + response.statusCode() + ").");

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Falha de comunicação com o servidor: " + e.getMessage());
        }
    }

    public void postMultipart(String path, File arquivo, String descricao) {
        try {
            String boundary = "----AutoVistorBoundary" + System.currentTimeMillis();
            byte[] arquivoBytes = Files.readAllBytes(arquivo.toPath());
            String nomeArquivo = arquivo.getName();

            var bodyBuilder = new java.io.ByteArrayOutputStream();
            String quebra = "\r\n";

            bodyBuilder.write(("--" + boundary + quebra).getBytes());
            bodyBuilder.write(("Content-Disposition: form-data; name=\"arquivo\"; filename=\"" + nomeArquivo + "\"" + quebra).getBytes());
            bodyBuilder.write(("Content-Type: application/octet-stream" + quebra + quebra).getBytes());
            bodyBuilder.write(arquivoBytes);
            bodyBuilder.write(quebra.getBytes());

            if (descricao != null && !descricao.isBlank()) {
                bodyBuilder.write(("--" + boundary + quebra).getBytes());
                bodyBuilder.write(("Content-Disposition: form-data; name=\"descricao\"" + quebra + quebra).getBytes());
                bodyBuilder.write((descricao + quebra).getBytes());
            }

            bodyBuilder.write(("--" + boundary + "--" + quebra).getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Authorization", "Bearer " + SessaoUsuario.getInstancia().getToken())
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBuilder.toByteArray()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException("Erro ao enviar foto (status " + response.statusCode() + ").");
            }

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Falha ao enviar foto: " + e.getMessage());
        }
    }
}