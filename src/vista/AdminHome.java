/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import util.UsuarioSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import conexion.conectarbd;
import controlador.PdfGenerator;
import controlador.ReporteController;
import java.util.List;
import java.util.ArrayList;
import controlador.UsuarioController;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Reportes;
import modelo.Usuario;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


/**
 *
 * @author yorda
 */
public class AdminHome extends javax.swing.JFrame {

    /**
     * Creates new form AdminHome
     */
    private Usuario usuario;

    public AdminHome(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        cargarDatosTrabajadores();
        setLocationRelativeTo(null);
        
    }

    private void cargarDatosTrabajadores() {
    DefaultTableModel model = (DefaultTableModel) jTableTrabajador.getModel();
    model.setColumnIdentifiers(new String[]{"Correo", "Nombre", "Rol"}); // Etiquetas para la interfaz

    // Obtener los datos de los trabajadores
    UsuarioController trabajadorController = new UsuarioController();
    List<Usuario> trabajadores = trabajadorController.obtenerTrabajadores();

    // Limpiar el modelo de la tabla antes de añadir nuevos datos
    model.setRowCount(0);

    // Añadir los datos al modelo de la tabla
    for (Usuario trabajador : trabajadores) {
        model.addRow(new Object[]{trabajador.getCorreo(), trabajador.getNombre(), trabajador.getRol()});
    }
}






       

 private void agregarTrabajador() {
    // Recuperar datos de los campos
    String nombre = txtNombreTrabajador.getText().trim();
    String correo = txtCorreoTrabajador.getText().trim();
    String rol = (String) cbRolTrabajador.getSelectedItem();  // Obtener rol del JComboBox
     String contraseña = txtContraseñaTrabajador.getText().trim();

    // Validar que no haya campos vacíos
    if (nombre.isEmpty() || correo.isEmpty() || rol.isEmpty() || contraseña.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos deben ser llenados.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Crear un nuevo trabajador en la base de datos
    UsuarioController usuarioController = new UsuarioController();
    usuarioController.crearUsuario(correo, nombre, rol, contraseña);

    // Agregar el nuevo trabajador a la tabla
    DefaultTableModel model = (DefaultTableModel) jTableTrabajador.getModel();
    model.addRow(new Object[]{correo, nombre, rol});

    // Limpiar campos de texto
    txtNombreTrabajador.setText("");
    txtCorreoTrabajador.setText("");
    txtContraseñaTrabajador.setText("");

    JOptionPane.showMessageDialog(this, "Trabajador agregado con éxito.");
}

    
// Método para generar y mostrar reportes en PDF

private String validarTipoReporte(String tipoReporte) {
    // Normaliza el tipo de reporte para que coincida con el formato del enum en la base de datos
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




public void generarYMostrarReportes(String tipoReporte, String descripcion) {
    // Validar el tipo de reporte
    String tipoValidado = validarTipoReporte(tipoReporte);
    
    // Obtener el ID del usuario desde la sesión
    int idUsuario = obtenerIdUsuarioDesdeSesion();
    
    // Insertar el reporte en la base de datos
    ReporteController reporteController = new ReporteController();
    reporteController.insertarReporte(idUsuario, tipoValidado, descripcion);
    
    // Obtener los reportes
    List<Reportes> reportes = reporteController.obtenerReportes(tipoValidado);
    
    // Generar el PDF correspondiente
    PdfGenerator pdfGenerator = new PdfGenerator();
    String fileName = "reporte_" + tipoValidado + ".pdf";
    
    switch (tipoValidado) {
        case "atraso":
            pdfGenerator.generarReporteAtrasos(fileName, reportes);
            break;
        case "salida_anticipada":
            pdfGenerator.generarReporteSalidasAnticipadas(fileName, reportes);
            break;
        case "inasistencia":
            pdfGenerator.generarReporteInasistencias(fileName, reportes);
            break;
    }
    
    // Mostrar el PDF generado
    mostrarPDF(fileName);
}




private int obtenerIdUsuarioDesdeSesion() {
    // Verifica si hay un usuario en sesión
    if (!UsuarioSession.isUsuarioAutenticado()) {
        throw new IllegalStateException("No hay un usuario autenticado.");
    }
    
    // Obtiene el correo electrónico del usuario en sesión
    String correo = UsuarioSession.getCorreoUsuarioActual();
    
    // Realiza una consulta a la base de datos para obtener el ID del usuario
    String sql = "SELECT idUsuario FROM usuarios WHERE correo = ?";
    
    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, correo);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("idUsuario");
            } else {
                throw new IllegalStateException("No se encontró el ID del usuario.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error al obtener el ID del usuario desde la base de datos.", e);
    }
}



// Método para obtener un usuario por su correo electrónico
private Usuario obtenerUsuarioPorCorreo(String correo) {
    Usuario usuario = null;
    String sql = "SELECT * FROM usuarios WHERE correo = ?";
    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, correo);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setRol(rs.getString("rol"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return usuario;
}

// Método para mostrar el PDF generado
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


/**
 * Método para abrir el archivo PDF generado.
 * @param nombreArchivo Nombre del archivo PDF a abrir.
 */







public String obtenerNombreUsuario(int idUsuario) {
    String nombreUsuario = null;
    String sql = "SELECT nombre FROM usuarios WHERE idUsuario = ?";

    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Establece el parámetro en la consulta
        stmt.setInt(1, idUsuario);

        try (ResultSet rs = stmt.executeQuery()) {
            // Si hay un resultado, obtiene el nombre del usuario
            if (rs.next()) {
                nombreUsuario = rs.getString("nombre");
            }
        }

    } catch (SQLException e) {
        // Imprime el error en caso de una excepción
        e.printStackTrace();
    }

    return nombreUsuario;
}





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNombreTrabajador = new javax.swing.JTextField();
        txtCorreoTrabajador = new javax.swing.JTextField();
        txtContraseñaTrabajador = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTrabajador = new javax.swing.JTable();
        btnagregartrabajador = new javax.swing.JButton();
        cbRolTrabajador = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnReporteAtrasos = new javax.swing.JButton();
        btnReporteSalida = new javax.swing.JButton();
        ReporteInasistencia = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableReportes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Nombre");

        jLabel2.setText("Correo");

        jLabel3.setText("Rol");

        jLabel4.setText("Contraseña");

        jTableTrabajador.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "correo", "nombre", "rol"
            }
        ));
        jScrollPane1.setViewportView(jTableTrabajador);

        btnagregartrabajador.setText("Nuevo Trabajador");
        btnagregartrabajador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnagregartrabajadorActionPerformed(evt);
            }
        });

        cbRolTrabajador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "empleado", "administrador" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNombreTrabajador, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(txtCorreoTrabajador, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(txtContraseñaTrabajador)
                            .addComponent(cbRolTrabajador, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btnagregartrabajador)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(148, 148, 148))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCorreoTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbRolTrabajador, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtContraseñaTrabajador, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                        .addGap(105, 105, 105)
                        .addComponent(btnagregartrabajador))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(116, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Control de Empleados", jPanel1);

        btnReporteAtrasos.setText("Mostrar Reporte de Atrasos");
        btnReporteAtrasos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteAtrasosActionPerformed(evt);
            }
        });

        btnReporteSalida.setText("Mostrar Reporte de Salidas");
        btnReporteSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteSalidaActionPerformed(evt);
            }
        });

        ReporteInasistencia.setText("Mostrar Reporte de Inasistencia");
        ReporteInasistencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReporteInasistenciaActionPerformed(evt);
            }
        });

        jTableReportes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableReportes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnReporteAtrasos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReporteSalida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ReporteInasistencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(159, 159, 159))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(btnReporteAtrasos)
                        .addGap(33, 33, 33)
                        .addComponent(btnReporteSalida)
                        .addGap(61, 61, 61)
                        .addComponent(ReporteInasistencia))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Reportes", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnagregartrabajadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnagregartrabajadorActionPerformed
        agregarTrabajador();
    }//GEN-LAST:event_btnagregartrabajadorActionPerformed

    private void btnReporteAtrasosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteAtrasosActionPerformed
  String descripcion = "Descripción del reporte de atrasos";

    try {
        // Llama al método que genera y muestra el reporte, pasando solo el tipo de reporte y la descripción.
        generarYMostrarReportes("atraso", descripcion);  // Aquí debes pasar solo el tipo ("atraso").
    } catch (IllegalArgumentException e) {
        // Manejo de excepciones para tipos de reporte inválidos
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        // Manejo de excepciones generales
        JOptionPane.showMessageDialog(this, "Ocurrió un error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }


    }//GEN-LAST:event_btnReporteAtrasosActionPerformed

    private void btnReporteSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteSalidaActionPerformed
            
    }//GEN-LAST:event_btnReporteSalidaActionPerformed

    private void ReporteInasistenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReporteInasistenciaActionPerformed
 
    }//GEN-LAST:event_ReporteInasistenciaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ReporteInasistencia;
    private javax.swing.JButton btnReporteAtrasos;
    private javax.swing.JButton btnReporteSalida;
    private javax.swing.JButton btnagregartrabajador;
    private javax.swing.JComboBox<String> cbRolTrabajador;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableReportes;
    private javax.swing.JTable jTableTrabajador;
    private javax.swing.JTextField txtContraseñaTrabajador;
    private javax.swing.JTextField txtCorreoTrabajador;
    private javax.swing.JTextField txtNombreTrabajador;
    // End of variables declaration//GEN-END:variables
}
