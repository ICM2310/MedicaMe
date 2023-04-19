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
import javeriana.edu.co.medicameapp.databinding.ActivityDomicilioExitosoMapBinding

class DomicilioExitosoMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDomicilioExitosoMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDomicilioExitosoMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeElements()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initializeElements()
    {
        binding.ButtonRetroceder.setOnClickListener {
            Log.i("Domicilio Exitoso", "Boton de back accionado")
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Punto de distribucion de prueba
        val puntoDeDistribucionPepito = LatLng( 4.694951, -74.039239)
        mMap.addMarker(MarkerOptions().position(puntoDeDistribucionPepito).title("Drogueria Pepito").snippet("Carrera 12 # 115 - 22"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntoDeDistribucionPepito, 13f))
    }
}