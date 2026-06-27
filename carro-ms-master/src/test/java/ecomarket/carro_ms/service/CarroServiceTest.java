package ecomarket.carro_ms.service;

import ecomarket.carro_ms.model.Carro;
import ecomarket.carro_ms.model.CuponDescuentoDTO;
import ecomarket.carro_ms.model.ItemCarro;
import ecomarket.carro_ms.model.ProductoDTO;
import ecomarket.carro_ms.model.UsuarioDTO;
import ecomarket.carro_ms.repository.CarroRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CarroService carroService;

    private ItemCarro crearItem(Long idProducto, Integer cantidad) {
        ItemCarro item = new ItemCarro();
        item.setIdProducto(idProducto);
        item.setCantidad(cantidad);
        return item;
    }

    private UsuarioDTO crearUsuarioDTO(String nombre, String apellido) {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        return usuario;
    }

    private ProductoDTO crearProductoDTO(Long id, String nombre, String precio) {
        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setPrecioUnitario(precio);
        return producto;
    }

    @Test
    void testGuardarCarroSinCupon() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreUsuario());
        assertEquals(1000.0, resultado.getSubtotal());
        assertEquals(1000.0, resultado.getTotal());
        assertEquals("Manzana", resultado.getItems().get(0).getNombreProducto());
        assertEquals(500.0, resultado.getItems().get(0).getPrecioUnitario());
        assertEquals(1000.0, resultado.getItems().get(0).getSubtotal());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testGuardarCarroConCuponValido() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(99L);
        cupon.setCodigo("DESC10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(true);
        cupon.setFechaExpiracion(LocalDate.now().plusDays(5));

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(restTemplate.getForObject(anyString(), eq(CuponDescuentoDTO.class))).thenReturn(cupon);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getSubtotal());
        assertEquals(900.0, resultado.getTotal()); // 1000 - 10%
        assertEquals(99L, resultado.getIdCupon());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testGuardarCarroConCuponInactivo() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(99L);
        cupon.setCodigo("DESC10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(false);
        cupon.setFechaExpiracion(LocalDate.now().plusDays(5));

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(restTemplate.getForObject(anyString(), eq(CuponDescuentoDTO.class))).thenReturn(cupon);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getSubtotal());
        assertEquals(1000.0, resultado.getTotal()); // sin descuento
        assertNull(resultado.getIdCupon());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testGuardarCarroConCuponExpirado() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("DESC10");
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        CuponDescuentoDTO cupon = new CuponDescuentoDTO();
        cupon.setIdCupon(99L);
        cupon.setCodigo("DESC10");
        cupon.setPorcentajeDescuento(10.0);
        cupon.setActivo(true);
        cupon.setFechaExpiracion(LocalDate.now().minusDays(1)); // expirado

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(restTemplate.getForObject(anyString(), eq(CuponDescuentoDTO.class))).thenReturn(cupon);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getTotal()); // sin descuento por expirado
        assertNull(resultado.getIdCupon());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testGuardarCarroUsuarioNull() {
        Carro carro = new Carro();
        carro.setIdUsuario(999L);
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertNull(resultado.getNombreUsuario()); // no se setea porque usuario es null
        assertEquals(1000.0, resultado.getSubtotal());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testListarCarro() {
        Carro c1 = new Carro();
        c1.setIdCarro(1L);
        Carro c2 = new Carro();
        c2.setIdCarro(2L);

        when(carroRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Carro> resultado = carroService.listarCarro();

        assertEquals(2, resultado.size());

        verify(carroRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Carro carro = new Carro();
        carro.setIdCarro(1L);

        when(carroRepository.findById(1L)).thenReturn(Optional.of(carro));

        Optional<Carro> resultado = carroService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCarro());

        verify(carroRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(carroRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Carro> resultado = carroService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(carroRepository, times(1)).findById(99L);
    }

    @Test
    void testEliminarCarro() {
        doNothing().when(carroRepository).deleteById(1L);

        carroService.eliminarCarro(1L);

        verify(carroRepository, times(1)).deleteById(1L);
    }

    @Test
    void testActualizarExistente() {
        Carro existente = new Carro();
        existente.setIdCarro(1L);
        existente.setTipoEntrega("RETIRO");

        Carro datosNuevos = new Carro();
        datosNuevos.setTipoEntrega("DESPACHO");
        datosNuevos.setIdDireccion(5L);

        when(carroRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(carroRepository.save(existente)).thenReturn(existente);

        Carro resultado = carroService.actualizar(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals("DESPACHO", resultado.getTipoEntrega());
        assertEquals(5L, resultado.getIdDireccion());

        verify(carroRepository, times(1)).findById(1L);
        verify(carroRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarNoExistente() {
        Carro datosNuevos = new Carro();
        datosNuevos.setTipoEntrega("DESPACHO");

        when(carroRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carroService.actualizar(99L, datosNuevos));

        assertEquals("Carro no encontrado", exception.getMessage());

        verify(carroRepository, times(1)).findById(99L);
        verify(carroRepository, never()).save(any(Carro.class));
    }

    @Test
    void testGuardarCarroConCuponNull() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon("NOEXISTE");
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(restTemplate.getForObject(anyString(), eq(CuponDescuentoDTO.class))).thenReturn(null);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getSubtotal());
        assertEquals(1000.0, resultado.getTotal()); // sin descuento porque el cupon es null
        assertNull(resultado.getIdCupon());

        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void testGuardarCarroConCuponVacio() {
        Carro carro = new Carro();
        carro.setIdUsuario(1L);
        carro.setCodigoCupon(""); // string vacío -> isEmpty() true
        carro.setItems(new ArrayList<>(Arrays.asList(crearItem(10L, 2))));

        UsuarioDTO usuario = crearUsuarioDTO("Juan", "Perez");
        ProductoDTO producto = crearProductoDTO(10L, "Manzana", "500.0");

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(usuario);
        when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class))).thenReturn(producto);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro resultado = carroService.guardarCarro(carro);

        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getSubtotal());
        assertEquals(1000.0, resultado.getTotal()); // sin descuento, cupon vacío
        assertNull(resultado.getIdCupon());

        verify(carroRepository, times(1)).save(carro);
    }
}
