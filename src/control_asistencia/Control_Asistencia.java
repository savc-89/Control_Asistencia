/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package control_asistencia;

import controlador.UsuarioController;


public class Control_Asistencia {

    public static void main(String[] args) {
        // Crear un controlador de usuario
        UsuarioController usuarioController = new UsuarioController();

        // Verificar y crear el administrador si no existe
        usuarioController.crearAdminSiNoExiste();

        // Aquí puedes agregar el resto del código para iniciar tu aplicación
    }
}
