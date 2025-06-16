package com.example.gasolineraparcial2.negocio

import android.content.Context
import com.example.gasolineraparcial2.datos.DCalculo
import java.text.SimpleDateFormat
import java.util.*

class NCalculo {

    private val largoPromedioVehiculo = 4.5
    private val tiempoPromedioPorVehiculo = 4
    private val litrosPromedioPorVehiculo = 40

    fun realizarCalculo(
        context: Context,
        idSucursal: Int,
        distanciaMetros: Double,
        litrosDisponibles: Int,
        cantidadBombas: Int
    ): ResultadoCalculo {
        val cantidadVehiculos = (distanciaMetros / largoPromedioVehiculo).toInt()
        val tiempoEstimado = if (cantidadBombas > 0)
            (cantidadVehiculos / cantidadBombas) * tiempoPromedioPorVehiculo
        else 0

        val litrosNecesarios = cantidadVehiculos * litrosPromedioPorVehiculo
        val disponible = litrosDisponibles >= litrosNecesarios
        val litrosRestantes = if (disponible) litrosDisponibles - litrosNecesarios else 0
        val tiempoFinal = if (disponible) tiempoEstimado else 0
        val fechaHora = obtenerFechaHoraActual()

        val resultado = ResultadoCalculo(
            disponible,
            tiempoFinal,
            litrosRestantes,
            fechaHora
        )

        val dCalculo = DCalculo(context)
        dCalculo.insertarCalculo(
            idSucursal,
            resultado.combustibleDisponible,
            resultado.tiempoEsperaMinutos,
            resultado.litrosRestantes,
            resultado.fechaHora
        )

        return resultado
    }

    fun obtenerUltimoResultadoFormateado(context: Context): String {
        val dCalculo = DCalculo(context)
        val cursor = dCalculo.obtenerUltimoCalculo()

        var mensaje = "❌ No hay resultados aún"
        if (cursor.moveToFirst()) {
            val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("combustibleDisponible")) == 1
            val tiempo = cursor.getInt(cursor.getColumnIndexOrThrow("tiempoEsperaMinutos"))
            val litros = cursor.getInt(cursor.getColumnIndexOrThrow("litrosRestantes"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fechaHora"))

            mensaje = """
                ✅ Último cálculo:
                Combustible disponible: ${if (disponible) "Sí" else "No"}
                Tiempo estimado: $tiempo min
                Litros restantes: $litros
                Fecha: $fecha
            """.trimIndent()
        }
        cursor.close()
        return mensaje
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formato.format(Date())
    }

    class ResultadoCalculo(
        val combustibleDisponible: Boolean,
        val tiempoEsperaMinutos: Int,
        val litrosRestantes: Int,
        val fechaHora: String
    )
}
