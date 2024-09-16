package controlador;

import conexion.conectarbd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import util.UsuarioSession;

public class LoginController {
    public Usuario autenticar(String correo, String password) {
        Usuario usuario = null;
        Connection conn = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try {
            // Obtén la conexión
            conn = conectarbd.obtenerConexion();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Recupera la contraseña cifrada almacenada en la base de datos
                String passwordHash = rs.getString("password");
                
                // Verifica la contraseña proporcionada contra la cifrada
                if (BCrypt.checkpw(password, passwordHash)) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setPassword(passwordHash); // Guarda el hash, no la contraseña en texto plano

                    // Guarda el correo del usuario en la sesión
                    UsuarioSession.setCorreoUsuarioActual(correo);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra la conexión en el bloque finally
            conectarbd.cerrarConexion(conn);
        }

        return usuario;
    }
}
