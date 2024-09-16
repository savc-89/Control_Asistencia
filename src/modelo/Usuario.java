/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author yorda
 */

public class Usuario {
    private int idUsuario;
    private String correo;
    private String password;
    private String nombre;
    private String rol;

    public Usuario(int idUsuario, String correo, String password, String nombre, String rol) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
  
    // Constructor vacío
    public Usuario() {
    }
    public Usuario(String correo, String nombre, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.rol = rol;
    }
    public Usuario(int idUsuario, String correo, String nombre, String rol) {
    this.idUsuario = idUsuario;
    this.correo = correo;
    this.nombre = nombre;
    this.rol = rol;
}

}

   