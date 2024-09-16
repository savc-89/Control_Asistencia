package controlador;

import org.mindrot.jbcrypt.BCrypt;
import conexion.conectarbd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

public class UsuarioController {

public void crearUsuario(String correo, String nombre, String rol, String password) {
    String sql = "INSERT INTO usuarios (correo, nombre, rol, password) VALUES (?, ?, ?, ?)";

    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Cifrar la contraseña antes de guardarla
        String passwordCifrada = cifrarPassword(password);

        stmt.setString(1, correo);
        stmt.setString(2, nombre);
        stmt.setString(3, rol);
        stmt.setString(4, passwordCifrada);

        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    /**
     * Obtiene una lista de trabajadores (empleados) desde la base de datos.
     * 
     * @return Una lista de trabajadores.
     */
   public List<Usuario> obtenerTrabajadores() {
    List<Usuario> trabajadores = new ArrayList<>();
    String sql = "SELECT idUsuario, correo, nombre, rol FROM usuarios WHERE rol = 'empleado'";

    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int idUsuario = rs.getInt("idUsuario");
            String correo = rs.getString("correo");
            String nombre = rs.getString("nombre");
            String rol = rs.getString("rol");

            // Agrega el trabajador a la lista
            trabajadores.add(new Usuario(idUsuario, correo, "", nombre, rol));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return trabajadores;
}


    /**
     * Cifra la contraseña usando BCrypt.
     * 
     * @param password La contraseña en texto plano.
     * @return La contraseña cifrada.
     */
    private String cifrarPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica si existe un administrador en la base de datos y crea uno si no existe.
     */
    public void crearAdminSiNoExiste() {
        String sqlCheck = "SELECT COUNT(*) FROM usuarios WHERE rol = 'Administrador'";
        String sqlInsert = "INSERT INTO usuarios (correo, nombre, rol, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = conectarbd.obtenerConexion();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                // No hay administradores, así que crea uno
                try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                    String correo = "admin@example.com";
                    String nombre = "Administrador";
                    String rol = "Administrador";
                    String password = "admin123"; // Usa una contraseña segura en producción

                    String passwordCifrada = cifrarPassword(password);

                    insertStmt.setString(1, correo);
                    insertStmt.setString(2, nombre);
                    insertStmt.setString(3, rol);
                    insertStmt.setString(4, passwordCifrada);

                    insertStmt.executeUpdate();
                    System.out.println("Cuenta de administrador creada con éxito.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Considera usar un logger en lugar de e.printStackTrace()
        }
    }
}
