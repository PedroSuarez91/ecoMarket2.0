# sucursal-ms

Microservicio de **Sucursales** del sistema EcoMarket SPA. Gestiona las sucursales físicas de la empresa (dirección, horario, políticas) y se comunica con los microservicios de **Bodega** e **Inventario** para entregar información consolidada de cada sucursal.

## Tecnologías

- Java 25
- Spring Boot 4.1.0
- Spring Data JPA
- MySQL (producción) / H2 (consola habilitada)
- Lombok
- RestTemplate (comunicación entre microservicios)
- Maven

## Configuración

| Propiedad        | Valor                                   |
|------------------|-----------------------------------------|
| Puerto           | `8084`                                  |
| Base de datos    | `jdbc:mysql://localhost:3306/sucursaldb`|
| Usuario          | `root`                                  |
| DDL              | `update`                                |
| Base path API    | `/api/v1/sucursales`                    |

## Modelo

### Entidad `Sucursal`

| Campo               | Tipo   | Restricción                  |
|---------------------|--------|------------------------------|
| `idSucursal`        | Long   | PK, autogenerado (IDENTITY)  |
| `direccionSucursal` | String | NOT NULL, máx. 25            |
| `horario`           | String | NOT NULL, máx. 25            |
| `politicas`         | String | NOT NULL, máx. 50            |
| `idBodega`          | Long   | Nullable (referencia bodega) |

### DTOs

**`SucursalDTO`** — vista consolidada de la sucursal con datos de su bodega:
`idSucursal`, `direccionSucursal`, `horario`, `nombreBodega`, `capacidadMax`.

**`BodegaDTO`** — datos que se reciben del microservicio de bodega:
`idBodega`, `nombreBodega`, `capacidadMax`, `activa`.

## Endpoints

Base: `http://localhost:8084/api/v1/sucursales`

### POST `/`
Crea una nueva sucursal.

- **Body:** objeto `Sucursal`
- **200 OK:** sucursal creada
- **409 CONFLICT:** `"Error al crear la nueva sucursal"`

```json
{
  "direccionSucursal": "Av. Alemania 0987",
  "horario": "09:00 - 19:00",
  "politicas": "Sin devoluciones tras 30 dias",
  "idBodega": 1
}
```

### GET `/`
Lista todas las sucursales registradas.

- **200 OK:** lista de sucursales
- **404 NOT FOUND:** `"No hay sucursales registradas"`

### GET `/{id}`
Obtiene una sucursal por su ID.

- **200 OK:** objeto `Sucursal`
- **404 NOT FOUND:** `"Sucursal {id} no encontrada"`

### GET `/{id}/detalle`
Obtiene la sucursal en formato `SucursalDTO`, complementando los datos con el **microservicio de Bodega** (`nombreBodega`, `capacidadMax`). Si la bodega no está disponible, devuelve el DTO con esos campos vacíos.

- **200 OK:** objeto `SucursalDTO`
- **404 NOT FOUND:** `"Sucursal {id} no encontrada"`

### GET `/{id}/stock`
Consulta el stock de la sucursal a través del **microservicio de Inventario**, usando el `idBodega` asociado.

- **200 OK:** stock de la bodega
- **200 OK:** `"Stock no disponible"` (si el inventario no responde)
- **404 NOT FOUND:** `"Sucursal {id} no encontrada"`

### PUT `/{id}`
Actualiza una sucursal existente (`direccionSucursal`, `horario`, `idBodega`).

- **Body:** objeto `Sucursal`
- **200 OK:** sucursal actualizada
- **404 NOT FOUND:** `"Sucursal {id} no encontrada"`

### DELETE `/{id}`
Elimina una sucursal por su ID.

- **200 OK:** `"Sucursal {id} eliminada"`
- **404 NOT FOUND:** `"Sucursal {id} no encontrada"`

## Métodos del Service (`SucursalService`)

| Método                                          | Descripción                                                        |
|-------------------------------------------------|--------------------------------------------------------------------|
| `crearSucursal(Sucursal)`                       | Guarda una nueva sucursal.                                         |
| `listarSucursales()`                            | Retorna todas las sucursales.                                      |
| `findById(Long id)`                             | Busca una sucursal por ID (`Optional<Sucursal>`).                  |
| `eliminarSucursal(Long id)`                     | Elimina si existe; retorna `boolean`.                              |
| `obtenerSucursalDTO(Long idSucursal)`           | Arma el `SucursalDTO` consultando el microservicio de Bodega.     |
| `consultarStock(Long idSucursal)`               | Consulta el stock vía microservicio de Inventario.                |
| `actualizarSucursal(Long id, Sucursal)`         | Actualiza dirección, horario e idBodega de la sucursal.           |

## Comunicación entre microservicios

El servicio usa `RestTemplate` (bean en `RestTemplateConfig`) para consumir:

- **Bodega:** `GET /api/v1/bodega/{idBodega}` → `BodegaDTO`
- **Inventario:** `GET /api/v1/inventario/stockPorBodega/{idBodega}` → stock

