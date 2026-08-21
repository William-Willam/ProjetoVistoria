package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.ClienteCadastroRequest;
import br.com.william.autovistorbackend.entity.Cliente;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void cadastrar_deveLancarExcecao_quandoCpfJaExiste() {
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(true);

        ClienteCadastroRequest request = new ClienteCadastroRequest(
                "Maria Silva", "12345678901", "61999999999", "maria@email.com", "senha12345");

        assertThatThrownBy(() -> clienteService.cadastrar(request))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("CPF");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoEmailJaExiste() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail("maria@email.com")).thenReturn(true);

        ClienteCadastroRequest request = new ClienteCadastroRequest(
                "Maria Silva", "12345678901", "61999999999", "maria@email.com", "senha12345");

        assertThatThrownBy(() -> clienteService.cadastrar(request))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("e-mail");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void cadastrar_deveCriptografarSenha_eNuncaSalvarTextoPuro() {
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senha12345")).thenReturn("hash-fake-abc123");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> {
            Cliente c = invocacao.getArgument(0);
            c.setId(1L);
            return c;
        });

        ClienteCadastroRequest request = new ClienteCadastroRequest(
                "Maria Silva", "12345678901", "61999999999", "maria@email.com", "senha12345");

        clienteService.cadastrar(request);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        Cliente clienteSalvo = captor.getValue();

        assertThat(clienteSalvo.getSenhaHash()).isEqualTo("hash-fake-abc123");
        assertThat(clienteSalvo.getSenhaHash()).isNotEqualTo("senha12345");
    }
}