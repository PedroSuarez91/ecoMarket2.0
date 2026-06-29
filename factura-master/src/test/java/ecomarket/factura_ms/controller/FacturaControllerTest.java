package ecomarket.factura_ms.controller;

import ecomarket.factura_ms.model.Factura;
import ecomarket.factura_ms.service.FacturaService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacturaController.class)
@ActiveProfiles("test")
public class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    @Test
    void testGetFacturasConContenido() throws Exception {
        Factura f1 = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        Factura f2 = new Factura(2L, 6L, "Luis", LocalDate.now(), 20000, 3800, 23800);
        Mockito.when(facturaService.listar()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(get("/api/v1/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreCliente", is("Ana")))
                .andExpect(jsonPath("$[1].total", is(23800)));
    }

    @Test
    void testGetFacturasVacioDevuelve204() throws Exception {
        Mockito.when(facturaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/facturas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFacturaExistente() throws Exception {
        Factura f = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        Mockito.when(facturaService.findById(1L)).thenReturn(Optional.of(f));

        mockMvc.perform(get("/api/v1/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente").value("Ana"));
    }

    @Test
    void testGetFacturaInexistenteDevuelve204() throws Exception {
        Mockito.when(facturaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/facturas/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPorPedidoExistente() throws Exception {
        Factura f = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        Mockito.when(facturaService.findByPedido(5L)).thenReturn(Optional.of(f));

        mockMvc.perform(get("/api/v1/facturas/pedido/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(5L));
    }

    @Test
    void testGetPorPedidoInexistenteDevuelve204() throws Exception {
        Mockito.when(facturaService.findByPedido(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/facturas/pedido/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEmitirNuevoDevuelve201() throws Exception {
        Factura creada = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        Mockito.when(facturaService.existePorPedido(5L)).thenReturn(false);
        Mockito.when(facturaService.emitirFactura(5L)).thenReturn(creada);

        mockMvc.perform(post("/api/v1/facturas/emitir/5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFactura").value(1L))
                .andExpect(jsonPath("$.neto").value(10000));
    }

    @Test
    void testEmitirYaFacturadoDevuelve409() throws Exception {
        Mockito.when(facturaService.existePorPedido(5L)).thenReturn(true);

        mockMvc.perform(post("/api/v1/facturas/emitir/5"))
                .andExpect(status().isConflict());
    }

    @Test
    void testEmitirPedidoInexistenteDevuelve404() throws Exception {
        Mockito.when(facturaService.existePorPedido(99L)).thenReturn(false);
        Mockito.when(facturaService.emitirFactura(99L)).thenReturn(null);

        mockMvc.perform(post("/api/v1/facturas/emitir/99"))
                .andExpect(status().isNotFound());
    }
}