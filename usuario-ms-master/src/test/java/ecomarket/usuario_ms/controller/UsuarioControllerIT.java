package ecomarket.usuario_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.usuario_ms.model.Usuario;
import ecomarket.usuario_ms.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "Max", "Power", "max@mail.com", "1234", "ADMIN");

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").exists())
                .andExpect(jsonPath("$.nombre").value("Max"));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Max"))
                .andExpect(jsonPath("$[0].apellido").value("Power"))
                .andExpect(jsonPath("$[0].emailUsuario").value("max@mail.com"))
                .andExpect(jsonPath("$[0].rol").value("ADMIN"));
    }

    @Test
    void testEliminarUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "Firulais", "Lopez", "firu@mail.com", "1234", "CLIENTE");
        Usuario guardado = usuarioRepository.save(usuario);

        mockMvc.perform(delete("/api/v1/usuarios/" + guardado.getIdUsuario()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/usuarios/" + guardado.getIdUsuario()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "Rocky", "Diaz", "rocky@mail.com", "1234", "CLIENTE");
        Usuario guardado = usuarioRepository.save(usuario);

        Usuario actualizado = new Usuario(null, "Rocky", "Diaz", "rocky@mail.com", "5678", "ADMIN");

        mockMvc.perform(put("/api/v1/usuarios/" + guardado.getIdUsuario())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }
}
