package controlador;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import conexion.conectarbd;
import modelo.Reportes;
import modelo.Usuario;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReporteController {

   

    private String validarTipoReporte(String tipoReporte) {
        switch (tipoReporte.toLowerCase()) {
            case "atraso":
                return "atraso";
            case "salida_anticipada":
                return "salida_anticipada";
            case "inasistencia":
                return "inasistencia";
            default:
                throw new IllegalArgumentException("Tipo de reporte inválido: " + tipoReporte);
        }
    }

public List<Reportes> obtenerReportes(String tipoReporte) {
    List<Reportes> reportes = new ArrayList<>();
    String sql = "SELECT idReporte, idUsuario, tipoReporte, fechaReporte, descripcion " +
                 "FROM reportes " +
                 "WHERE tipoReporte = ?";

    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, tipoReporte);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Reportes reporte = new Reportes();
                reporte.setIdReporte(rs.getInt("idReporte")); // Asegúrate de que este nombre coincida con el de tu base de datos
                reporte.setIdUsuario(rs.getInt("idUsuario")); // Asegúrate de que este nombre coincida con el de tu base de datos
                reporte.setTipoReporte(rs.getString("tipoReporte")); // Asegúrate de que este nombre coincida con el de tu base de datos
                reporte.setFechaReporte(rs.getDate("fechaReporte")); // Asegúrate de que este nombre coincida con el de tu base de datos
                reporte.setDescripcion(rs.getString("descripcion")); // Asegúrate de que este nombre coincida con el de tu base de datos
                reportes.add(reporte);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return reportes;
}
 






public void insertarReporte(int idUsuario, String tipoReporte, String descripcion) {
    // Validar el ID de usuario
    if (idUsuario <= 0) {
        throw new IllegalArgumentException("ID de usuario inválido: " + idUsuario);
    }

    // Validar el tipo de reporte
    if (!tipoReporte.equals("atraso") && !tipoReporte.equals("salida_anticipada") && !tipoReporte.equals("inasistencia")) {
        throw new IllegalArgumentException("Tipo de reporte inválido: " + tipoReporte);
    }

    String sql = "INSERT INTO reportes (idUsuario, tipoReporte, fechaReporte, descripcion) VALUES (?, ?, NOW(), ?)";
    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idUsuario);
        stmt.setString(2, tipoReporte);
        stmt.setString(3, descripcion);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Reporte insertado con éxito.");
        } else {
            System.out.println("No se insertó ningún reporte.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    private int obtenerUsuarioActual() {
        // Implementa este método según tu sistema de gestión de usuarios
        // Ejemplo:
        // return UsuarioSession.getCurrentUser().getId();
        return 1; // Retorna un ID de usuario ficticio para pruebas
    }

    private void mostrarPDF(String fileName) {
        try {
            File pdfFile = new File(fileName);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                System.out.println("El archivo PDF no existe.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
