package ecomarket.factura_ms.controller;

import ecomarket.factura_ms.model.Factura;
import ecomarket.factura_ms.model.PedidoDTO;
import ecomarket.factura_ms.repository.FacturaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FacturaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaRepository facturaRepository;
    @MockitoBean
    private RestTemplate restTemplate;

    @BeforeEach
    void limpiarBd() {
        facturaRepository.deleteAll();
    }

    @Test
    void testEmitirFacturaFlujoReal() throws Exception {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(1L);
        pedido.setNombreCliente("Juan Perez");
        pedido.setTotal(11900.0);

        Mockito.when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/facturas/emitir/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFactura").exists())
                .andExpect(jsonPath("$.neto").value(10000))
                .andExpect(jsonPath("$.iva").value(1900))
                .andExpect(jsonPath("$.total").value(11900));
        assertThat(facturaRepository.findByIdPedido(1L)).isPresent();
    }


    @Test
    void testEmitirDuplicadoDevuelve409() throws Exception {
        facturaRepository.save(new Factura(null, 1L, "Ana", LocalDate.now(), 10000, 1900, 11900));

        mockMvc.perform(post("/api/v1/facturas/emitir/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void testEmitirPedidoInexistenteDevuelve404() throws Exception {
        Mockito.when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/facturas/emitir/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListarYObtener() throws Exception {
        Factura guardada = facturaRepository.save(
                new Factura(null, 7L, "Pedro", LocalDate.now(), 10000, 1900, 11900));

        mockMvc.perform(get("/api/v1/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Pedro"));

        mockMvc.perform(get("/api/v1/facturas/" + guardada.getIdFactura()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(7L));
    }

    @Test
    void testGetPorPedido() throws Exception {
        facturaRepository.save(new Factura(null, 5L, "Sofia", LocalDate.now(), 10000, 1900, 11900));

        mockMvc.perform(get("/api/v1/facturas/pedido/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente").value("Sofia"));
    }
}