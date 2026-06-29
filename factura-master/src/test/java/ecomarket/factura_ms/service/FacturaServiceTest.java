package ecomarket.factura_ms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import ecomarket.factura_ms.model.Factura;
import ecomarket.factura_ms.model.PedidoDTO;
import ecomarket.factura_ms.repository.FacturaRepository;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FacturaService facturaService;

    @Test
    void testEmitirFacturaCalculaNetoEIva() {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(1L);
        pedido.setNombreCliente("Juan Perez");
        pedido.setTotal(11900.0);

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedido);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        Factura resultado = facturaService.emitirFactura(1L);

        assertThat(resultado.getTotal()).isEqualTo(11900);
        assertThat(resultado.getNeto()).isEqualTo(10000);
        assertThat(resultado.getIva()).isEqualTo(1900);
        assertThat(resultado.getNombreCliente()).isEqualTo("Juan Perez");
        assertThat(resultado.getIdPedido()).isEqualTo(1L);
        verify(facturaRepository).save(any(Factura.class));
    }

    @Test
    void testEmitirFacturaPedidoInexistenteDevuelveNull() {
        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(null);

        Factura resultado = facturaService.emitirFactura(99L);

        assertThat(resultado).isNull();
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void testEmitirFacturaTotalNullDevuelveNull() {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(1L);
        pedido.setNombreCliente("Sin total");
        pedido.setTotal(null);

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedido);

        Factura resultado = facturaService.emitirFactura(1L);

        assertThat(resultado).isNull();
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void testExistePorPedidoTrue() {
        Factura f = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        when(facturaRepository.findByIdPedido(5L)).thenReturn(Optional.of(f));

        assertThat(facturaService.existePorPedido(5L)).isTrue();
        verify(facturaRepository).findByIdPedido(5L);
    }

    @Test
    void testExistePorPedidoFalse() {
        when(facturaRepository.findByIdPedido(5L)).thenReturn(Optional.empty());

        assertThat(facturaService.existePorPedido(5L)).isFalse();
    }

    @Test
    void testListar() {
        List<Factura> facturas = new ArrayList<>();
        facturas.add(new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900));
        when(facturaRepository.findAll()).thenReturn(facturas);

        assertThat(facturaService.listar()).hasSize(1);
        verify(facturaRepository).findAll();
    }

    @Test
    void testFindById() {
        Factura f = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(f));

        assertThat(facturaService.findById(1L)).isPresent();
        verify(facturaRepository).findById(1L);
    }

    @Test
    void testFindByPedido() {
        Factura f = new Factura(1L, 5L, "Ana", LocalDate.now(), 10000, 1900, 11900);
        when(facturaRepository.findByIdPedido(5L)).thenReturn(Optional.of(f));

        assertThat(facturaService.findByPedido(5L)).isPresent();
        verify(facturaRepository).findByIdPedido(5L);
    }
}