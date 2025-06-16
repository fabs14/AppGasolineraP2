package com.example.gasolineraparcial2.datos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DCalculo(context: Context) {
    private val helper = BaseDeDatosHelper(context)

    fun insertarCalculo(
        idSucursal: Int,
        combustibleDisponible: Boolean,
        tiempoEsperaMinutos: Int,
        litrosRestantes: Int,
        fechaHora: String
    ): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("idSucursal", idSucursal)
            put("combustibleDisponible", if (combustibleDisponible) 1 else 0)
            put("tiempoEsperaMinutos", tiempoEsperaMinutos)
            put("litrosRestantes", litrosRestantes)
            put("fechaHora", fechaHora)
        }
        val result = db.insert("calculo", null, values)
        db.close()
        return result != -1L
    }

    fun obtenerUltimoCalculo(): Cursor {
        val db = helper.readableDatabase
        return db.rawQuery("SELECT * FROM calculo ORDER BY id DESC LIMIT 1", null)
    }
}
