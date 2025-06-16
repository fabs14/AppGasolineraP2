package com.example.gasolineraparcial2.presentacion.template

import android.content.Context
import com.example.gasolineraparcial2.negocio.NSucursal

class ActualizarSucursal(
    context: Context,
    private val id: Int?,
    private val nombre: String,
    private val latitud: Double?,
    private val longitud: Double?,
    private val bombas: Int?
) : AccionSucursal(context) {

    override fun validar(): Boolean {
        return id != null && id > 0 &&
                nombre.isNotBlank() &&
                latitud != null && longitud != null &&
                bombas != null && bombas > 0
    }

    override fun procesar(): Boolean {
        val nSucursal = NSucursal(context)
        return nSucursal.actualizarSucursal(id!!, nombre, latitud!!, longitud!!, bombas!!)
    }

    override fun mensajeExito(): String = "Sucursal actualizada correctamente"
    override fun mensajeError(): String = "Error al actualizar la sucursal"
}
