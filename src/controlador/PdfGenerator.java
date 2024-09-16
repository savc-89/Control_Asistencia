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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PdfGenerator {

  // Añadimos esta función para obtener las asistencias de los usuarios que llegaron tarde
private void agregarAsistenciasAtrasadas(Connection conn, Document document) throws SQLException, DocumentException {
    String sql = "SELECT u.nombre, a.fecha, a.horaEntrada FROM asistencia a " +
                 "JOIN usuarios u ON a.idUsuario = u.idUsuario " +
                 "WHERE a.horaEntrada > '09:00:00'"; // Ajusta la hora límite para definir 'tarde'

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
            // Agrega un título o párrafo al PDF antes de la tabla
            document.add(new Paragraph("Lista de Asistencias Tardías:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(" "));

            // Crear una nueva tabla para las asistencias
            PdfPTable asistenciaTable = new PdfPTable(3);  // Tres columnas: Nombre, Fecha, Hora de Entrada
            asistenciaTable.setWidthPercentage(100);
            asistenciaTable.addCell(new PdfPCell(new Paragraph("Nombre Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            asistenciaTable.addCell(new PdfPCell(new Paragraph("Fecha", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
            asistenciaTable.addCell(new PdfPCell(new Paragraph("Hora de Entrada", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            while (rs.next()) {
                String nombreUsuario = rs.getString("nombre");
                String fecha = dateFormat.format(rs.getDate("fecha"));
                String horaEntrada = rs.getTime("horaEntrada").toString();

                asistenciaTable.addCell(nombreUsuario);
                asistenciaTable.addCell(fecha);
                asistenciaTable.addCell(horaEntrada);
            }

            // Añadir la tabla al documento
            document.add(asistenciaTable);
        }
    }
}

// Modificación del método generarReporte para incluir las asistencias tardías
private void generarReporte(String tipoReporte, String fileName, List<Reportes> reportes) {
    Document document = new Document();
    FileOutputStream fos = null;
    try {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();  // Elimina el archivo si ya existe
        }

        fos = new FileOutputStream(fileName);
        PdfWriter.getInstance(document, fos);
        document.open();

        document.add(new Paragraph(tipoReporte, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);  // Ajusta el número de columnas
        table.setWidthPercentage(100);
        table.addCell(new PdfPCell(new Paragraph("ID Reporte", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Paragraph("ID Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Paragraph("Nombre Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Paragraph("Tipo Reporte", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Paragraph("Fecha Reporte", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Paragraph("Descripción", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Conexión para obtener el nombre del usuario y las asistencias tardías
        try (Connection conn = conectarbd.obtenerConexion()) {
            for (Reportes reporte : reportes) {
                // Agregar los datos del reporte
                table.addCell(String.valueOf(reporte.getIdReporte()));
                table.addCell(String.valueOf(reporte.getIdUsuario()));
                
                // Obtener el nombre del usuario
                String nombreUsuario = obtenerNombreUsuario(conn, reporte.getIdUsuario());
                table.addCell(nombreUsuario);

                table.addCell(reporte.getTipoReporte());
                table.addCell(dateFormat.format(reporte.getFechaReporte()));
                table.addCell(reporte.getDescripcion());
            }

            // Añadir la tabla de reportes al documento
            document.add(table);

            // Añadir la lista de asistencias tardías al PDF
            agregarAsistenciasAtrasadas(conn, document);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("PDF generado con éxito en: " + fileName);

    } catch (DocumentException | IOException e) {
        System.err.println("Error al generar el PDF: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (document.isOpen()) {
                document.close();
            }
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el FileOutputStream: " + e.getMessage());
        }
    }
}


    private String obtenerNombreUsuario(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT nombre FROM usuarios WHERE idUsuario = ?";  // Corrección del nombre de columna
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                } else {
                    return "Nombre no encontrado";
                }
            }
        }
    }

    // Métodos para generar reportes específicos
    public void generarReporteAtrasos(String fileName, List<Reportes> reportes) {
        generarReporte("Reporte de Atrasos", fileName, reportes);
    }

    public void generarReporteSalidasAnticipadas(String fileName, List<Reportes> reportes) {
        generarReporte("Reporte de Salidas Anticipadas", fileName, reportes);
    }

    public void generarReporteInasistencias(String fileName, List<Reportes> reportes) {
        generarReporte("Reporte de Inasistencias", fileName, reportes);
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