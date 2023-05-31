package javeriana.edu.co.medicameapp

import android.app.AlertDialog
import android.content.Intent

import android.net.Uri

import android.provider.Settings

import android.content.Context
import android.content.pm.PackageManager

import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult

import androidx.activity.result.IntentSenderRequest

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cursokotlin.routemapexample.RouteResponse
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import android.Manifest

import com.google.android.gms.maps.SupportMapFragment

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import javeriana.edu.co.medicameapp.databinding.ActivityDomiciliooBinding
import javeriana.edu.co.medicameapp.mapsapi.ApiService
import javeriana.edu.co.medicameapp.modelos.Order
import javeriana.edu.co.medicameapp.notifications.notificationsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import javeriana.edu.co.medicameapp.databinding.ActivityRealizarPedidoBinding

class RealizarPedidoActivity : AppCompatActivity(), OnMapReadyCallback {
    //Location
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var locationCallback: LocationCallback? = null
    var lastLocation: Location? = null
    private var apiKeyRoute: String? = null
    var requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            checkLocationSettings()
        } else {
            showLocationAlertDialogPermission()
        }
    }
    var requestLocationSettingsLauncher =
        registerForActivityResult<IntentSenderRequest, ActivityResult>(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                startLocationUpdates()
            } else {
                showLocationAlertDialog()
            }
        }
    val bogota = LatLng(4.60971, -74.08175)
    var mMap: GoogleMap? = null
    var currentLocation: Location? = null
    var userTracking: String? = null
    var userTrackingLocation: LatLng? = null
    var lastUserTrackingLocation: LatLng? = null
    var markerUserTracking: Marker? = null
    var markerCurrentLocation: Marker? = null
    private var deliveryMarker: Marker? = null
    var markerOptionsCurrentLocation: MarkerOptions? = null
    var poly: Polyline? = null
    var markerOptionsUserTracking: MarkerOptions? = null
    var distance: Float? = null
    var usersRef: DatabaseReference? = null
    var uid: String? = null
    var query: Query? = null
    var binding: ActivityRealizarPedidoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        apiKeyRoute = "5b3ce3597851110001cf62488378f0e9cb494245a468ef60281c6bd7"
        super.onCreate(savedInstanceState)
        binding = ActivityRealizarPedidoBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        val intent = intent
        uid = intent.getStringExtra("uid")
        Log.d("locationFromBD", uid!!)




        //Location
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()
        locationCallback = setupLocationCallback()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMap()




    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onRestart() {
        super.onRestart()
        startLocationUpdates()
    }

    private fun setupLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null && locationResult.lastLocation != null) {
                    val latLng = LatLng(
                        locationResult.lastLocation!!.latitude, locationResult.lastLocation!!
                            .longitude
                    )
                    lastLocation = currentLocation
                    currentLocation = locationResult.lastLocation
                    if (FirebaseAuth.getInstance().currentUser!!.uid != null) {
                        val userRef = FirebaseDatabase.getInstance().getReference(
                            PATH_USERS + FirebaseAuth.getInstance().currentUser!!
                                .uid
                        )
                        if (markerCurrentLocation != null) {
                            markerCurrentLocation!!.remove()
                        }
                        if (lastLocation !== currentLocation) {
                            markerOptionsCurrentLocation =
                                MarkerOptions().position(latLng).title("Current Location").icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                )
                            markerCurrentLocation = mMap!!.addMarker(markerOptionsCurrentLocation!!)
                            val customLatLng = CustomLatLng(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude
                            )
                            val mAuth = FirebaseAuth.getInstance()
                            val database = FirebaseDatabase.getInstance()
                            val ordersRef = database.getReference(
                                "deliveries/" + mAuth.currentUser!!
                                    .uid
                            )
                            ordersRef.child("location").setValue(customLatLng)
                        }
                        val customLatLng =
                            CustomLatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                        userRef.child("location").setValue(customLatLng)
                    }
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest!!,
                locationCallback!!, mainLooper
            )
        }
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            locationRequest!!
        )
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { startLocationUpdates() }
        task.addOnFailureListener { e ->
            if ((e as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                val resolvable = e as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                requestLocationSettingsLauncher.launch(isr)
            } else {
                Toast.makeText(this@RealizarPedidoActivity, "No GPS available", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLocationAlertDialog() {
        val builder = AlertDialog.Builder(this@RealizarPedidoActivity)
        builder.setMessage("In order for the app to function properly, you must turn on your location. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun showLocationAlertDialogPermission() {
        val builder = AlertDialog.Builder(this@RealizarPedidoActivity)
        builder.setMessage("The app needs to access your location to function properly. Would you like to allow access to your location?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun setupMap() {
        mMap!!.uiSettings.isZoomControlsEnabled = true

        // Obtener la ubicacion del usuario que se va a entregar el pedido
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userLocationRef = usersRef.child(uid!!).child("location")


        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.getValue(CustomLatLng::class.java)
                Log.i(
                    "locationFromDB",
                    location!!.getLatitude().toString() + "---" + location.getLongitude() + ""
                )
                userTrackingLocation = LatLng(location!!.getLatitude(), location.getLongitude())
                Log.i("locationFromDB", userTrackingLocation!!.latitude.toString())
                Log.i("locationFromDB", userTrackingLocation!!.longitude.toString())
                markerOptionsUserTracking = MarkerOptions().position(userTrackingLocation!!).title("User Tracking").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
                markerUserTracking = mMap!!.addMarker(markerOptionsUserTracking!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("locationFromDB", "getUser:onCancelled", databaseError.toException())
            }
        }

        userLocationRef.addValueEventListener(listener)
        //Log.i("locationFromDB", userTrackingLocation!!.latitude.toString())



        /*
        markerOptionsUserTracking = MarkerOptions().position(userTrackingLocation!!).title("User Tracking").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
        markerUserTracking = mMap!!.addMarker(markerOptionsUserTracking!!)
         */




    }

    private fun positionDefault() {
        val bogota = LatLng(4.60971, -74.08175)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(bogota))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f))
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun mostrarRuta(puntoDeDistribucionPepito: LatLng, markerPosition: LatLng){
        val start =
            "${puntoDeDistribucionPepito.longitude},${puntoDeDistribucionPepito.latitude}"
        val end = "${markerPosition.longitude},${markerPosition.latitude}"
        CoroutineScope(Dispatchers.IO).launch {
            val call = apiKeyRoute?.let {
                getRetrofit().create(ApiService::class.java)
                    .getRoute(it, start, end)
            }
            if (call != null) {
                if (call.isSuccessful) {
                    drawRoute(call.body())
                } else {
                    Log.i("aris", "KO")
                }
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {
        val polyLineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }

        runOnUiThread {
            poly?.remove() // Remover la ruta existente, si la hay
            poly = mMap?.addPolyline(polyLineOptions)
            val icon = (bitmapDescriptorFromVector(this,R.drawable.deliverymarker))
            markerCurrentLocation?.remove()
            // Agregar Marcador al mapa
            var ubicacionDelivery = currentLocation?.let { LatLng(it.latitude, it.longitude) }
            markerCurrentLocation = ubicacionDelivery?.let { MarkerOptions().position(it).icon(icon).title("Mi Ubicación Actual") }
                ?.let { mMap?.addMarker(it) }

            // Hacer zoom en la ubicación buscada
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(bogota, 10f))
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val PATH_USERS = "users/"
    }
}