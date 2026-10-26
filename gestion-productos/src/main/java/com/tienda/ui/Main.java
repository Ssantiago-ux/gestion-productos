package com.tienda.ui;

import com.tienda.dao.ProductoDAO;
import com.tienda.db.ConexionDB;
import com.tienda.model.Producto;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Capa de presentacion: menu de consola del Sistema de Gestion de Productos. */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final ProductoDAO dao = new ProductoDAO();

    public static void main(String[] args) {
        try {
            ConexionDB.inicializar();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con la base de datos: " + e.getMessage());
            return;
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ", 0, 5);
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> actualizar();
                    case 4 -> eliminar();
                    case 5 -> listarTodos();
                    case 0 -> System.out.println("Hasta pronto.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== SISTEMA DE GESTION DE PRODUCTOS =====");
        System.out.println("1. Registrar producto");
        System.out.println("2. Consultar (por codigo o nombre)");
        System.out.println("3. Actualizar producto");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Listar todos los productos");
        System.out.println("0. Salir");
    }

    // RF1
    private static void registrar() throws SQLException {
        String codigo = leerTextoObligatorio("Codigo: ");
        if (dao.buscarPorCodigo(codigo) != null) {
            System.out.println("Ya existe un producto con ese codigo.");
            return;
        }
        String nombre = leerTextoObligatorio("Nombre: ");
        System.out.print("Descripcion: ");
        String descripcion = sc.nextLine().trim();
        double precio = leerPrecio("Precio: ");
        int cantidad = leerEntero("Cantidad disponible: ", 0, Integer.MAX_VALUE);

        Producto p = new Producto(codigo, nombre, descripcion, precio, cantidad);
        System.out.println(dao.insertar(p) ? "Producto registrado correctamente." : "No se pudo registrar.");
    }

    // RF2
    private static void consultar() throws SQLException {
        System.out.println("1. Buscar por codigo  2. Buscar por nombre");
        int tipo = leerEntero("Opcion: ", 1, 2);
        if (tipo == 1) {
            String codigo = leerTextoObligatorio("Codigo: ");
            Producto p = dao.buscarPorCodigo(codigo);
            if (p == null) {
                System.out.println("No existe un producto con ese codigo.");
            } else {
                imprimirEncabezado();
                System.out.println(p);
            }
        } else {
            String nombre = leerTextoObligatorio("Nombre (o parte): ");
            imprimirLista(dao.buscarPorNombre(nombre));
        }
    }

    private static void listarTodos() throws SQLException {
        imprimirLista(dao.listarTodos());
    }

    // RF3
    private static void actualizar() throws SQLException {
        String codigo = leerTextoObligatorio("Codigo del producto a actualizar: ");
        Producto p = dao.buscarPorCodigo(codigo);
        if (p == null) {
            System.out.println("No existe un producto con ese codigo.");
            return;
        }
        System.out.println("Producto actual:");
        imprimirEncabezado();
        System.out.println(p);

        System.out.print("Nueva descripcion (Enter para conservar): ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) p.setDescripcion(desc);

        System.out.print("Nuevo precio (Enter para conservar): ");
        String precioTxt = sc.nextLine().trim();
        if (!precioTxt.isEmpty()) {
            try {
                double nuevo = Double.parseDouble(precioTxt);
                if (nuevo < 0) {
                    System.out.println("El precio no puede ser negativo. Actualizacion cancelada.");
                    return;
                }
                p.setPrecio(nuevo);
            } catch (NumberFormatException e) {
                System.out.println("Precio invalido. Actualizacion cancelada.");
                return;
            }
        }

        System.out.print("Nueva cantidad (Enter para conservar): ");
        String cantTxt = sc.nextLine().trim();
        if (!cantTxt.isEmpty()) {
            try {
                int nueva = Integer.parseInt(cantTxt);
                if (nueva < 0) {
                    System.out.println("La cantidad no puede ser negativa. Actualizacion cancelada.");
                    return;
                }
                p.setCantidad(nueva);
            } catch (NumberFormatException e) {
                System.out.println("Cantidad invalida. Actualizacion cancelada.");
                return;
            }
        }

        System.out.println(dao.actualizar(p) ? "Producto actualizado." : "No se pudo actualizar.");
    }

    // RF4
    private static void eliminar() throws SQLException {
        String codigo = leerTextoObligatorio("Codigo del producto a eliminar: ");
        Producto p = dao.buscarPorCodigo(codigo);
        if (p == null) {
            System.out.println("No existe un producto con ese codigo.");
            return;
        }
        imprimirEncabezado();
        System.out.println(p);
        System.out.print("Esta seguro de eliminarlo? (s/n): ");
        String r = sc.nextLine().trim().toLowerCase();
        if (r.equals("s") || r.equals("si")) {
            System.out.println(dao.eliminar(codigo) ? "Producto eliminado." : "No se pudo eliminar.");
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    // ---------- Utilidades de presentacion y validacion ----------

    private static void imprimirEncabezado() {
        System.out.println(String.format("%-10s | %-20s | %-25s | %13s | %5s",
                "CODIGO", "NOMBRE", "DESCRIPCION", "PRECIO", "CANT."));
        System.out.println("-".repeat(85));
    }

    private static void imprimirLista(List<Producto> lista) {
        if (lista.isEmpty()) {
            System.out.println("No se encontraron productos.");
            return;
        }
        imprimirEncabezado();
        lista.forEach(System.out::println);
    }

    private static String leerTextoObligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String t = sc.nextLine().trim();
            if (!t.isEmpty()) return t;
            System.out.println("El campo no puede estar vacio.");
        }
    }

    private static double leerPrecio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= 0) return v;
                System.out.println("El precio no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println("Debe ser un entero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }
}
