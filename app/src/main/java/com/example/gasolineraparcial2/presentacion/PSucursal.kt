package com.example.gasolineraparcial2.presentacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gasolineraparcial2.R
import com.example.gasolineraparcial2.negocio.NSucursal
import com.example.gasolineraparcial2.presentacion.template.AccionSucursal
import com.example.gasolineraparcial2.presentacion.template.ActualizarSucursal
import com.example.gasolineraparcial2.presentacion.template.CrearSucursal
import com.example.gasolineraparcial2.presentacion.template.EliminarSucursal
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PSucursal : Fragment(), OnMapReadyCallback {

    private lateinit var nSucursal: NSucursal
    private lateinit var listaSucursales: ListView
    private lateinit var edtNombre: EditText
    private lateinit var edtLatitud: EditText
    private lateinit var edtLongitud: EditText
    private lateinit var edtBombas: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button

    private var idEditando: Int? = null
    private var mapa: GoogleMap? = null
    private var marcadorSucursal: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sucursal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nSucursal = NSucursal(requireContext())

        // Inicializar vistas
        edtNombre = view.findViewById(R.id.edtNombre)
        edtLatitud = view.findViewById(R.id.edtLatitud)
        edtLongitud = view.findViewById(R.id.edtLongitud)
        edtBombas = view.findViewById(R.id.edtBombas)
        btnCrear = view.findViewById(R.id.btnCrearSucursal)
        btnActualizar = view.findViewById(R.id.btnActualizarSucursal)
        btnEliminar = view.findViewById(R.id.btnEliminarSucursal)
        listaSucursales = view.findViewById(R.id.listaSucursales)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSucursal) as SupportMapFragment
        mapFragment.getMapAsync(this)

        cargarSucursales()

        btnActualizar.isEnabled = false
        btnEliminar.isEnabled = false
        btnCrear.isEnabled = true

        listaSucursales.setOnItemClickListener { _, _, position, _ ->
            val datos = listaSucursales.getItemAtPosition(position) as String
            val partes = datos.split("|")
            if (partes.size >= 5) {
                idEditando = partes[0].trim().toIntOrNull()
                edtNombre.setText(partes[1].trim())
                edtLatitud.setText(partes[2].trim())
                edtLongitud.setText(partes[3].trim())
                edtBombas.setText(partes[4].trim())
                marcadorSucursal = LatLng(partes[2].toDouble(), partes[3].toDouble())
                mapa?.clear()
                marcadorSucursal?.let { pos ->
                    mapa?.addMarker(MarkerOptions().position(pos).title("Sucursal"))
                    mapa?.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                }
                btnCrear.isEnabled = false
                btnActualizar.isEnabled = true
                btnEliminar.isEnabled = true
            }
        }

        btnCrear.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val lat = edtLatitud.text.toString().toDoubleOrNull()
            val lon = edtLongitud.text.toString().toDoubleOrNull()
            val bmb = edtBombas.text.toString().toIntOrNull()

            val accion: AccionSucursal = CrearSucursal(requireContext(), nombre, lat, lon, bmb)
            if (accion.ejecutar()) {
                limpiarCampos()
                cargarSucursales()
            }
        }

        btnActualizar.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val lat = edtLatitud.text.toString().toDoubleOrNull()
            val lon = edtLongitud.text.toString().toDoubleOrNull()
            val bmb = edtBombas.text.toString().toIntOrNull()

            val accion: AccionSucursal = ActualizarSucursal(requireContext(), idEditando, nombre, lat, lon, bmb)
            if (accion.ejecutar()) {
                limpiarCampos()
                cargarSucursales()
            }
        }

        btnEliminar.setOnClickListener {
            val accion: AccionSucursal = EliminarSucursal(requireContext(), idEditando)
            if (accion.ejecutar()) {
                limpiarCampos()
                cargarSucursales()
            }
        }
    }

    private fun cargarSucursales() {
        val cursor = nSucursal.obtenerSucursales()
        val lista = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"))
            val lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
            val bombas = cursor.getInt(cursor.getColumnIndexOrThrow("bombas"))
            lista.add("$id | $nombre | $lat | $lon | $bombas")
        }
        cursor.close()
        listaSucursales.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
    }

    private fun limpiarCampos() {
        edtNombre.setText("")
        edtLatitud.setText("")
        edtLongitud.setText("")
        edtBombas.setText("")
        idEditando = null
        marcadorSucursal = null
        mapa?.clear()
        btnActualizar.isEnabled = false
        btnEliminar.isEnabled = false
        btnCrear.isEnabled = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        mapa?.uiSettings?.isZoomControlsEnabled = true
        val centro = LatLng(-17.7835, -63.1821)
        mapa?.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 13f))

        mapa?.setOnMapClickListener { latLng ->
            marcadorSucursal = latLng
            edtLatitud.setText(latLng.latitude.toString())
            edtLongitud.setText(latLng.longitude.toString())
            mapa?.clear()
            mapa?.addMarker(MarkerOptions().position(latLng).title("Sucursal nueva"))
        }
    }
}