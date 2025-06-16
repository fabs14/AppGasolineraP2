package com.example.gasolineraparcial2.presentacion

//import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gasolineraparcial2.R
import com.example.gasolineraparcial2.negocio.NCalculo
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
    private lateinit var nCalculo: NCalculo

    private var idSucursal: Int = -1
    private var bombas: Int = 0
    private var puntoSucursal: LatLng? = null

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

        nSucursal = NSucursal(requireContext())  //inicializando Sucursal
        nCalculo = NCalculo.getInstance()   //inicializando nCalculo

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
                puntosFila = puntosFila.toList()
            )

            val validarSucursal = ValidarSucursal()
            val validarLitros = ValidarLitros()
            val validarDibujo = ValidarDibujo()
            val ejecutarCalculo = EjecutarCalculo()

            validarSucursal.setNext(validarLitros)
                .setNext(validarDibujo)
                .setNext(ejecutarCalculo)

            if (validarSucursal.handle(request)) {
                val mensaje = nCalculo.obtenerUltimoResultadoFormateado(requireContext())
                mostrarResultado(mensaje)
            } else {
                Toast.makeText(requireContext(), "Error: Verifique los datos ingresados.", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun cargarSpinnerSucursales() {
        val listaNombres = mutableListOf<String>()
        val listaDatos = mutableListOf<Triple<Int, LatLng, Int>>()

        // 👇 Agregamos un ítem dummy como primera opción
        listaNombres.add("Seleccione una sucursal")
        listaDatos.add(Triple(-1, LatLng(0.0, 0.0), 0))

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
                //  Solo mostramos en el mapa si se seleccionó una sucursal válida
                if (idSel > 0) {
                    map.addMarker(MarkerOptions().position(latLng).title("Sucursal"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 👇 Seleccionamos el ítem dummy por defecto
        spinnerSucursal.setSelection(0)
    }


    private fun mostrarResultado(mensaje: String) {
        txtResultado.text = mensaje
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
}
