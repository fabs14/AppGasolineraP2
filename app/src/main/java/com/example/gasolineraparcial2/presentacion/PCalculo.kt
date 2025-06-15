package com.example.gasolineraparcial2.presentacion

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gasolineraparcial2.R
import com.example.gasolineraparcial2.negocio.NSucursal
import com.example.gasolineraparcial2.presentacion.handlers.* //import a todos los handlers
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class PCalculo : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var edtLitros: EditText
    private lateinit var btnCalcular: Button
    private lateinit var spinnerSucursal: Spinner
    private lateinit var txtResultado: TextView
    private lateinit var nSucursal: NSucursal

    private var idSucursal: Int = -1
    private var bombas: Int = 0
    private var puntoSucursal: LatLng? = null
    private var distanciaMetros: Double = 0.0

    private val puntosFila = mutableListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_calculo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtLitros = view.findViewById(R.id.edtLitros)
        btnCalcular = view.findViewById(R.id.btnCalcular)
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal)
        txtResultado = view.findViewById(R.id.txtResultado)

        nSucursal = NSucursal(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        cargarSpinnerSucursales()

        btnCalcular.setOnClickListener {
            val litros = edtLitros.text.toString().toIntOrNull()

            val request = CalculoRequest(
                context = requireContext(),
                sucursalId = idSucursal,
                litrosDisponibles = litros,
                bombas = bombas,
                puntosFila = puntosFila.toList(),
                txtResultado = txtResultado
            )

            val cadena = ValidarSucursal()
                .setNext(ValidarLitros())
                .setNext(ValidarDibujo())
                .setNext(EjecutarCalculo())

            if (!cadena.handle(request)) {
                Toast.makeText(requireContext(), "Error: Verifique los datos ingresados.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarSpinnerSucursales() {
        val listaNombres = mutableListOf<String>()
        val listaDatos = mutableListOf<Triple<Int, LatLng, Int>>()

        val cursor = nSucursal.obtenerSucursales()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
            val bombas = cursor.getInt(cursor.getColumnIndexOrThrow("bombas"))

            listaNombres.add(nombre)
            listaDatos.add(Triple(id, LatLng(lat, lon), bombas))
        }
        cursor.close()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaNombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSucursal.adapter = adapter

        spinnerSucursal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val (idSel, latLng, bombasSel) = listaDatos[position]
                idSucursal = idSel
                puntoSucursal = latLng
                bombas = bombasSel

                map.clear()
                puntosFila.clear()
                map.addMarker(MarkerOptions().position(latLng).title("Sucursal"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        val centro = LatLng(-17.7835, -63.1821)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 13f))

        map.setOnMapClickListener { latLng ->
            if (puntosFila.isEmpty()) puntoSucursal?.let { puntosFila.add(it) }
            puntosFila.add(latLng)

            map.clear()

            puntoSucursal?.let {
                map.addMarker(MarkerOptions().position(it).title("Sucursal"))
            }

            if (puntosFila.isNotEmpty()) {
                map.addPolyline(PolylineOptions().addAll(puntosFila))
            }
        }
    }

    private fun calcularDistanciaTotal(puntos: List<LatLng>): Double {
        var distancia = 0.0
        for (i in 0 until puntos.size - 1) {
            val result = FloatArray(1)
            Location.distanceBetween(
                puntos[i].latitude, puntos[i].longitude,
                puntos[i + 1].latitude, puntos[i + 1].longitude,
                result
            )
            distancia += result[0]
        }
        return distancia
    }


}
