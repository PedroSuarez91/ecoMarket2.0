package ecomarket.sucursal_ms.service;

import ecomarket.sucursal_ms.model.BodegaDTO;
import ecomarket.sucursal_ms.model.Sucursal;
import ecomarket.sucursal_ms.model.SucursalDTO;
import ecomarket.sucursal_ms.repository.SucursalRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SucursalService sucursalService;

    @Test
    void testCrearSucursal() {
        Sucursal sucursal = new Sucursal(null, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);
        Sucursal guardada = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);

        when(sucursalRepository.save(sucursal)).thenReturn(guardada);

        Sucursal resultado = sucursalService.crearSucursal(sucursal);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccionSucursal());
        assertEquals("09:00-18:00", resultado.getHorario());

        verify(sucursalRepository, times(1)).save(sucursal);
    }

    @Test
    void testListarSucursales() {
        Sucursal s1 = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);
        Sucursal s2 = new Sucursal(2L, "Calle Falsa 456", "10:00-19:00", "Con devoluciones", 2L);

        when(sucursalRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<Sucursal> resultado = sucursalService.listarSucursales();

        assertEquals(2, resultado.size());
        assertEquals("Av. Siempre Viva 123", resultado.get(0).getDireccionSucursal());
        assertEquals("Calle Falsa 456", resultado.get(1).getDireccionSucursal());

        verify(sucursalRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));

        Optional<Sucursal> resultado = sucursalService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdSucursal());

        verify(sucursalRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Sucursal> resultado = sucursalService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(sucursalRepository, times(1)).findById(99L);
    }

    @Test
    void testEliminarSucursalExistente() {
        when(sucursalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sucursalRepository).deleteById(1L);

        boolean resultado = sucursalService.eliminarSucursal(1L);

        assertTrue(resultado);

        verify(sucursalRepository, times(1)).existsById(1L);
        verify(sucursalRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarSucursalNoExistente() {
        when(sucursalRepository.existsById(99L)).thenReturn(false);

        boolean resultado = sucursalService.eliminarSucursal(99L);

        assertFalse(resultado);

        verify(sucursalRepository, times(1)).existsById(99L);
        verify(sucursalRepository, never()).deleteById(99L);
    }

    @Test
    void testObtenerSucursalDTOConBodega() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 5L);

        BodegaDTO bodega = new BodegaDTO();
        bodega.setIdBodega(5L);
        bodega.setNombreBodega("Bodega Central");
        bodega.setCapacidadMax(1000);
        bodega.setActiva(true);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(restTemplate.getForObject(anyString(), eq(BodegaDTO.class))).thenReturn(bodega);

        SucursalDTO resultado = sucursalService.obtenerSucursalDTO(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccionSucursal());
        assertEquals("Bodega Central", resultado.getNombreBodega());
        assertEquals(1000, resultado.getCapacidadMax());

        verify(sucursalRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BodegaDTO.class));
    }

    @Test
    void testObtenerSucursalDTOBodegaNoDisponible() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 5L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(restTemplate.getForObject(anyString(), eq(BodegaDTO.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        SucursalDTO resultado = sucursalService.obtenerSucursalDTO(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertNull(resultado.getNombreBodega());
        assertNull(resultado.getCapacidadMax());

        verify(sucursalRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerSucursalDTONoExistente() {
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        SucursalDTO resultado = sucursalService.obtenerSucursalDTO(99L);

        assertNull(resultado);

        verify(sucursalRepository, times(1)).findById(99L);
        verify(restTemplate, never()).getForObject(anyString(), eq(BodegaDTO.class));
    }

    @Test
    void testConsultarStockExitoso() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 5L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("Stock: 250 unidades");

        String resultado = sucursalService.consultarStock(1L);

        assertEquals("Stock: 250 unidades", resultado);

        verify(sucursalRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testConsultarStockInventarioNoDisponible() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 5L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        String resultado = sucursalService.consultarStock(1L);

        assertEquals("Stock no disponible", resultado);

        verify(sucursalRepository, times(1)).findById(1L);
    }

    @Test
    void testConsultarStockSucursalNoExistente() {
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        String resultado = sucursalService.consultarStock(99L);

        assertNull(resultado);

        verify(sucursalRepository, times(1)).findById(99L);
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
    }

    @Test
    void testActualizarSucursalExistente() {
        Sucursal existente = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 1L);
        Sucursal datosNuevos = new Sucursal(null, "Calle Nueva 789", "08:00-17:00", "Sin devoluciones", 2L);
        Sucursal actualizada = new Sucursal(1L, "Calle Nueva 789", "08:00-17:00", "Sin devoluciones", 2L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(existente)).thenReturn(actualizada);

        Sucursal resultado = sucursalService.actualizarSucursal(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals("Calle Nueva 789", resultado.getDireccionSucursal());
        assertEquals("08:00-17:00", resultado.getHorario());
        assertEquals(2L, resultado.getIdBodega());

        verify(sucursalRepository, times(1)).findById(1L);
        verify(sucursalRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarSucursalNoExistente() {
        Sucursal datosNuevos = new Sucursal(null, "Calle Nueva 789", "08:00-17:00", "Sin devoluciones", 2L);

        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        Sucursal resultado = sucursalService.actualizarSucursal(99L, datosNuevos);

        assertNull(resultado);

        verify(sucursalRepository, times(1)).findById(99L);
        verify(sucursalRepository, never()).save(any(Sucursal.class));
    }

    @Test
    void testObtenerSucursalDTOBodegaNull() {
        Sucursal sucursal = new Sucursal(1L, "Av. Siempre Viva 123", "09:00-18:00", "Sin devoluciones", 5L);

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(restTemplate.getForObject(anyString(), eq(BodegaDTO.class))).thenReturn(null);

        SucursalDTO resultado = sucursalService.obtenerSucursalDTO(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSucursal());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccionSucursal());
        assertNull(resultado.getNombreBodega());
        assertNull(resultado.getCapacidadMax());

        verify(sucursalRepository, times(1)).findById(1L);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(BodegaDTO.class));
    }
}
