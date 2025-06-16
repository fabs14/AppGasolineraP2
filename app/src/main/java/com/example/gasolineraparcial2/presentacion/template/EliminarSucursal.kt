package com.example.gasolineraparcial2.presentacion.template

import android.content.Context
import com.example.gasolineraparcial2.negocio.NSucursal

class EliminarSucursal(
    context: Context,
    private val id: Int?
) : AccionSucursal(context) {

    override fun validar(): Boolean {
        return id != null && id > 0
    }

    override fun procesar(): Boolean {
        val nSucursal = NSucursal(context)
        return nSucursal.eliminarSucursal(id!!)
    }

    override fun mensajeExito(): String = "Sucursal eliminada correctamente"
    override fun mensajeError(): String = "Error al eliminar la sucursal"
}
