# Sistema de Gestion de Productos (Java + JDBC + SQLite)

## Requisitos
- JDK 17 o superior
- Maven 3.8+

## Ejecucion
```bash
mvn compile exec:java
```
La base de datos `productos.db` se crea automaticamente en la carpeta del proyecto
al ejecutar por primera vez (el script equivalente esta en `sql/schema.sql`).

## Estructura
- `model/Producto.java`   -> modelo
- `db/ConexionDB.java`    -> conexion JDBC y creacion de la tabla
- `dao/ProductoDAO.java`  -> operaciones CRUD con PreparedStatement
- `ui/Main.java`          -> menu de consola y validaciones

## Persistencia
Cierre el programa (opcion 0), vuelva a ejecutarlo y use "Listar todos":
los productos registrados siguen almacenados en `productos.db`.

## Ejecutar en la nube (sin instalar nada)
- **Replit:** abrir el enlace del proyecto y pulsar **Run**. Se ejecuta `mvn -q compile exec:java`.
- **GitHub Codespaces:** en el repositorio, boton verde **Code > Codespaces > Create codespace**.
  Cuando cargue, en la terminal escribir: `mvn -q compile exec:java`
