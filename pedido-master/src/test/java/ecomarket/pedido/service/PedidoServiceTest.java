package ecomarket.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import ecomarket.pedido.model.CarroDTO;
import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.model.EstadoPedido;
import ecomarket.pedido.model.ItemCarroDTO;
import ecomarket.pedido.model.Pedido;
import ecomarket.pedido.repository.DireccionRepository;
import ecomarket.pedido.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    private CarroDTO carroConUnItem() {
        ItemCarroDTO item = new ItemCarroDTO();
        item.setIdProducto(1L);
        item.setNombreProducto("Manzanas");
        item.setCantidad(3);
        item.setPrecioUnitario(500.0);
        item.setSubtotal(1500.0);

        CarroDTO carro = new CarroDTO();
        carro.setIdCarro(7L);
        carro.setIdUsuario(1L);
        carro.setNombreUsuario("Camila");
        carro.setTipoEntrega("DESPACHO");
        carro.setCodigoCupon("VERANO10");
        carro.setSubtotal(1500.0);
        carro.setTotal(1350.0);
        List<ItemCarroDTO> items = new ArrayList<>();
        items.add(item);
        carro.setItems(items);
        return carro;
    }

    @Test
    void testCrearPedidoDesdeCarro() {
        CarroDTO carro = carroConUnItem();
        Direccion dir = new Direccion(2L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(direccionRepository.findById(2L)).thenReturn(Optional.of(dir));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crearPedidoDesdeCarro(7L, 2L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreCliente()).isEqualTo("Camila");
        assertThat(resultado.getTotal()).isEqualTo(1350.0);
        assertThat(resultado.getEstadoPedido()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getDireccionEnvio()).isEqualTo(dir);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoCarroNuloDevuelveNull() {
        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(null);

        Pedido resultado = pedidoService.crearPedidoDesdeCarro(99L, 1L);

        assertThat(resultado).isNull();
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoCarroVacioDevuelveNull() {
        CarroDTO carro = new CarroDTO();
        carro.setItems(new ArrayList<>());

        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);

        Pedido resultado = pedidoService.crearPedidoDesdeCarro(7L, 1L);

        assertThat(resultado).isNull();
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoSinDireccion() {
        CarroDTO carro = carroConUnItem();
        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.crearPedidoDesdeCarro(7L, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDireccionEnvio()).isNull();
        verify(direccionRepository, never()).findById(any());
    }

    @Test
    void testActualizarDireccionOk() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        Direccion dir = new Direccion(2L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(direccionRepository.findById(2L)).thenReturn(Optional.of(dir));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.actualizarDireccion(1L, 2L);

        assertThat(resultado.getDireccionEnvio()).isEqualTo(dir);
    }

    @Test
    void testActualizarDireccionPedidoInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(pedidoService.actualizarDireccion(99L, 2L)).isNull();
        verify(direccionRepository, never()).findById(any());
    }

    @Test
    void testActualizarDireccionDireccionInexistente() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(pedidoService.actualizarDireccion(1L, 99L)).isNull();
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void testActualizarEstadoOk() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.actualizarEstado(1L, EstadoPedido.ENVIADO);

        assertThat(resultado.getEstadoPedido()).isEqualTo(EstadoPedido.ENVIADO);
    }

    @Test
    void testActualizarEstadoInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(pedidoService.actualizarEstado(99L, EstadoPedido.ENVIADO)).isNull();
    }

    @Test
    void testListarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido());
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        assertThat(pedidoService.listarPedidos()).hasSize(1);
    }

    @Test
    void testListarPorUsuario() {
        when(pedidoRepository.findByIdUsuario(1L)).thenReturn(new ArrayList<>());

        assertThat(pedidoService.listarPorUsuario(1L)).isEmpty();
        verify(pedidoRepository).findByIdUsuario(1L);
    }

    @Test
    void testFindById() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThat(pedidoService.findById(1L)).isPresent();
    }

    @Test
    void testEliminarPedido() {
        pedidoService.eliminarPedido(1L);
        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void testCrearPedidoCuandoDescontarStockFalla() {
        CarroDTO carro = carroConUnItem();
        when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);
        org.mockito.Mockito.doThrow(new RuntimeException("inventario caido"))
            .when(restTemplate).put(anyString(), any());
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        Pedido resultado = pedidoService.crearPedidoDesdeCarro(7L, null);
            assertThat(resultado).isNotNull();
            assertThat(resultado.getItems()).hasSize(1);
    }

    @Test
void testCrearPedidoCarroConItemsNullDevuelveNull() {
    CarroDTO carro = new CarroDTO();
    carro.setItems(null); 

    when(restTemplate.getForObject(anyString(), eq(CarroDTO.class))).thenReturn(carro);

    Pedido resultado = pedidoService.crearPedidoDesdeCarro(7L, 1L);

    assertThat(resultado).isNull();
    verify(pedidoRepository, never()).save(any(Pedido.class));
}
}