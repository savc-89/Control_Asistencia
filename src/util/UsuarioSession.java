package util;

public class UsuarioSession {
    private static String correoUsuarioActual = null;

    public static String getCorreoUsuarioActual() {
        return correoUsuarioActual;
    }

    public static void setCorreoUsuarioActual(String correo) {
        correoUsuarioActual = correo;
    }

    public static boolean isUsuarioAutenticado() {
        return correoUsuarioActual != null;
    }

    public static void cerrarSesion() {
        correoUsuarioActual = null;
    }
}

