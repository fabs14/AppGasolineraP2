package com.example.gasolineraparcial2.negocio

import android.content.Context
import android.database.Cursor
import com.example.gasolineraparcial2.datos.DSucursal

class NSucursal(context: Context) {
    private val dsucursal = DSucursal(context)

    fun crearSucursal(nombre: String, latitud: Double, longitud: Double, bombas: Int): Boolean {
        // Podrías validar si nombre está vacío, por ejemplo
        return dsucursal.insertar(nombre, latitud, longitud, bombas)
    }

    fun obtenerSucursales(): Cursor {
        return dsucursal.obtenerTodas()
    }

    fun eliminarSucursal(id: Int): Boolean {
        return dsucursal.eliminar(id)
    }

    fun actualizarSucursal(id: Int, nombre: String, latitud: Double, longitud: Double, bombas: Int): Boolean {
        return dsucursal.actualizar(id, nombre, latitud, longitud, bombas)
    }
}
