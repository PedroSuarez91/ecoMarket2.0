# Carro MS — Microservicio de Carro de Compras

Microservicio de EcoMarket SPA encargado de gestionar el carro de compras: creación, consulta, actualización y eliminación. Al crear un carro consume otros microservicios (Usuarios, Productos, Cupones) vía `RestTemplate` para enriquecer la información y calcular subtotales/totales.

## Stack

| | |
|---|---|
| Framework | Spring Boot |
| Java | 25 |
| Persistencia | Spring Data JPA |
| BD | MySQL (`dbcarro`) / H2 (consola) |
| Utilidades | Lombok |
| Puerto | `8083` |
| Base path | `/api/v1/carros` |
| Paquete raíz | `ecomarket.carro_ms` |

## Configuración (`application.properties`)

```properties
spring.application.name=carro-ms
server.port=8083
spring.datasource.url=jdbc:mysql://localhost:3306/dbcarro
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Endpoints (CarroController)

Todos cuelgan de `/api/v1/carros`.

| Método HTTP | Ruta | Descripción | Body | Respuestas |
|---|---|---|---|---|
| `GET` | `/api/v1/carros` | Lista todos los carros | — | `200 OK` con la lista · `204 NO_CONTENT` si está vacía |
| `POST` | `/api/v1/carros` | Crea un carro (calcula subtotal/total y consume servicios externos) | `Carro` | `201 CREATED` · `409 CONFLICT` ante error |
| `GET` | `/api/v1/carros/{id}` | Obtiene un carro por su ID | — | `200 OK` · `204 NO_CONTENT` si no existe |
| `PUT` | `/api/v1/carros/{id}` | Actualiza `tipoEntrega` e `idDireccion` | `Carro` | `200 OK` · `404 NOT_FOUND` |
| `DELETE` | `/api/v1/carros/{id}` | Elimina un carro por su ID | — | `204 NO_CONTENT` · `404 NOT_FOUND` |

### Detalle de métodos del controller

- **`getCarros()`** → `ResponseEntity<List<Carro>>`
  Devuelve todos los carros. Si la lista viene vacía responde `204`.

- **`postCarro(Carro carro)`** → `ResponseEntity<Carro>`
  Recibe el carro en el body, delega en el service y responde `201` con el carro creado. Cualquier excepción se traduce a `409`.

- **`getCarro(Long id)`** → `ResponseEntity<Carro>`
  Busca por ID. Si el `Optional` está vacío responde `204`.

- **`actualizarCarro(Long id, Carro carro)`** → `ResponseEntity<Carro>`
  Actualiza solo entrega y dirección. Si el ID no existe responde `404`.

- **`eliminarCarro(Long id)`** → `ResponseEntity<Void>`
  Borra el carro. Si falla responde `404`.

---

## Métodos del service (CarroService)

| Método | Firma | Qué hace |
|---|---|---|
| Guardar | `Carro guardarCarro(Carro carro)` | Lógica de negocio central (ver abajo) |
| Listar | `List<Carro> listarCarro()` | `findAll()` del repositorio |
| Buscar | `Optional<Carro> findById(Long id)` | `findById()` del repositorio |
| Eliminar | `void eliminarCarro(Long id)` | `deleteById()` del repositorio |
| Actualizar | `Carro actualizar(Long id, Carro carro)` | Carga el existente o lanza `RuntimeException`; actualiza `tipoEntrega` e `idDireccion` y guarda |

### Flujo de `guardarCarro(...)`

1. Setea `fechaCreacion = LocalDate.now()`.
2. Llama a **Usuarios** (`http://localhost:8081/api/v1/usuarios/{idUsuario}`) y arma `nombreUsuario = nombre + apellido`.
3. Por cada `ItemCarro` llama a **Productos** (`http://localhost:8xxx/api/v1/productos/{idProducto}`), setea nombre y precio unitario, y calcula `subtotal = precioUnitario × cantidad`. Acumula el `subtotalGeneral`.
4. Si hay `codigoCupon`, llama a **Cupones** (`http://localhost:8086/api/v1/cupones/{codigoCupon}`). Si el cupón está activo y no expiró, aplica el descuento sobre el total; si no, `total = subtotal`.
5. Persiste con `carroRepository.save(carro)`.



## Servicios externos consumidos

| Servicio | URL | DTO | Uso |
|---|---|---|---|
| Usuarios | `localhost:8081/api/v1/usuarios/{id}` | `UsuarioDTO` | Nombre del usuario |
| Productos | `localhost:8xxx/api/v1/productos/{id}` | `ProductoDTO` | Nombre y precio por ítem |
| Cupones | `localhost:8086/api/v1/cupones/{codigo}` | `CuponDescuentoDTO` | Descuento aplicable |

El `RestTemplate` se registra como bean en `RestTemplateConfig`.

---

## Modelo de datos

### `Carro` (entidad)
`idCarro` (PK, IDENTITY) · `idUsuario` · `nombreUsuario` · `fechaCreacion` · `subtotal` · `total` · `idCupon` (write-only en JSON) · `codigoCupon` · `tipoEntrega` · `idDireccion` · `items` (`@OneToMany`, cascade ALL, orphanRemoval).

### `ItemCarro` (entidad)
`idItemCarro` (PK, IDENTITY) · `idProducto` · `nombreProducto` · `cantidad` · `precioUnitario` · `subtotal` · `carro` (`@ManyToOne`, `@JsonBackReference`).

### DTOs
- **`UsuarioDTO`**: `nombre`, `apellido`.
- **`ProductoDTO`**: `idProducto`, `idInventario`, `tipoProducto`, `nombre`, `descripcion`, `marca`, `precioUnitario`, `estado`.
- **`CuponDescuentoDTO`**: `idCupon`, `codigo`, `porcentajeDescuento`, `activo`, `fechaExpiracion`.

---

## Ejemplo de creación (`POST /api/v1/carros`)

```json
{
  "idUsuario": 1,
  "codigoCupon": "DESC10",
  "tipoEntrega": "DESPACHO",
  "idDireccion": 5,
  "items": [
    { "idProducto": 10, "cantidad": 2 },
    { "idProducto": 15, "cantidad": 1 }
  ]
}
```
