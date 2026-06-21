# Proveedor MS

Microservicio de gestión de **proveedores** para EcoMarket SPA. Expone una API REST para realizar operaciones CRUD sobre la entidad `Proveedor`, construido con Spring Boot y arquitectura en capas (Controller → Service → Repository).

## Stack

| Componente | Detalle |
|---|---|
| Framework | Spring Boot 4.1.0 |
| Java | 25 |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MySQL (`proveedoresdb`) — H2 disponible como runtime |
| Utilidades | Lombok |
| Puerto | `8082` |
| Base path | `/api/v1/proveedores` |
| Grupo / artefacto | `ecomarket` / `proveedor-ms` |

## Modelo — `Proveedor`

Entidad JPA mapeada a la tabla `proveedores`.

| Atributo | Tipo | Restricciones |
|---|---|---|
| `idProveedor` | `Long` | PK, autogenerado (`IDENTITY`) |
| `nombre` | `String` | `NOT NULL`, máx. 25 caracteres |
| `rut` | `String` | `NOT NULL`, máx. 25 caracteres |

Usa anotaciones de Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`), por lo que los getters, setters y constructores se generan en tiempo de compilación.

## Métodos por capa

### Controller — `ProveedorController`

Recibe las peticiones HTTP y delega en el servicio. Anotado con `@RestController` y `@RequestMapping("api/v1/proveedores")`.

| Método Java | Verbo HTTP | Ruta | Descripción | Delega en |
|---|---|---|---|---|
| `postProveedor(Proveedor proveedor)` | `POST` | `/api/v1/proveedores` | Crea un nuevo proveedor a partir del body. | `proveedorService.guardarProveedor(...)` |
| `getProveedores()` | `GET` | `/api/v1/proveedores` | Devuelve la lista de todos los proveedores. | `proveedorService.listar()` |
| `putProveedor(Long id, Proveedor proveedor)` | `PUT` | `/api/v1/proveedores/{id}` | Actualiza el proveedor indicado por `id`. | `proveedorService.modificar(id, ...)` |
| `deleteProveedor(Long id)` | `DELETE` | `/api/v1/proveedores/{id}` | Elimina el proveedor indicado por `id`. | `proveedorService.eliminar(id)` |

### Service — `ProveedorService`

Contiene la lógica de negocio. Anotado con `@Service` y `@Transactional`.

| Método | Firma | Descripción |
|---|---|---|
| `guardarProveedor` | `Proveedor guardarProveedor(Proveedor proveedor)` | Persiste un proveedor usando `save()` y devuelve la entidad guardada. |
| `listar` | `List<Proveedor> listar()` | Recupera todos los proveedores con `findAll()`. |
| `modificar` | `Proveedor modificar(Long id, Proveedor proveedor)` | Busca el proveedor por `id`; si no existe lanza `RuntimeException("Proveedor no encontrado: id")`. Actualiza `nombre` y `rut`, guarda y devuelve la entidad actualizada. |
| `eliminar` | `void eliminar(Long id)` | Elimina el proveedor por `id` con `deleteById()`. |

### Repository — `ProveedorRepository`

Interfaz que extiende `JpaRepository<Proveedor, Long>`. Hereda las operaciones CRUD estándar de Spring Data JPA (`save`, `findAll`, `findById`, `deleteById`, etc.) sin necesidad de implementación manual.

## Referencia de la API

### Crear proveedor
```http
POST /api/v1/proveedores
Content-Type: application/json

{
  "nombre": "Distribuidora Sur",
  "rut": "76.543.210-9"
}
```
Respuesta: el proveedor creado, incluyendo su `idProveedor`.

### Listar proveedores
```http
GET /api/v1/proveedores
```
Respuesta: arreglo de proveedores.

### Actualizar proveedor
```http
PUT /api/v1/proveedores/1
Content-Type: application/json

{
  "nombre": "Distribuidora Sur Ltda.",
  "rut": "76.543.210-9"
}
```
Respuesta: el proveedor actualizado. Si el `id` no existe, devuelve error por la `RuntimeException` lanzada.

### Eliminar proveedor
```http
DELETE /api/v1/proveedores/1
```
Respuesta: sin contenido.


