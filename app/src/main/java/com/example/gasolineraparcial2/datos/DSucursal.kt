package com.example.gasolineraparcial2.datos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DSucursal(private val context: Context) {
    private val helper = BaseDeDatosHelper(context)

    fun insertar(nombre: String, latitud: Double, longitud: Double, bombas: Int): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("latitud", latitud)
            put("longitud", longitud)
            put("bombas", bombas)
        }
        val resultado = db.insert("sucursal", null, values)
        db.close()
        return resultado != -1L
    }

    fun obtenerTodas(): Cursor {
        val db = helper.readableDatabase
        return db.rawQuery("SELECT * FROM sucursal", null)
    }

    fun eliminar(id: Int): Boolean {
        val db = helper.writableDatabase
        val resultado = db.delete("sucursal", "id=?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    fun actualizar(id: Int, nombre: String, latitud: Double, longitud: Double, bombas: Int): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("latitud", latitud)
            put("longitud", longitud)
            put("bombas", bombas)
        }
        val resultado = db.update("sucursal", values, "id=?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }
}
