package javeriana.edu.co.medicameapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javeriana.edu.co.medicameapp.databinding.ActivityPuntosDeDistribucionBinding

class PuntosDeDistribucion : AppCompatActivity(), OnMapReadyCallback
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

        // Punto de distribucion de prueba
        val puntoDeDistribuccionPepito = LatLng( 4.694951, -74.039239)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionPepito).title("Drogueria Pepito").snippet("Carrera 12 # 115 - 22"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntoDeDistribuccionPepito, 15f))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionUnicentro = LatLng( 4.701206, -74.040304)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionUnicentro).title("Drogueria Unicentro").snippet("Carrera 15 # 124 - 30"))

        // Punto de distribucion de prueba
        val puntoDeDistribuccionSantaBarbara = LatLng( 4.693421, -74.033028)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribuccionSantaBarbara).title("Drogueria Santa Barbara").snippet("Carrera 7 # 115 - 60"))
    }
}