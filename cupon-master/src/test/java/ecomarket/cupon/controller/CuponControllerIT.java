package ecomarket.cupon.controller;


import ecomarket.cupon.model.Cupon;
import ecomarket.cupon.repository.CuponRepository;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CuponControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarBd() {
        cuponRepository.deleteAll();
    }

    @Test
    void testCrearYListarCupon() throws Exception {
        Cupon nuevo = new Cupon(null, "VERANO10", 10.0, true, LocalDate.of(2026, 12, 31));

        mockMvc.perform(post("/api/v1/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCupon").exists())
                .andExpect(jsonPath("$.codigo").value("VERANO10"));

        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("VERANO10"))
                .andExpect(jsonPath("$[0].porcentajeDescuento").value(10.0));
    }

    @Test
    void testBuscarPorCodigo() throws Exception {
        cuponRepository.save(new Cupon(null, "BUSCAME", 5.0, true, null));

        mockMvc.perform(get("/api/v1/cupones/codigo/BUSCAME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeDescuento").value(5.0));
    }

    @Test
    void testValidarCuponActivo() throws Exception {
        cuponRepository.save(new Cupon(null, "OK", 10.0, true, LocalDate.now().plusDays(10)));

        mockMvc.perform(get("/api/v1/cupones/validar/OK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testValidarCuponVencido() throws Exception {
        cuponRepository.save(new Cupon(null, "VIEJO", 10.0, true, LocalDate.now().minusDays(1)));

        mockMvc.perform(get("/api/v1/cupones/validar/VIEJO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void testActualizarCupon() throws Exception {
        Cupon guardado = cuponRepository.save(new Cupon(null, "VERANO10", 10.0, true, null));

        Cupon datos = new Cupon(null, "VERANO10", 25.0, false, null);

        mockMvc.perform(put("/api/v1/cupones/" + guardado.getIdCupon())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeDescuento").value(25.0))
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void testEliminarCupon() throws Exception {
        Cupon guardado = cuponRepository.save(new Cupon(null, "BORRAME", 10.0, true, null));

        mockMvc.perform(delete("/api/v1/cupones/" + guardado.getIdCupon()))
                .andExpect(status().isNoContent());

        // tras borrarlo, obtenerlo devuelve 204 (asi responde tu controller)
        mockMvc.perform(get("/api/v1/cupones/" + guardado.getIdCupon()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCodigoDuplicadoEnIntegracion() throws Exception {
    cuponRepository.save(new Cupon(null, "REPETIDO", 10.0, true, null));
    Cupon segundo = new Cupon(null, "REPETIDO", 20.0, true, null);
    mockMvc.perform(post("/api/v1/cupones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(segundo)))
            .andExpect(status().isConflict());
}
}