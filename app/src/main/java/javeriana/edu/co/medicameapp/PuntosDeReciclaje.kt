package javeriana.edu.co.medicameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javeriana.edu.co.medicameapp.databinding.ActivityPuntosDeReciclajeBinding

class PuntosDeReciclaje : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener
{
    private lateinit var mMap: GoogleMap
    private lateinit var bindingPuntosDeReciclaje: ActivityPuntosDeReciclajeBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        bindingPuntosDeReciclaje = ActivityPuntosDeReciclajeBinding.inflate(layoutInflater)
        setContentView(bindingPuntosDeReciclaje.root)

        inicializarElementos()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun inicializarElementos()
    {
        bindingPuntosDeReciclaje.bButtonReciclaje.setOnClickListener {
            Log.i("Puntos de Reciclaje", "Boton de back accionado")
            finish()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Gestos de zoom
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Controles de zoom
        mMap.uiSettings.isZoomControlsEnabled = true

        // Punto de reciclaje de prueba
        val puntoDeReciclajePepito = LatLng( 4.694951, -74.039239)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajePepito).title("Drogueria Pepito").snippet("Carrera 12 # 115 - 22"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntoDeReciclajePepito, 14f))

        // Punto de reciclaje de prueba
        val puntoDeReciclajeUnicentro = LatLng( 4.701206, -74.040304)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajeUnicentro).title("Drogueria Unicentro").snippet("Carrera 15 # 124 - 30"))

        // Punto de reciclaje de prueba
        val puntoDeReciclajeSantaBarbara = LatLng( 4.693421, -74.033028)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajeSantaBarbara).title("Drogueria Santa Barbara").snippet("Carrera 7 # 115 - 60"))

        // Punto de reciclaje de prueba
        val puntoDeReciclajeJaveriana = LatLng( 4.627890, -74.064496)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajeJaveriana).title("Hospital San Ignacio").snippet("Carrera 7 # 40B - 36"))

        // Punto de reciclaje de prueba
        val puntoDeReciclajeAeropuerto = LatLng( 4.696788, -74.140506)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajeAeropuerto).title("Synlab El Dorado").snippet("Avenida El Dorado # 103 - 9"))

        // Punto de reciclaje de prueba
        val puntoDeReciclajeMendez = LatLng( 4.726437, -74.037911)
        mMap.addMarker(MarkerOptions().position(puntoDeReciclajeMendez).title("Drogueria Mendez").snippet("Calle 146 # 13 - 09"))

        // Listener para los marcadores
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marcador: Marker): Boolean
    {
        Log.i("Puntos de reciclaje", "Marcador presionado: " + marcador.title)

        // Zoom al marcador
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marcador.position, 16f))

        // Texto del marcador seleccionado
        bindingPuntosDeReciclaje.puntoDeReciclaje.text = marcador.title
        bindingPuntosDeReciclaje.direccionPuntoDeReciclaje.text = marcador.snippet

        return true
    }
}