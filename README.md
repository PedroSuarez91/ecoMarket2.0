# EcoMarket SPA — Sistema de Microservicios

Plataforma de e-commerce de productos ecológicos construida con una arquitectura de
**14 microservicios** independientes. Cada servicio es una aplicación **Spring Boot**
autónoma, con su propio puerto, su propia base de datos **MySQL** y la misma estructura
por capas: `Controller → Service → Repository → Model`.

La comunicación entre servicios se realiza vía **REST** con `RestTemplate`.

## Stack tecnológico

- Java 25 + Spring Boot 4.1.0
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Positive`, etc.)
- Maven

## Tabla de servicios

| Microservicio | Puerto | Path base | Base de datos |
|---|---|---|---|
| usuario-ms | 8081 | `/api/v1/usuarios` | usuariosdb |
| proveedor-ms | 8082 | `/api/v1/proveedores` | proveedoresdb |
| carro-ms | 8083 | `/api/v1/carros` | dbcarro |
| sucursal-ms | 8084 | `/api/v1/sucursales` | sucursaldb |
| reabastecimiento-ms | 8085 | `/api/v1/reabastecimientos` | dbreabastecimiento |
| factura-ms | 8087 | `/api/v1/facturas` | dbfactura |
| catalogo | 8090 | `/api/v1/productos` (+ catálogos, categorías, reseñas) | catalogodb |
| cupon | 8091 | `/api/v1/cupones` | cupondb |
| soporte | 9091 | `/api/v1/soporte` | soportedb |
| autentificacion | 9092 | `/api/v1/autentificacion` | autentificaciondb |
| inventario | 9093 | `/api/v1/inventario` | inventariodb |
| bodega | 9094 | `/api/v1/bodega` | bodegadb |
| envio_service | 9095 | `/api/v1/envios` (+ rutas) | enviosdb |
| pedido-ms | 8093 | `/api/v1/pedidos` (+ direcciones) | dbpedido |

---

## Métodos por microservicio

### usuario-ms — `8081`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/usuarios` | Registra un nuevo usuario |
| GET | `/api/v1/usuarios` | Lista todos los usuarios |
| GET | `/api/v1/usuarios/{id}` | Obtiene un usuario por su id |
| GET | `/api/v1/usuarios/emailUsuario/{emailUsuario}` | Busca un usuario por su email |
| PUT | `/api/v1/usuarios/{id}` | Actualiza un usuario existente |
| DELETE | `/api/v1/usuarios/{id}` | Elimina un usuario |

### proveedor-ms — `8082`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/proveedores` | Registra un nuevo proveedor |
| GET | `/api/v1/proveedores` | Lista todos los proveedores |
| GET | `/api/v1/proveedores/{id}` | Obtiene un proveedor por su id |
| PUT | `/api/v1/proveedores/{id}` | Actualiza un proveedor |
| DELETE | `/api/v1/proveedores/{id}` | Elimina un proveedor |

### carro-ms — `8083`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/carros` | Crea un carro; calcula subtotal y total consultando usuario, catálogo y cupón |
| GET | `/api/v1/carros` | Lista todos los carros |
| GET | `/api/v1/carros/{id}` | Obtiene un carro por su id |
| PUT | `/api/v1/carros/{id}` | Actualiza tipo de entrega y dirección del carro |
| DELETE | `/api/v1/carros/{id}` | Elimina un carro |

### sucursal-ms — `8084`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/sucursales` | Crea una nueva sucursal |
| GET | `/api/v1/sucursales` | Lista todas las sucursales |
| GET | `/api/v1/sucursales/{id}` | Obtiene una sucursal por su id |
| GET | `/api/v1/sucursales/{id}/detalle` | Devuelve el detalle de la sucursal con datos de su bodega |
| GET | `/api/v1/sucursales/{id}/stock` | Consulta el stock asociado a la sucursal |
| PUT | `/api/v1/sucursales/{id}` | Actualiza una sucursal |
| DELETE | `/api/v1/sucursales/{id}` | Elimina una sucursal |

### reabastecimiento-ms — `8085`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/reabastecimientos` | Crea una orden de reabastecimiento (valida proveedor y productos) |
| GET | `/api/v1/reabastecimientos` | Lista todas las órdenes de reabastecimiento |
| GET | `/api/v1/reabastecimientos/{id}` | Obtiene una orden por su id |

### factura-ms — `8087`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/facturas` | Lista todas las facturas |
| GET | `/api/v1/facturas/{id}` | Obtiene una factura por su id |
| GET | `/api/v1/facturas/pedido/{idPedido}` | Obtiene la factura asociada a un pedido |
| POST | `/api/v1/facturas/emitir/{idPedido}` | Emite una factura a partir de un pedido (neto, IVA y total) |

### catalogo — `8090`

Servicio con 4 controladores: catálogos, categorías, productos y reseñas.

**Productos**

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/productos` | Registra un nuevo producto |
| GET | `/api/v1/productos` | Lista todos los productos |
| GET | `/api/v1/productos/{id}` | Obtiene un producto por su id |
| PUT | `/api/v1/productos/{id}` | Actualiza un producto |
| GET | `/api/v1/productos/categoria/{idCategoria}` | Lista productos de una categoría |
| GET | `/api/v1/productos/buscar?nombre=` | Busca productos por nombre |
| GET | `/api/v1/productos/marca/{marca}` | Lista productos de una marca |
| GET | `/api/v1/productos/precio/rango?min=&max=` | Lista productos dentro de un rango de precio |
| GET | `/api/v1/productos/precio/maximo?max=` | Lista productos bajo un precio máximo |
| GET | `/api/v1/productos/precio/minimo?min=` | Lista productos sobre un precio mínimo |
| DELETE | `/api/v1/productos/{id}` | Elimina un producto | 

**Catálogos**

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/catalogos` | Lista todos los catálogos |
| GET | `/api/v1/catalogos/{id}` | Obtiene un catálogo por su id |
| POST | `/api/v1/catalogos` | Crea un catálogo |
| PUT | `/api/v1/catalogos/{id}` | Actualiza un catálogo |
| DELETE | `/api/v1/catalogos/{id}` | Elimina un catálogo |

**Categorías**

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/categorias` | Lista todas las categorías |
| GET | `/api/v1/categorias/{id}` | Obtiene una categoría por su id |
| POST | `/api/v1/categorias` | Crea una categoría |
| DELETE | `/api/v1/categorias/{id}` | Elimina una categoría |

**Reseñas**

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/resenias` | Lista todas las reseñas |
| GET | `/api/v1/resenias/producto/{idProducto}` | Lista las reseñas de un producto |
| POST | `/api/v1/resenias` | Registra una reseña (el producto debe existir) |
| PUT | `/api/v1/resenias/{id}` | Actualiza una reseña |
| DELETE | `/api/v1/resenias/{id}` | Elimina una reseña |

### cupon — `8091`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/cupones` | Crea un cupón (el código es único) |
| GET | `/api/v1/cupones` | Lista todos los cupones |
| GET | `/api/v1/cupones/{id}` | Obtiene un cupón por su id |
| GET | `/api/v1/cupones/codigo/{codigo}` | Busca un cupón por su código |
| GET | `/api/v1/cupones/validar/{codigo}` | Valida si un cupón es aplicable (devuelve true/false) |
| PUT | `/api/v1/cupones/{id}` | Actualiza un cupón |
| DELETE | `/api/v1/cupones/{id}` | Elimina un cupón |

### soporte — `9091`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/soporte` | Crea un ticket de soporte (valida el usuario) |
| GET | `/api/v1/soporte` | Lista todos los tickets |
| GET | `/api/v1/soporte/{id}` | Obtiene un ticket por su id |
| PUT | `/api/v1/soporte/{id}/cerrar` | Cierra un ticket de soporte |

### autentificacion — `9092`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/autentificacion/login` | Valida credenciales y registra el login |
| GET | `/api/v1/autentificacion` | Lista los registros de login |

### inventario — `9093`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/inventario` | Crea un registro de inventario (valida el producto en catálogo) |
| GET | `/api/v1/inventario` | Lista todos los inventarios |
| GET | `/api/v1/inventario/stockPorBodega/{idBodega}` | Total de stock de una bodega |
| GET | `/api/v1/inventario/alertas` | Lista los productos bajo el stock mínimo |
| PUT | `/api/v1/inventario/{id}` | Actualiza un registro de inventario |
| PUT | `/api/v1/inventario/descontar/{idProducto}/{cantidad}` | Descuenta stock de un producto |

### bodega — `9094`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/bodega` | Crea una bodega |
| GET | `/api/v1/bodega` | Lista todas las bodegas |
| GET | `/api/v1/bodega/{id}` | Obtiene una bodega por su id |
| GET | `/api/v1/bodega/{id}/detalle` | Detalle de la bodega con datos de sucursal e inventario |
| GET | `/api/v1/bodega/listar_con_capacidad` | Lista bodegas con su capacidad ocupada |
| DELETE | `/api/v1/bodega/{id}` | Elimina una bodega |

Documentación Swagger: `http://localhost:9094/doc/swagger-ui.html`

### envio_service — `9095`

**Envíos**

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/envios` | Crea un envío (valida el pedido) |
| GET | `/api/v1/envios` | Lista todos los envíos |
| GET | `/api/v1/envios/{id}` | Obtiene un envío por su id |
| PUT | `/api/v1/envios/{id}/estado?estadoEnvio=` | Actualiza el estado (`PREPARANDO`, `EN_CAMINO`, `ENTREGADO`) |
| DELETE | `/api/v1/envios/{id}` | Elimina un envío |

**Rutas de entrega**

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/rutas` | Crea una ruta de entrega |
| GET | `/api/v1/rutas` | Lista todas las rutas |
| GET | `/api/v1/rutas/{id}` | Obtiene una ruta por su id |
| PUT | `/api/v1/rutas/{id}` | Actualiza una ruta |
| DELETE | `/api/v1/rutas/{id}` | Elimina una ruta |

Documentación Swagger: `http://localhost:9095/doc/swagger-ui.html`

### pedido-ms — `8093`

**Pedidos**

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/pedidos` | Lista todos los pedidos |
| GET | `/api/v1/pedidos/{id}` | Obtiene un pedido por su id |
| GET | `/api/v1/pedidos/usuario/{idUsuario}` | Lista los pedidos de un usuario |
| POST | `/api/v1/pedidos/carro/{idCarro}?idDireccion=` | Crea un pedido a partir de un carro |
| PUT | `/api/v1/pedidos/{id}/direccion/{idDireccion}` | Asigna o cambia la dirección del pedido |
| PUT | `/api/v1/pedidos/{id}/estado?estado=` | Actualiza el estado (`PENDIENTE`, `PAGADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`) |
| DELETE | `/api/v1/pedidos/{id}` | Elimina un pedido |

**Direcciones**

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/direcciones` | Lista todas las direcciones |
| GET | `/api/v1/direcciones/{id}` | Obtiene una dirección por su id |
| POST | `/api/v1/direcciones` | Crea una dirección |
| PUT | `/api/v1/direcciones/{id}` | Actualiza una dirección |
| DELETE | `/api/v1/direcciones/{id}` | Elimina una dirección |

---

## Dependencias entre servicios

Algunos endpoints llaman a otros microservicios al ejecutarse. El servicio destino debe
estar corriendo:

- `carro` → usuario, catálogo, cupón
- `inventario` → catálogo
- `bodega` → sucursal, inventario
- `sucursal` → bodega, inventario
- `reabastecimiento` → proveedor, catálogo
- `soporte` → usuario
- `pedido` → carro
- `factura` → pedido
- `envio` → pedido
- `autentificacion` → usuario

Cada microservicio levanta en su puerto indicado en la tabla. La base de datos MySQL
correspondiente debe existir; Hibernate crea las tablas automáticamente
(`spring.jpa.hibernate.ddl-auto=update`).
