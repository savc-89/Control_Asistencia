package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conectarbd {

    private static final String URL = "jdbc:mysql://localhost:3307/control_asistencia";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "root";

    // Método estático para obtener la conexión
    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión exitosa");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el controlador JDBC", e);
        } catch (SQLException e) {
            throw new SQLException("Error en la conexión: " + e.getMessage(), e);
        }
        return conexion;
    }

    // Método estático para cerrar la conexión
    public static void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
