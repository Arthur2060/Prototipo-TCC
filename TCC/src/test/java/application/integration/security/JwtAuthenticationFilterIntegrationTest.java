package application.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.infraestructure.security.UsuarioDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UsuarioDetailService usuarioDetailService;

    @Test
    void deveRetornar405QuandoMetodoHttpNaoSuportado() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void devePermitirAcessoAoLoginSemTokenMesmoComSenhaInvalida() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarAcessoAEndpointProtegidoSemToken() throws Exception {
        mockMvc.perform(get("/estacionamento"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAcessoComTokenValido() throws Exception {
        String email = "rafael@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");

        UserDetails mockUser = User.withUsername(email)
                .password("encoded")
                .roles("ADMIN")
                .build();

        when(usuarioDetailService.loadUserByUsername(email)).thenReturn(mockUser);

        mockMvc.perform(get("/estacionamento")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}