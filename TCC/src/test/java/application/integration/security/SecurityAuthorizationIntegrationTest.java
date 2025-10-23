package application.integration.security;


import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.infraestructure.security.UsuarioDetailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {
    /*TODO*/
    //PERMISSÕES:
    //ADMIN: TUDO
    //DONO: TUDO EM ESTACIONAMENTO, VALOR E GERENTE, DEVE SOMENTE ACEITAR OU RECUSAR RESERVAS
    //GERENTE: DETERMINADAS FUNÇÕES EM ESTACIONAMENTO E VALOR(determinados na documentação), DEVE SOMENTE ACEITAR OU RECUSAR RESERVAS
    //CLIENTE: TUDO EM CARRO E AVALIAÇÃO, DEVE SOMENTE SOLICITAR E CANCELAR RESERVAS
    //OUTRAS CLASSES NÃO SÃO MANIPULADAS DIRETAMENTE POR USUÁRIOS(Acesso por exemplo)

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Mock
    private UsuarioDetailService usuarioDetailsService;

//    @Mock
//    private ProfessorService professorService;

    private void mockUsuario(String email, String role) {
        UserDetails user = User.withUsername(email)
                .password("encoded")
                .roles(role)
                .build();
        when(usuarioDetailsService.loadUserByUsername(email)).thenReturn(user);
    }

    @Test
    void devePermitirPostParaAdmin() throws Exception {
        String email = "admin@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");
        mockUsuario(email, "ADMIN");

        String body = """
        {
            "nome": "Professor Teste",
            "cpf": "111.222.333-44",
            "email": "professor@senai.com",
            "senha": "123456"
        }
        """;

        mockMvc.perform(post("/professores")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarPostParaProfessor() throws Exception {
        String email = "prof@senai.com";
        String token = jwtService.generateToken(email, "PROFESSOR");
        mockUsuario(email, "PROFESSOR");

        mockMvc.perform(post("/professores")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirGetParaProfessor() throws Exception {
        String email = "prof@senai.com";
        String token = jwtService.generateToken(email, "PROFESSOR");
        mockUsuario(email, "PROFESSOR");

        mockMvc.perform(get("/professores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirGetParaAdmin() throws Exception {
        String email = "admin@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");
        mockUsuario(email, "ADMIN");

        mockMvc.perform(get("/professores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/professores"))
                .andExpect(status().isForbidden());
    }
}