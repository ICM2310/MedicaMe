package javeriana.edu.co.medicameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import javeriana.edu.co.medicameapp.databinding.ActivityPuntosDeDistribucionBinding

class PuntosDeDistribucion : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener
{
    private lateinit var mMap: GoogleMap
    private lateinit var bindingPuntosDeDistribucion: ActivityPuntosDeDistribucionBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        bindingPuntosDeDistribucion = ActivityPuntosDeDistribucionBinding.inflate(layoutInflater)
        setContentView(bindingPuntosDeDistribucion.root)

        inicializarElementos()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun inicializarElementos()
    {
        bindingPuntosDeDistribucion.bButton.setOnClickListener {
            Log.i("Puntos de Distribucion", "Boton de back accionado")
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Gestos de zoom
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Controles de zoom
        mMap.uiSettings.isZoomControlsEnabled = true

        // Punto de distribucion de prueba
        val puntoDeDistribuccionPepito = LatLng( 4.694951, -74.039239)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionPepito).title("Drogueria Pepito").snippet("Carrera 12 # 115 - 22"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntoDeDistribuccionPepito, 14f))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionUnicentro = LatLng( 4.701206, -74.040304)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionUnicentro).title("Drogueria Unicentro").snippet("Carrera 15 # 124 - 30"))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionSantaBarbara = LatLng( 4.693421, -74.033028)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionSantaBarbara).title("Drogueria Santa Barbara").snippet("Carrera 7 # 115 - 60"))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionJaveriana = LatLng( 4.627890, -74.064496)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionJaveriana).title("Hospital San Ignacio").snippet("Carrera 7 # 40B - 36"))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionAeropuerto = LatLng( 4.696788, -74.140506)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionAeropuerto).title("Synlab El Dorado").snippet("Avenida El Dorado # 103 - 9"))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionMendez = LatLng( 4.726437, -74.037911)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionMendez).title("Drogueria Mendez").snippet("Calle 146 # 13 - 09"))

        // Listener para los marcadores
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marcador: Marker): Boolean
    {
        Log.i("Puntos de Distribucion", "Marcador presionado: " + marcador.title)

        // Zoom al marcador
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marcador.position, 16f))

        // Texto del marcador seleccionado
        bindingPuntosDeDistribucion.puntoDeDistribucion.text = marcador.title
        bindingPuntosDeDistribucion.direccionPuntoDeDistribucion.text = marcador.snippet

        return true
    }
}