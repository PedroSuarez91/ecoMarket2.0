package ecomarket.cupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

import ecomarket.cupon.model.Cupon;
import ecomarket.cupon.repository.CuponRepository;

@ExtendWith(MockitoExtension.class)
public class CuponServiceTest {

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    @Test
    void testGuardarCupon() {
        Cupon nuevo = new Cupon(null, "VERANO10", 10.0, true, LocalDate.of(2026, 12, 31));
        Cupon guardado = new Cupon(1L, "VERANO10", 10.0, true, LocalDate.of(2026, 12, 31));

        when(cuponRepository.save(nuevo)).thenReturn(guardado);

        Cupon resultado = cuponService.guardar(nuevo);

        assertThat(resultado.getIdCupon()).isEqualTo(1L);
        assertThat(resultado.getCodigo()).isEqualTo("VERANO10");
        verify(cuponRepository).save(nuevo);
    }

    @Test
    void testGuardarCuponSinEstadoNaceActivo() {
        Cupon sinEstado = new Cupon(null, "NUEVO", 15.0, null, null);

        when(cuponRepository.save(any(Cupon.class))).thenAnswer(inv -> inv.getArgument(0));

        Cupon resultado = cuponService.guardar(sinEstado);

        assertThat(resultado.getActivo()).isTrue();
        verify(cuponRepository).save(sinEstado);
    }

    @Test
    void testListarCupones() {
        List<Cupon> cupones = new ArrayList<>();
        cupones.add(new Cupon(1L, "VERANO10", 10.0, true, null));

        when(cuponRepository.findAll()).thenReturn(cupones);

        List<Cupon> resultado = cuponService.listar();

        assertThat(resultado).hasSize(1);
        verify(cuponRepository).findAll();
    }

    @Test
    void testFindById() {
        Cupon cupon = new Cupon(1L, "VERANO10", 10.0, true, null);
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        Optional<Cupon> resultado = cuponService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigo()).isEqualTo("VERANO10");
        verify(cuponRepository).findById(1L);
    }

    @Test
    void testBuscarPorCodigo() {
        Cupon cupon = new Cupon(1L, "VERANO10", 10.0, true, null);
        when(cuponRepository.findByCodigo("VERANO10")).thenReturn(Optional.of(cupon));

        Optional<Cupon> resultado = cuponService.buscarPorCodigo("VERANO10");

        assertThat(resultado).isPresent();
        verify(cuponRepository).findByCodigo("VERANO10");
    }

    @Test
    void testActualizarCupon() {
        Cupon existente = new Cupon(1L, "VERANO10", 10.0, true, null);
        Cupon datos = new Cupon(null, "INVIERNO20", 20.0, false, LocalDate.of(2026, 8, 1));

        when(cuponRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(inv -> inv.getArgument(0));

        Cupon resultado = cuponService.actualizar(1L, datos);

        assertThat(resultado.getCodigo()).isEqualTo("INVIERNO20");
        assertThat(resultado.getPorcentajeDescuento()).isEqualTo(20.0);
        assertThat(resultado.getActivo()).isFalse();
        verify(cuponRepository).findById(1L);
        verify(cuponRepository).save(existente);
    }

    @Test
    void testActualizarCuponNoExistente() {
        Cupon datos = new Cupon(null, "X", 5.0, true, null);
        when(cuponRepository.findById(99L)).thenReturn(Optional.empty());

        Cupon resultado = cuponService.actualizar(99L, datos);

        assertThat(resultado).isNull();
        verify(cuponRepository).findById(99L);
    }

    @Test
    void testEliminarCupon() {
        doNothing().when(cuponRepository).deleteById(1L);

        cuponService.eliminar(1L);

        verify(cuponRepository).deleteById(1L);
    }

    @Test
    void testValidarCuponValido() {
        Cupon cupon = new Cupon(1L, "OK", 10.0, true, null);
        when(cuponRepository.findByCodigo("OK")).thenReturn(Optional.of(cupon));

        assertThat(cuponService.validar("OK")).isTrue();
    }

    @Test
    void testValidarCuponVencido() {
        Cupon cupon = new Cupon(1L, "VIEJO", 10.0, true, LocalDate.now().minusDays(1));
        when(cuponRepository.findByCodigo("VIEJO")).thenReturn(Optional.of(cupon));

        assertThat(cuponService.validar("VIEJO")).isFalse();
    }

    @Test
    void testValidarCuponInactivo() {
        Cupon cupon = new Cupon(1L, "OFF", 10.0, false, null);
        when(cuponRepository.findByCodigo("OFF")).thenReturn(Optional.of(cupon));

        assertThat(cuponService.validar("OFF")).isFalse();
    }

    @Test
    void testValidarCuponInexistente() {
        when(cuponRepository.findByCodigo("NADA")).thenReturn(Optional.empty());

        assertThat(cuponService.validar("NADA")).isFalse();
    }

}