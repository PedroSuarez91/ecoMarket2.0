# reabastecimiento-ms

Microservicio de **gestión de reabastecimiento** para EcoMarket SPA. Permite registrar pedidos de reabastecimiento a proveedores, cada uno con su lista de ítems (productos y cantidades), validando contra los microservicios de Proveedores y Productos antes de persistir.

---

## Tecnologías

- Java 25
- Spring Boot 4.1.0
- Spring Data JPA
- MySQL (producción) / H2 (consola en memoria)
- Lombok
- Maven

---

## Requisitos previos

- JDK 25
- Maven 3.9+
- MySQL en ejecución con una base de datos llamada `dbreabastecimiento`

---

## Configuración

Archivo `src/main/resources/application.properties`:

```properties
spring.application.name=reabastecimiento-ms
server.port=8085
spring.datasource.url=jdbc:mysql://localhost:3306/dbreabastecimiento
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

| Parámetro | Valor |
|-----------|-------|
| Puerto | `8085` |
| Base de datos | `dbreabastecimiento` |
| Context path base | `/api/v1/reabastecimientos` |

---

## Endpoints / Métodos

Base URL: `http://localhost:8085/api/v1/reabastecimientos`

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| `GET` | `/api/v1/reabastecimientos` | Lista todos los reabastecimientos | `200 OK` con la lista · `204 NO_CONTENT` si está vacía |
| `POST` | `/api/v1/reabastecimientos` | Crea un nuevo pedido de reabastecimiento | `201 CREATED` con el registro · `409 CONFLICT` si falla la validación |
| `GET` | `/api/v1/reabastecimientos/{id}` | Obtiene un reabastecimiento por su ID | `200 OK` con el registro · `204 NO_CONTENT` si no existe |

### Detalle de los métodos

#### 1. `GET /api/v1/reabastecimientos`
Devuelve todos los reabastecimientos registrados.
- **200 OK** → lista de reabastecimientos.
- **204 NO_CONTENT** → no hay registros.

#### 2. `POST /api/v1/reabastecimientos`
Registra un nuevo pedido de reabastecimiento. La lógica de negocio:
- Asigna automáticamente la `fecha` con la fecha actual (`LocalDate.now()`).
- Si no se envía `estado`, se asigna `"PENDIENTE"` por defecto.
- Valida que el proveedor exista llamando a **proveedor-ms** (`GET http://localhost:8082/api/v1/proveedores/{idProveedor}`).
- Por cada ítem, valida que el producto exista llamando a **producto-ms** (`GET .../api/v1/productos/{idProducto}`).
- Si el proveedor o algún producto no existe, lanza excepción y responde `409 CONFLICT`.

Cuerpo de la petición (ejemplo):

```json
{
  "idProveedor": 1,
  "estado": "PENDIENTE",
  "items": [
    { "idProducto": 10, "cantidad": 50 },
    { "idProducto": 12, "cantidad": 30 }
  ]
}
```

- **201 CREATED** → reabastecimiento guardado.
- **409 CONFLICT** → proveedor o producto no encontrado.

#### 3. `GET /api/v1/reabastecimientos/{id}`
Busca un reabastecimiento por su identificador.
- **200 OK** → reabastecimiento encontrado.
- **204 NO_CONTENT** → no existe.

---

## Modelo de datos

### Entidad `Reabastecimiento`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idPedidoReabastecimiento` | `long` | PK autogenerada |
| `idProveedor` | `long` | ID del proveedor (referencia externa) |
| `fecha` | `LocalDate` | Fecha del pedido (asignada automáticamente) |
| `estado` | `String` | Estado del pedido (por defecto `PENDIENTE`) |
| `items` | `List<ItemReabastecimiento>` | Ítems del pedido (1:N, cascade ALL) |

### Entidad `ItemReabastecimiento`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idItemReabastecimiento` | `Long` | PK autogenerada |
| `idProducto` | `Long` | ID del producto (referencia externa) |
| `cantidad` | `Integer` | Cantidad a reabastecer |
| `reabastecimiento` | `Reabastecimiento` | Relación N:1 (`@JsonBackReference`) |

### DTOs (datos de microservicios externos)
- **`ProveedorDTO`** → `idProveedor`, `nombre`, `rut`.
- **`ProductoDTO`** → `idProducto`, `idInventario`, `tipoProducto`, `nombre`, `descripcion`, `marca`, `precioUnitario`, `estado`.

---

## Estructura del proyecto

```
reabastecimiento-ms/
└── src/main/java/ecomarket/reabastecimiento_ms/
    ├── ReabastecimientoMsApplication.java
    ├── config/
    │   └── RestTemplateConfig.java
    ├── controller/
    │   └── ReabastecimientoController.java
    ├── model/
    │   ├── Reabastecimiento.java
    │   ├── ItemReabastecimiento.java
    │   ├── ProductoDTO.java
    │   └── ProveedorDTO.java
    ├── repository/
    │   └── ReabastecimientoRepository.java
    └── service/
        └── ReabastecimientoService.java
```

---
