package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
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
public class DonoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarDonoValido() throws Exception {
        var dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        mockMvc.perform(
                        post("/dono")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Pedro"));
    }

    @Test
    void deveAtualizarDono() throws Exception {
        var dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        var salvo = mockMvc.perform(
                post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(salvo, DonoResponse.class);

        var atualizado = new DonoRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        mockMvc.perform(
                        put("/dono/" + donoSalvo.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(atualizado))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Arthur"));
    }

    @Test
    void deveDeletarDono() throws Exception {
        var dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        var salvo = mockMvc.perform(
                post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var dono = objectMapper.readValue(salvo, DonoResponse.class);

        mockMvc.perform(delete("/dono/" + dono.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarDonoPorId() throws Exception {
        var dto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        var salvo = mockMvc.perform(
                post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var dono = objectMapper.readValue(salvo, DonoResponse.class);

        mockMvc.perform(get("/dono/" + dono.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Pedro"));
    }

    @Test
    void deveRetornarErroAoBuscarDonoInexistente() throws Exception {
        mockMvc.perform(get("/dono/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarDonoInexistente() throws Exception {
        mockMvc.perform(delete("/dono/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Dono buscado não cadastrado no sistema"));
    }
}
