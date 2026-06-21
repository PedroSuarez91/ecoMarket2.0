# usuario-ms

Microservicio de gestión de usuarios construido con **Spring Boot**. Expone una API REST para crear, consultar, actualizar y eliminar usuarios, con persistencia en base de datos a través de Spring Data JPA.

## Tecnologías

- Java 25
- Spring Boot 4.1.0 (Web MVC, Data JPA)
- MySQL (producción) / H2 (consola)
- Lombok
- Maven

## Configuración

El servicio se levanta en el puerto **8081** y se conecta por defecto a una base de datos MySQL llamada `usuariosdb`:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/usuariosdb
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

## Modelo: `Usuario`

Entidad mapeada a la tabla `usuarios`.

| Campo          | Tipo     | Restricciones                  |
|----------------|----------|--------------------------------|
| `idUsuario`    | `long`   | PK, autogenerado (IDENTITY)    |
| `nombre`       | `String` | obligatorio, máx. 25           |
| `apellido`     | `String` | obligatorio, máx. 25           |
| `emailUsuario` | `String` | obligatorio, máx. 100          |
| `password`     | `String` | obligatorio, máx. 25           |
| `rol`          | `String` | obligatorio                    |

---

## Endpoints REST

Base path: `api/v1/usuarios`

| Método HTTP | Ruta                                  | Descripción                          |
|-------------|---------------------------------------|--------------------------------------|
| `POST`      | `/api/v1/usuarios`                    | Crea un nuevo usuario                |
| `GET`       | `/api/v1/usuarios`                    | Lista todos los usuarios             |
| `GET`       | `/api/v1/usuarios/{id}`               | Busca un usuario por su ID           |
| `GET`       | `/api/v1/usuarios/emailUsuario/{email}` | Busca un usuario por su email      |
| `PUT`       | `/api/v1/usuarios/{id}`               | Actualiza un usuario existente       |
| `DELETE`    | `/api/v1/usuarios/{id}`               | Elimina un usuario por su ID         |

### Detalle de los métodos del Controller

#### `guardarUsuario`
- **`POST /api/v1/usuarios`**
- Recibe un `Usuario` en el cuerpo de la petición (`@RequestBody`).
- Devuelve el usuario guardado (con su ID generado).

#### `listarUsuarios`
- **`GET /api/v1/usuarios`**
- Devuelve la lista completa de usuarios (`List<Usuario>`).

#### `findById`
- **`GET /api/v1/usuarios/{id}`**
- Busca por ID. Devuelve el usuario o `null` si no existe.

#### `findByEmail`
- **`GET /api/v1/usuarios/emailUsuario/{emailUsuario}`**
- Busca por email. Devuelve el usuario o `null` si no existe.

#### `actualizarUsuario`
- **`PUT /api/v1/usuarios/{id}`**
- Recibe el ID en la ruta y un `Usuario` en el cuerpo.
- Actualiza nombre, apellido, email, password y rol del usuario existente.

#### `eliminarUsuario`
- **`DELETE /api/v1/usuarios/{id}`**
- Elimina el usuario con el ID indicado. No devuelve contenido.

---

## Capa de servicio: `UsuarioService`

Contiene la lógica de negocio y delega la persistencia en `UsuarioRepository`.

| Método                                              | Descripción                                                                 |
|-----------------------------------------------------|-----------------------------------------------------------------------------|
| `guardarUsuario(Usuario usuario)`                   | Guarda un usuario con `save()`.                                             |
| `listarUsuarios()`                                  | Devuelve todos los usuarios con `findAll()`.                               |
| `findById(Long id)`                                 | Devuelve un `Optional<Usuario>` buscando por ID.                          |
| `findByEmailUsuario(String emailUsuario)`           | Busca por email y devuelve un DTO copiado del usuario, o `null`.          |
| `actualizarUsuario(Long id, Usuario usuario)`       | Busca por ID (lanza excepción si no existe) y actualiza sus campos.        |
| `eliminarUsuario(Long id)`                          | Elimina el usuario por ID con `deleteById()`.                             |

---

## Repositorio: `UsuarioRepository`

Interfaz que extiende `JpaRepository<Usuario, Long>`, lo que provee los métodos CRUD estándar (`save`, `findAll`, `findById`, `deleteById`, etc.).

Método de consulta personalizado:

| Método                                                  | Descripción                                  |
|---------------------------------------------------------|----------------------------------------------|
| `Optional<Usuario> findByEmailUsuario(String email)`    | Busca un usuario por su email (derivado).    |

---

## Ejemplo de petición (crear usuario)

```bash
POST http://localhost:8081/api/v1/usuarios 
  
  '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "emailUsuario": "juan@ejemplo.com",
    "password": "secreta123",
    "rol": "ADMIN"
  }'
```
