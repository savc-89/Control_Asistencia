/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;

/**
 *
 * @author yorda
 */
public class Reportes {
    private int idReporte;
    private int idUsuario;
    private String tipoReporte;
    private Date fechaReporte;
    private String descripcion;

    public Reportes(int idReporte, int idUsuario, String tipoReporte, Date fechaReporte, String descripcion) {
        this.idReporte = idReporte;
        this.idUsuario = idUsuario;
        this.tipoReporte = tipoReporte;
        this.fechaReporte = fechaReporte;
        this.descripcion = descripcion;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
public Reportes() {
    }
// Asegúrate de que este constructor esté definido en la clase Reportes
public Reportes(int idUsuario, String tipoReporte, Date fechaReporte, String descripcion) {
    this.idUsuario = idUsuario;
    this.tipoReporte = tipoReporte;
    this.fechaReporte = fechaReporte;
    this.descripcion = descripcion;
}

}
