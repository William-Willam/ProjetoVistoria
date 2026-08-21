package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.config.SecurityConfig;
import br.com.william.autovistorbackend.security.AutenticacaoService;
import br.com.william.autovistorbackend.security.JwtAuthFilter;
import br.com.william.autovistorbackend.security.JwtService;
import br.com.william.autovistorbackend.service.FuncionarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtAuthFilter.class, AuthenticationConfiguration.class})
class FuncionarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private FuncionarioService funcionarioService;
    @MockitoBean private AutenticacaoService autenticacaoService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private PasswordEncoder passwordEncoder;

    @Test
    void listarTodos_deveNegarAcesso_quandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/funcionarios"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void listarTodos_deveRetornar403_quandoPerfilNaoAutorizado() throws Exception {
        mockMvc.perform(get("/funcionarios").with(user("cliente@teste.com").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodos_deveRetornar200_quandoGerente() throws Exception {
        mockMvc.perform(get("/funcionarios").with(user("gerente@teste.com").roles("GERENTE")))
                .andExpect(status().isOk());
    }
}