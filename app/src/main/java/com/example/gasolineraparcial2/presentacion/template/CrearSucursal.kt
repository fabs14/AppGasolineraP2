package com.example.gasolineraparcial2.presentacion.template

import android.content.Context
import com.example.gasolineraparcial2.negocio.NSucursal

class CrearSucursal(
    context: Context,
    private val nombre: String,
    private val latitud: Double?,
    private val longitud: Double?,
    private val bombas: Int?
) : AccionSucursal(context) {

    override fun validar(): Boolean {
        return nombre.isNotBlank() && latitud != null && longitud != null && bombas != null
    }

    override fun procesar(): Boolean {
        val nSucursal = NSucursal(context)
        return nSucursal.crearSucursal(nombre, latitud!!, longitud!!, bombas!!)
    }

    override fun mensajeExito(): String = "Sucursal creada correctamente"
    override fun mensajeError(): String = "Error al crear sucursal"
}
