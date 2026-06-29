package ecomarket.pedido.controller;

import ecomarket.pedido.model.EstadoPedido;
import ecomarket.pedido.model.Pedido;
import ecomarket.pedido.service.PedidoService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@ActiveProfiles("test")
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private Pedido pedidoEjemplo() {
        Pedido p = new Pedido();
        p.setIdPedido(1L);
        p.setIdUsuario(5L);
        p.setNombreCliente("Camila");
        p.setTotal(1350.0);
        p.setEstadoPedido(EstadoPedido.PENDIENTE);
        return p;
    }

    @Test
    void testGetPedidosConContenido() throws Exception {
        Mockito.when(pedidoService.listarPedidos()).thenReturn(Arrays.asList(pedidoEjemplo()));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCliente").value("Camila"));
    }

    @Test
    void testGetPedidosVacio204() throws Exception {
        Mockito.when(pedidoService.listarPedidos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPedidoExistente() throws Exception {
        Mockito.when(pedidoService.findById(1L)).thenReturn(Optional.of(pedidoEjemplo()));

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1L));
    }

    @Test
    void testGetPedidoInexistente204() throws Exception {
        Mockito.when(pedidoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pedidos/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPorUsuarioConContenido() throws Exception {
        Mockito.when(pedidoService.listarPorUsuario(5L)).thenReturn(Arrays.asList(pedidoEjemplo()));

        mockMvc.perform(get("/api/v1/pedidos/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetPorUsuarioVacio204() throws Exception {
        Mockito.when(pedidoService.listarPorUsuario(5L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/pedidos/usuario/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCrearDesdeCarro201() throws Exception {
        Mockito.when(pedidoService.crearPedidoDesdeCarro(eq(7L), eq(2L))).thenReturn(pedidoEjemplo());

        mockMvc.perform(post("/api/v1/pedidos/carro/7").param("idDireccion", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPedido").value(1L));
    }

    @Test
    void testCrearDesdeCarroNoExiste404() throws Exception {
        Mockito.when(pedidoService.crearPedidoDesdeCarro(eq(99L), any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/pedidos/carro/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAsignarDireccion200() throws Exception {
        Mockito.when(pedidoService.actualizarDireccion(1L, 2L)).thenReturn(pedidoEjemplo());

        mockMvc.perform(put("/api/v1/pedidos/1/direccion/2"))
                .andExpect(status().isOk());
    }

    @Test
    void testAsignarDireccion404() throws Exception {
        Mockito.when(pedidoService.actualizarDireccion(1L, 99L)).thenReturn(null);

        mockMvc.perform(put("/api/v1/pedidos/1/direccion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarEstado200() throws Exception {
        Mockito.when(pedidoService.actualizarEstado(eq(1L), eq(EstadoPedido.ENVIADO)))
                .thenReturn(pedidoEjemplo());

        mockMvc.perform(put("/api/v1/pedidos/1/estado").param("estado", "ENVIADO"))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarEstado404() throws Exception {
        Mockito.when(pedidoService.actualizarEstado(eq(99L), any())).thenReturn(null);

        mockMvc.perform(put("/api/v1/pedidos/99/estado").param("estado", "ENVIADO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminar204() throws Exception {
        Mockito.doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void testEliminarPedidoFallaDevuelve404() throws Exception {
        Mockito.doThrow(new RuntimeException("no existe"))
                .when(pedidoService).eliminarPedido(99L);
        mockMvc.perform(delete("/api/v1/pedidos/99"))
                .andExpect(status().isNotFound());
}
}