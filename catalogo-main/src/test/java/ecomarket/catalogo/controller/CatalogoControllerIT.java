package ecomarket.catalogo.controller;

import tools.jackson.databind.ObjectMapper;
import ecomarket.catalogo.model.Catalogo;
import ecomarket.catalogo.model.Producto;
import ecomarket.catalogo.repository.CatalogoRepository;
import ecomarket.catalogo.repository.ProductoRepository;
import ecomarket.catalogo.repository.ReseniaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ReseniaRepository reseniaRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        // Orden importante por las FK: primero reseñas, luego productos, luego catálogos
        reseniaRepository.deleteAll();
        productoRepository.deleteAll();
        catalogoRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerCatalogo() throws Exception {
        Catalogo catalogo = new Catalogo();
        catalogo.setNombreCatalogo("Catalogo Verano");
        catalogo.setFechaActualizacion(LocalDate.now());

        mockMvc.perform(post("/api/v1/catalogos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catalogo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCatalogo").exists())
                .andExpect(jsonPath("$.nombreCatalogo").value("Catalogo Verano"));

        mockMvc.perform(get("/api/v1/catalogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCatalogo").value("Catalogo Verano"));
    }

    @Test
    void testCrearYBuscarProducto() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Manzana Roja");
        producto.setMarca("FrutCorp");
        producto.setPrecioUnitario(500);
        producto.setEstado(true);

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").exists())
                .andExpect(jsonPath("$.nombre").value("Manzana Roja"));

        mockMvc.perform(get("/api/v1/productos/buscar").param("nombre", "manzana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Manzana Roja"));

        mockMvc.perform(get("/api/v1/productos/marca/FrutCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("FrutCorp"));
    }

    @Test
    void testCrearReseniaConProductoExistente() throws Exception {
        // Primero creamos un producto real en BD
        Producto producto = new Producto();
        producto.setNombre("Manzana Roja");
        producto.setMarca("FrutCorp");
        producto.setPrecioUnitario(500);
        producto.setEstado(true);
        Producto guardado = productoRepository.save(producto);

        // El JSON de la reseña referencia el producto por su id
        String body = "{ \"comentario\": \"Muy buena\", \"calificacion\": 5, \"producto\": { \"idProducto\": "
                + guardado.getIdProducto() + " } }";

        mockMvc.perform(post("/api/v1/resenias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idResenia").exists())
                .andExpect(jsonPath("$.comentario").value("Muy buena"));
    }

    @Test
    void testCrearReseniaProductoInexistente() throws Exception {
        // Reseña apuntando a un producto que no existe -> el service devuelve null -> 404
        String body = "{ \"comentario\": \"Muy buena\", \"calificacion\": 5, \"producto\": { \"idProducto\": 9999 } }";

        mockMvc.perform(post("/api/v1/resenias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarProducto() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Pera");
        producto.setMarca("FrutCorp");
        producto.setPrecioUnitario(600);
        producto.setEstado(true);
        Producto guardado = productoRepository.save(producto);

        mockMvc.perform(delete("/api/v1/productos/" + guardado.getIdProducto()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/productos/" + guardado.getIdProducto()))
                .andExpect(status().isNoContent());
    }
}
