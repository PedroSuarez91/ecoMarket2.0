package ecomarket.carro_ms.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.carro_ms.model.Carro;
import ecomarket.carro_ms.model.ProductoDTO;
import ecomarket.carro_ms.model.UsuarioDTO;
import ecomarket.carro_ms.repository.CarroRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarroControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarroRepository carroRepository;

    @SuppressWarnings("removal")
    @MockitoBean
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        carroRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerCarro() throws Exception {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(10L);
        producto.setNombre("Manzana");
        producto.setPrecioUnitario("500.0");

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);

        String body = "{ \"idUsuario\": 1, \"items\": [ { \"idProducto\": 10, \"cantidad\": 2 } ] }";

        mockMvc.perform(post("/api/v1/carros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCarro").exists())
                .andExpect(jsonPath("$.nombreUsuario").value("Juan Perez"))
                .andExpect(jsonPath("$.subtotal").value(1000.0))
                .andExpect(jsonPath("$.total").value(1000.0));

        mockMvc.perform(get("/api/v1/carros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUsuario").value("Juan Perez"));
    }

    @Test
    void testEliminarCarro() throws Exception {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        Carro guardado = carroRepository.save(carro);

        mockMvc.perform(delete("/api/v1/carros/" + guardado.getIdCarro()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/carros/" + guardado.getIdCarro()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testActualizarCarro() throws Exception {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setTipoEntrega("RETIRO");
        Carro guardado = carroRepository.save(carro);

        String body = "{ \"tipoEntrega\": \"DESPACHO\", \"idDireccion\": 5 }";

        mockMvc.perform(put("/api/v1/carros/" + guardado.getIdCarro())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEntrega").value("DESPACHO"))
                .andExpect(jsonPath("$.idDireccion").value(5L));
    }
}
