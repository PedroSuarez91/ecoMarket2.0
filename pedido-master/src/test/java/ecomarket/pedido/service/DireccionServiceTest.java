package ecomarket.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import ecomarket.pedido.model.Direccion;
import ecomarket.pedido.repository.DireccionRepository;

@ExtendWith(MockitoExtension.class)
public class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private DireccionService direccionService;

    @Test
    void testGuardarDireccion() {
        Direccion dir = new Direccion(null, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
        Direccion guardada = new Direccion(1L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
        when(direccionRepository.save(dir)).thenReturn(guardada);

        Direccion resultado = direccionService.guardarDireccion(dir);

        assertThat(resultado.getIdDireccion()).isEqualTo(1L);
        verify(direccionRepository).save(dir);
    }

    @Test
    void testListarDirecciones() {
        List<Direccion> lista = new ArrayList<>();
        lista.add(new Direccion(1L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000));
        when(direccionRepository.findAll()).thenReturn(lista);

        assertThat(direccionService.listarDirecciones()).hasSize(1);
    }

    @Test
    void testFindById() {
        Direccion dir = new Direccion(1L, "Calle 1", "123", "RM", "Santiago", "Providencia", 8320000);
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(dir));

        assertThat(direccionService.findById(1L)).isPresent();
    }

    @Test
    void testActualizarOk() {
        Direccion existente = new Direccion(1L, "Vieja", "1", "RM", "Stgo", "Centro", 1000);
        Direccion datos = new Direccion(null, "Nueva", "999", "V", "Valpo", "Cerro", 2340000);

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(inv -> inv.getArgument(0));

        Direccion resultado = direccionService.actualizar(1L, datos);

        assertThat(resultado.getCalle()).isEqualTo("Nueva");
        assertThat(resultado.getCiudad()).isEqualTo("Valpo");
        assertThat(resultado.getCodigoPostal()).isEqualTo(2340000);
    }

    @Test
    void testActualizarInexistente() {
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(direccionService.actualizar(99L, new Direccion())).isNull();
    }

    @Test
    void testEliminar() {
        direccionService.eliminar(1L);
        verify(direccionRepository).deleteById(1L);
    }
}