package com.tienda.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Gestiona la conexion JDBC con la base de datos SQLite y crea la tabla si no existe. */
public class ConexionDB {

    private static final String URL = "jdbc:sqlite:productos.db";

    private static final String CREAR_TABLA = """
            CREATE TABLE IF NOT EXISTS productos (
                codigo      TEXT PRIMARY KEY,
                nombre      TEXT NOT NULL,
                descripcion TEXT,
                precio      REAL NOT NULL CHECK (precio >= 0),
                cantidad    INTEGER NOT NULL CHECK (cantidad >= 0)
            )
            """;

    /** Devuelve una nueva conexion. Quien la llame debe cerrarla (try-with-resources). */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Crea la tabla de productos si aun no existe. */
    public static void inicializar() throws SQLException {
        try (Connection con = getConexion();
             Statement st = con.createStatement()) {
            st.execute(CREAR_TABLA);
        }
    }
}
