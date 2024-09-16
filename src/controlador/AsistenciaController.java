/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import conexion.conectarbd;
import modelo.Asistencia;

import conexion.conectarbd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import modelo.Asistencia;

/**
 *
 * @author yorda
 */
public class AsistenciaController {
    

    public boolean registrarAsistencia(Asistencia asistencia) {
        String sql = "INSERT INTO asistencia (idUsuario, fecha, horaEntrada) VALUES (?, ?, ?)";

        try (Connection conn = conectarbd.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asistencia.getIdUsuario());
            stmt.setDate(2, new java.sql.Date(asistencia.getFecha().getTime()));
            stmt.setTime(3, asistencia.getHoraEntrada());

            int filasInsertadas = stmt.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }}
    
public boolean registrarSalida(Asistencia asistencia) {
    // SQL para actualizar la hora de salida en la fila correspondiente
    String sql = "UPDATE asistencia SET horaSalida = ? WHERE idUsuario = ? AND fecha = ?";

    try (Connection conn = conectarbd.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Establece los parámetros del PreparedStatement
        stmt.setTime(1, asistencia.getHoraSalida());
        stmt.setInt(2, asistencia.getIdUsuario());
        stmt.setDate(3, new java.sql.Date(asistencia.getFecha().getTime()));

        // Ejecuta la actualización
        int filasActualizadas = stmt.executeUpdate();
        return filasActualizadas > 0; // Retorna true si se actualizó al menos una fila

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


}

    
