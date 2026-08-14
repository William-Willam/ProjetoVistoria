package br.com.william.autovistorbackend.security;

import br.com.william.autovistorbackend.entity.Cliente;
import br.com.william.autovistorbackend.entity.Funcionario;
import br.com.william.autovistorbackend.repository.ClienteRepository;
import br.com.william.autovistorbackend.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Cliente cliente = clienteRepository.findByEmail(email).orElse(null);
        if (cliente != null) {
            return new AutenticadoPrincipal(
                    cliente.getId(), cliente.getEmail(), cliente.getSenhaHash(),
                    "CLIENTE", "CLIENTE"
            );
        }

        Funcionario funcionario = funcionarioRepository.findByEmail(email).orElse(null);
        if (funcionario != null) {
            return new AutenticadoPrincipal(
                    funcionario.getId(), funcionario.getEmail(), funcionario.getSenhaHash(),
                    "FUNCIONARIO", funcionario.getCargo().name() // "VISTORIADOR" ou "GERENTE"
            );
        }

        throw new UsernameNotFoundException("E-mail ou senha inválidos.");
    }
}