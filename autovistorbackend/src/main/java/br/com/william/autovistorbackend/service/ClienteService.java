package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.ClienteAtualizacaoRequest;
import br.com.william.autovistorbackend.dto.ClienteCadastroRequest;
import br.com.william.autovistorbackend.dto.ClienteResponse;
import br.com.william.autovistorbackend.entity.Cliente;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClienteResponse cadastrar(ClienteCadastroRequest request) {
        if (clienteRepository.existsByCpf(request.cpf())) {
            throw new RecursoDuplicadoException("Já existe um cliente cadastrado com este CPF.");
        }
        if (clienteRepository.existsByEmail(request.email())) {
            throw new RecursoDuplicadoException("Já existe um cliente cadastrado com este e-mail.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setCpf(request.cpf());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setSenhaHash(passwordEncoder.encode(request.senha()));

        Cliente salvo = clienteRepository.save(cliente);
        return toResponse(salvo);
    }

    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
        return toResponse(cliente);
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteAtualizacaoRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());

        return toResponse(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
    }
}