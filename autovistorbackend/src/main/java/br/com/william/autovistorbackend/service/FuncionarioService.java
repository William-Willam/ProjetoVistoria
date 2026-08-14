package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.*;
import br.com.william.autovistorbackend.entity.DesligamentoFuncionario;
import br.com.william.autovistorbackend.entity.Funcionario;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.DesligamentoFuncionarioRepository;
import br.com.william.autovistorbackend.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final DesligamentoFuncionarioRepository desligamentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public FuncionarioResponse cadastrar(FuncionarioCadastroRequest request) {
        if (funcionarioRepository.existsByMatricula(request.matricula())) {
            throw new RecursoDuplicadoException("Já existe um funcionário com esta matrícula.");
        }
        if (funcionarioRepository.existsByEmail(request.email())) {
            throw new RecursoDuplicadoException("Já existe um funcionário com este e-mail.");
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(request.nome());
        funcionario.setEmail(request.email());
        funcionario.setMatricula(request.matricula());
        funcionario.setSenhaHash(passwordEncoder.encode(request.senha()));
        funcionario.setCargo(request.cargo());

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return toResponse(salvo);
    }

    public FuncionarioResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public FuncionarioResponse atualizar(Long id, FuncionarioAtualizacaoRequest request) {
        Funcionario funcionario = buscarEntidadePorId(id);
        funcionario.setNome(request.nome());
        funcionario.setEmail(request.email());
        return toResponse(funcionario);
    }

    @Transactional
    public void desligar(Long id, DesligamentoRequest request) {
        Funcionario funcionario = buscarEntidadePorId(id);

        // 1. snapshot do histórico ANTES de qualquer alteração/exclusão
        DesligamentoFuncionario desligamento = new DesligamentoFuncionario();
        desligamento.setIdFuncionario(funcionario.getId());
        desligamento.setNomeFuncionario(funcionario.getNome());
        desligamento.setMatricula(funcionario.getMatricula());
        desligamento.setMotivo(request.motivo());
        desligamentoRepository.save(desligamento);

        // 2. só então o funcionário é excluído
        funcionarioRepository.delete(funcionario);
    }

    private Funcionario buscarEntidadePorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));
    }

    private FuncionarioResponse toResponse(Funcionario f) {
        return new FuncionarioResponse(f.getId(), f.getNome(), f.getEmail(), f.getMatricula(), f.getCargo(), f.isAtivo());
    }
}