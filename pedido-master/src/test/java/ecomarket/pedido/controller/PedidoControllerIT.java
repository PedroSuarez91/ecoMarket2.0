package ecomarket.pedido.controller;


import ecomarket.pedido.model.CarroDTO;
import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.model.ItemCarroDTO;
import ecomarket.pedido.repository.DireccionRepository;
import ecomarket.pedido.repository.PedidoRepository;
import tools.jackson.databind.ObjectMapper;

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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestTemplate restTemplate;

    @BeforeEach
    void limpiar() {
        pedidoRepository.deleteAll();
        direccionRepository.deleteAll();
    }

    private CarroDTO carroConUnItem() {
        ItemCarroDTO item = new ItemCarroDTO();
        item.setIdProducto(1L);
        item.setNombreProducto("Manzanas");
        item.setCantidad(3);
        item.setPrecioUnitario(500.0);
        item.setSubtotal(1500.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdUsuario(5L);
        carro.setNombreUsuario("Camila");
        carro.setTipoEntrega("DESPACHO");
        carro.setSubtotal(1500.0);
        carro.setTotal(1350.0);
        List<ItemCarroDTO> items = new ArrayList<>();
        items.add(item);
        carro.setItems(items);
        return carro;
    }

    @Test
    void testCrearPedidoDesdeCarroFlujoReal() throws Exception {
        // direccion existente en H2
        Direccion dir = direccionRepository.save(
                new Direccion(null, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000));

        // el carro se simula
        Mockito.when(restTemplate.getForObject(anyString(), eq(CarroDTO.class)))
                .thenReturn(carroConUnItem());

        mockMvc.perform(post("/api/v1/pedidos/carro/7")
                        .param("idDireccion", String.valueOf(dir.getIdDireccion())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCliente").value("Camila"))
                .andExpect(jsonPath("$.total").value(1350.0))
                .andExpect(jsonPath("$.estadoPedido").value("PENDIENTE"));
    }

    @Test
    void testCrearPedidoCarroNuloDevuelve404() throws Exception {
        Mockito.when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/pedidos/carro/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearYListarDireccion() throws Exception {
        Direccion nueva = new Direccion(null, "Av Siempre Viva", "742", "RM", "Santiago", "Maipu", 9250000);

        mockMvc.perform(post("/api/v1/direcciones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDireccion").exists());

        mockMvc.perform(get("/api/v1/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comuna").value("Maipu"));
    }
}