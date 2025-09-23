package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadatrarClienteValido() throws Exception {
        var dto = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                new java.util.Date()
        );

        mockMvc.perform(
                post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
        ).andExpect(status().isCreated()).andExpect(jsonPath("$.nome").value("Pedro"));
    }
    @Test
    void deveAtualizarUsuario() throws Exception{
        var dto = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                new java.util.Date()
        );

        var salvo = mockMvc.perform(
                post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var usuarioSalvo = objectMapper.readValue(salvo, ClienteResponse.class);

        var atualizado = new ClienteRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                new java.util.Date()
        );

        mockMvc.perform(
                put("/cliente/"+usuarioSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))
        ).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Arthur"));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        var dto = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                new java.util.Date()
        );
        var salvo = mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        var usuario = objectMapper.readValue(salvo, ClienteResponse.class);

        mockMvc.perform(delete("/cliente/" + usuario.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/cliente/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Cliente buscado não cadastrado no sistema"));
    }
}