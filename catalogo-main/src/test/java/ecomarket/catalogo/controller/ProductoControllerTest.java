package ecomarket.catalogo.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.service.ProductoService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

@WebMvcTest(ProductoController.class)
@ActiveProfiles("test")
public class ProductoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockitoBean
        private ProductoService productoService;

        private ObjectMapper objectMapper = new ObjectMapper();

        private Producto crearProducto(Long id, String nombre, String marca, Integer precio) {
                Producto producto = new Producto();
                producto.setIdProducto(id);
                producto.setNombre(nombre);
                producto.setMarca(marca);
                producto.setPrecioUnitario(precio);
                producto.setEstado(true);
                return producto;
        }

        @Test
        void testGetProductos() throws Exception {
                Mockito.when(productoService.listarProductos())
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500),
                                                crearProducto(2L, "Pera", "FrutCorp", 600)));

                mockMvc.perform(get("/api/v1/productos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nombre").value("Manzana"));
        }

        @Test
        void testGetProductosVacio() throws Exception {
                Mockito.when(productoService.listarProductos()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetProductoExistente() throws Exception {
                Mockito.when(productoService.findByIdProducto(1L))
                                .thenReturn(Optional.of(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Manzana"));
        }

        @Test
        void testGetProductoNoExistente() throws Exception {
                Mockito.when(productoService.findByIdProducto(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/v1/productos/99"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testPostProducto() throws Exception {
                Producto nuevo = crearProducto(null, "Manzana", "FrutCorp", 500);
                Producto guardado = crearProducto(1L, "Manzana", "FrutCorp", 500);

                Mockito.when(productoService.registrarProducto(any(Producto.class))).thenReturn(guardado);

                mockMvc.perform(post("/api/v1/productos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.idProducto").value(1L))
                                .andExpect(jsonPath("$.nombre").value("Manzana"));
        }

        @Test
        void testPostProductoConflicto() throws Exception {
                Producto nuevo = crearProducto(null, "Manzana", "FrutCorp", 500);

                Mockito.when(productoService.registrarProducto(any(Producto.class)))
                                .thenThrow(new RuntimeException("Error al registrar"));

                mockMvc.perform(post("/api/v1/productos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isConflict());
        }

        @Test
        void testPutProductoExistente() throws Exception {
                Producto actualizado = crearProducto(1L, "Manzana Verde", "FrutCorp", 700);

                Mockito.when(productoService.actualizarProducto(eq(1L), any(Producto.class))).thenReturn(actualizado);

                mockMvc.perform(put("/api/v1/productos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actualizado)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Manzana Verde"));
        }

        @Test
        void testPutProductoNoExistente() throws Exception {
                Producto datos = crearProducto(null, "Manzana Verde", "FrutCorp", 700);

                Mockito.when(productoService.actualizarProducto(eq(99L), any(Producto.class))).thenReturn(null);

                mockMvc.perform(put("/api/v1/productos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(datos)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testGetPorCategoria() throws Exception {
                Mockito.when(productoService.findByCategoria(5L))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/categoria/5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorCategoriaVacio() throws Exception {
                Mockito.when(productoService.findByCategoria(5L)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/categoria/5"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testBuscarPorNombre() throws Exception {
                Mockito.when(productoService.buscarPorNombre("manz"))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/buscar").param("nombre", "manz"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testBuscarPorNombreVacio() throws Exception {
                Mockito.when(productoService.buscarPorNombre("xyz")).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/buscar").param("nombre", "xyz"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetPorMarca() throws Exception {
                Mockito.when(productoService.findByMarca("FrutCorp"))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/marca/FrutCorp"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorMarcaVacio() throws Exception {
                Mockito.when(productoService.findByMarca("NoExiste")).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/marca/NoExiste"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetPorRangoPrecio() throws Exception {
                Mockito.when(productoService.findByRangoPrecio(400, 600))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/precio/rango").param("min", "400").param("max", "600"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorRangoPrecioVacio() throws Exception {
                Mockito.when(productoService.findByRangoPrecio(1000, 2000)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/precio/rango").param("min", "1000").param("max", "2000"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetPorPrecioMaximo() throws Exception {
                Mockito.when(productoService.findByPrecioMaximo(600))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/precio/maximo").param("max", "600"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorPrecioMaximoVacio() throws Exception {
                Mockito.when(productoService.findByPrecioMaximo(100)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/precio/maximo").param("max", "100"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testGetPorPrecioMinimo() throws Exception {
                Mockito.when(productoService.findByPrecioMinimo(400))
                                .thenReturn(Arrays.asList(crearProducto(1L, "Manzana", "FrutCorp", 500)));

                mockMvc.perform(get("/api/v1/productos/precio/minimo").param("min", "400"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        void testGetPorPrecioMinimoVacio() throws Exception {
                Mockito.when(productoService.findByPrecioMinimo(9999)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/v1/productos/precio/minimo").param("min", "9999"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testEliminarProductoExistente() throws Exception {
                Mockito.when(productoService.eliminarProducto(1L)).thenReturn(true);

                mockMvc.perform(delete("/api/v1/productos/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void testEliminarProductoNoExistente() throws Exception {
                Mockito.when(productoService.eliminarProducto(99L)).thenReturn(false);

                mockMvc.perform(delete("/api/v1/productos/99"))
                                .andExpect(status().isNotFound());
        }
}
