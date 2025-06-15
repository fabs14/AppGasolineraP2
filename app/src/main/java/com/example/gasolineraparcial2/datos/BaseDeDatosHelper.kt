package com.example.gasolineraparcial2.datos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDatosHelper(context: Context) :
    SQLiteOpenHelper(context, "GasolineraDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla sucursal
        db.execSQL(
            """CREATE TABLE sucursal (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL,
                bombas INTEGER NOT NULL
            );"""
        )

        // Crear tabla calculo
        db.execSQL(
            """CREATE TABLE calculo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idSucursal INTEGER NOT NULL,
                combustibleDisponible INTEGER NOT NULL,
                tiempoEsperaMinutos INTEGER NOT NULL,
                litrosRestantes INTEGER NOT NULL,
                fechaHora TEXT NOT NULL,
                FOREIGN KEY (idSucursal) REFERENCES sucursal(id)
            );"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si cambias la estructura en el futuro
        db.execSQL("DROP TABLE IF EXISTS calculo")
        db.execSQL("DROP TABLE IF EXISTS sucursal")
        onCreate(db)
    }
}
