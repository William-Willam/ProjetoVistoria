package br.com.william.autovistorbackend.exception;

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message) {
        super(message);
    }
}