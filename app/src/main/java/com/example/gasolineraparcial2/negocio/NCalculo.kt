package com.example.gasolineraparcial2.negocio

import android.content.Context
import com.example.gasolineraparcial2.datos.DCalculo
import java.text.SimpleDateFormat
import java.util.*

class NCalculo private constructor() {

    companion object {
        private class Holder {
            companion object {
                val INSTANCE = NCalculo()
            }
        }

        fun getInstance(): NCalculo {
            return Holder.INSTANCE
        }
    }

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

        DCalculo(context).insertarCalculo(
            idSucursal,
            resultado.combustibleDisponible,
            resultado.tiempoEsperaMinutos,
            resultado.litrosRestantes,
            resultado.fechaHora
        )

        return resultado
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formato.format(Date())
    }

    // CLASE INTERNA REGULAR (no data class)
    class ResultadoCalculo(
        val combustibleDisponible: Boolean,
        val tiempoEsperaMinutos: Int,
        val litrosRestantes: Int,
        val fechaHora: String
    )
}
