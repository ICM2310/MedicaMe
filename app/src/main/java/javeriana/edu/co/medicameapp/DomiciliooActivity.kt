
package javeriana.edu.co.medicameapp


import android.Manifest
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.database.ValueEventListener
import javeriana.edu.co.medicameapp.databinding.ActivityDomiciliooBinding
import javeriana.edu.co.medicameapp.mapsapi.ApiService
import javeriana.edu.co.medicameapp.modelos.Order
import javeriana.edu.co.medicameapp.notifications.notificationsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class DomiciliooActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMapLongClickListener{

    val userListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // No se necesita hacer nada aquí, ya que estamos interesados en los cambios de estado y no en los nuevos elementos agregados
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            val estado = snapshot.child("estado").getValue(String::class.java)
            if (estado == "Entregado"){
                binding.textEstadoOrden.text = "Estado: Entregado"
            }else if (estado == "Asignado" || snapshot.key == FirebaseAuth.getInstance().currentUser?.uid) {
                binding.textEstadoOrden.text = "Estado: Asignado"
                Toast.makeText(this@DomiciliooActivity, "El estado del pedido ha cambiado", Toast.LENGTH_SHORT).show()
                val usuarioRepartidor = snapshot.child("usuarioRepartidor").getValue(String::class.java)
                uidDelivery = usuarioRepartidor
                Log.i("Puntos de Distribucion", "El usuario repartidor es: $usuarioRepartidor")
                obtenerUbicacionDelivery()

                //mostrarRuta(ubicacionDelivery!!, markerPosition)
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            // No se necesita hacer nada aquí
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            // No se necesita hacer nada aquí
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Manejar cualquier error que ocurra durante la consulta
        }
    }


    val deliveryLocationListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (dataSnapshot in snapshot.children) {
                if (dataSnapshot.key == uidDelivery){
                    var i = 0
                    var latitude = 0.0
                    var longitude = 0.0
                    for (d in dataSnapshot.child("location").children){
                        i += 1
                        if (i == 1){
                            latitude = d.value.toString().toDouble()
                        } else if (i == 2){
                            longitude = d.value.toString().toDouble()
                        }
                    }
                    ubicacionDelivery = LatLng(latitude, longitude)

                    // Distancia entre el usuario actual y el repartidor
                    val distance = FloatArray(1)
                    Location.distanceBetween(latitude, longitude, currentLocation.latitude, currentLocation.longitude, distance);

                    if (distance[0] < 1000) {

                        // Crear notificacion.
                        notificationsHelper.showRepartidorCercanoNotification(
                            baseContext,
                            "Repartidor cercano",
                            "El repartidor está por llegar con tu pedido"
                        )

                    }

                    removeRoute()
                    mostrarRuta(ubicacionDelivery!!, markerPosition)
                    Log.i("Puntos de Distribucion", "La ubicacion del delivery es: $ubicacionDelivery")

                }
                }
            }

        override fun onCancelled(databaseError: DatabaseError) {
            // Manejar cualquier error que ocurra durante la consulta
        }
    }




    private var settingsOK: Boolean = false
    // Cargar el archivo local.properties
// Cargar el archivo local.properties

    var poly: Polyline? = null

    private lateinit var mBdRef : DatabaseReference

    private var orderUid : String? = null


    val puntoDeDistribucionPepito = LatLng( 4.694951, -74.039239)

    private var ubicacionDelivery : LatLng? = null
    private var uidDelivery : String? = "null"

    private lateinit var mLocationRequest:LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = callbackAction()
    var getLocationSettings = registerForActivityResult<IntentSenderRequest, ActivityResult>(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startLocationUpdates()
        }
    }
    var locationPermission = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            // Mostrar Toast cuando se conceden los permisos
            Toast.makeText(
                this@DomiciliooActivity,
                "Se han concedido los permisos de ubicación",
                Toast.LENGTH_SHORT
            ).show()
            checkLocationSettings()
        } else {
            Toast.makeText(
                this@DomiciliooActivity,
                "No hay permiso de localización",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var deliveryMarker: Marker? = null


    private lateinit var currentLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDomiciliooBinding
    private lateinit var etDestino: EditText
    val lowerLeftLatitude = 1.396967
    val lowerLeftLongitude = -78.903968
    val upperRightLatitude = 11.983639
    val upperRigthLongitude = -71.869905
    private lateinit var mGeocoder: Geocoder
    private lateinit var markerPosition : LatLng
    val bogota = LatLng(4.60971, -74.08175)

    private lateinit var apiKeyRoute : String



    override fun onCreate(savedInstanceState: Bundle?)
    {
        apiKeyRoute = "5b3ce3597851110001cf62488378f0e9cb494245a468ef60281c6bd7"
        super.onCreate(savedInstanceState)
        mGeocoder = Geocoder(baseContext)
        binding = ActivityDomiciliooBinding.inflate(layoutInflater)
        setContentView(binding.root)
        poly?.remove()

        locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = createLocationRequest()
        mLocationCallback = callbackAction()

        mBdRef = FirebaseDatabase.getInstance().getReference()


        markerPosition = LatLng(0.0,0.0)

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("orders")
        val query = reference.orderByChild("usuarioSoliciante").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
        query.addChildEventListener(userListener)


        uidDelivery = "null"

        val deliveryLocationRef = FirebaseDatabase.getInstance().getReference("deliveries")

        deliveryLocationRef.addValueEventListener(deliveryLocationListener)


        inicializarElementos()


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)





    }


    override fun onResume() {
        super.onResume()
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("orders")
        val query = reference.orderByChild("usuarioSoliciante").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
        query.addChildEventListener(userListener)


        uidDelivery = "null"

        val deliveryLocationRef = FirebaseDatabase.getInstance().getReference("deliveries")

        deliveryLocationRef.addValueEventListener(deliveryLocationListener)
    }

    private fun callbackAction(): LocationCallback? {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLocation = locationResult.lastLocation!!
            }
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
        mMap.setOnMapLongClickListener(this)
        // Desactivar marcadores
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false

        inizializeMapLocation()
    }

    private fun inizializeMapLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Se tienen permisos de ubicación, mostrar marcador en ubicación actual
            Toast.makeText(this, "Ubicacion Actual", Toast.LENGTH_SHORT).show()
            mMap.isMyLocationEnabled = true

            // Obtener la ubicación actual
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        currentLocation = location
                        // Verificar si se obtuvo la ubicación actual correctamente
                        requestWeatherForecast(location.latitude, location.longitude)
                        marketCurrentLocation(location)
                    }
                }
        } else {
            // No se tienen permisos de ubicación, posicionar el mapa en Bogotá con menos zoom
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 10f))
        }
    }
        private fun marketCurrentLocation(location: Location?) {
        if (location != null) {
            markerPosition = LatLng(location.latitude, location.longitude)
            if (mMap != null)
                // Limpiar marcadores anteriores
                mMap.clear()
            mMap.addMarker(
                MarkerOptions().position(markerPosition).title("Mi Ubicación Actual")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15f))
        }

    }


    private fun inicializarElementos()
    {
        binding.bButtonDomicilio.setOnClickListener {
            Log.i("Puntos de Distribucion", "Boton de back accionado")
            finish()
        }

        etDestino = binding.destino
        binding.buscarDestinoButton.setOnClickListener {
            marcadorEnBusqueda()
        }
        binding.destino.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                marcadorEnBusqueda()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.destino.windowToken, 0)
                return@setOnEditorActionListener true // Retornar true si el evento ha sido manejado
            }
            false // Retornar false si el evento no ha sido manejado
        }



        binding.buttonSolicitarDomicilio.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance().currentUser?.uid
            val uid = mAuth
            val order = Order()
            order.usuarioSoliciante = uid
            order.usuarioRepartidor = "null"
            order.ubicacion = markerPosition
            order.estado = "Pendiente"
            binding.textEstadoOrden.text = "Estado: Pendiente"


            val orderRef = mBdRef.child("orders").push()
            orderUid = orderRef.key

            orderRef.setValue(order).addOnSuccessListener {


                // Create notification
                notificationsHelper.showDomicilioNotification(
                    baseContext,
                    "Su solicitud de domicilio ha sido recibida",
                    "Esperamos asignarle un repartidor pronto"
                )


                Toast.makeText(this, "Solicitud de domicilio enviada $orderUid", Toast.LENGTH_SHORT).show()

                // Aquí puedes utilizar el orderUid, que es el UID del objeto recién agregado
                println("El UID del objeto recién agregado es: $orderUid")
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar la solicitud de domicilio", Toast.LENGTH_SHORT).show()
            }


        }


        binding.buttonNuevoDomicilio.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance().currentUser?.uid
            val uid = mAuth

            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("orders")

            val query = reference.orderByChild("usuarioSoliciante").equalTo(uid)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val estado = snapshot.child("estado").getValue(String::class.java)
                        if (estado == "Entregado") {
                            binding.textEstadoOrden.text = "Estado: "
                            removeRoute()
                            snapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Log.i("Puntos de Distribucion", "Registro eliminado correctamente")
                                }
                                .addOnFailureListener { error ->
                                    Log.e("Puntos de Distribucion", "Error al eliminar el registro", error)
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Maneja cualquier error que ocurra durante la consulta
                }
            })


        }

        binding.currentlocButton.setOnClickListener {
            inizializeMapLocation()
        }



    }

    fun obtenerUbicacionDelivery() {
        if (uidDelivery != null || uidDelivery != "null") {
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("deliveries").child(uidDelivery!!)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ubicacionSnapshot = dataSnapshot.child("location")
                    val latitude = ubicacionSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = ubicacionSnapshot.child("longitude").getValue(Double::class.java)

                    if (latitude != null && longitude != null) {
                        ubicacionDelivery = LatLng(latitude, longitude)
                        Log.i("Puntos de Distribucion", "Ubicación del repartidor: $ubicacionDelivery")
                        mostrarRuta(ubicacionDelivery!!, markerPosition)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar cualquier error que ocurra durante la consulta
                }
            })
        }
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
                val call = getRetrofit().create(ApiService::class.java)
                    .getRoute(apiKeyRoute, start, end)
                if (call.isSuccessful) {
                    drawRoute(call.body())
                } else {
                    Log.i("aris", "KO")
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
            poly = mMap.addPolyline(polyLineOptions)
            val icon = (bitmapDescriptorFromVector(this,R.drawable.deliverymarker))
            deliveryMarker?.remove()
            // Agregar Marcador al mapa
            deliveryMarker = ubicacionDelivery?.let { MarkerOptions().position(it).icon(icon).title("Mi Ubicación Actual") }
                ?.let { mMap.addMarker(it) }

            // Hacer zoom en la ubicación buscada
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bogota, 10f))
        }
    }

    private fun removeRoute() {

        if (poly != null) {
            runOnUiThread {
                poly?.remove() // Remover la ruta existente, si la hay
            }

        }
        deliveryMarker?.remove()

        Log.i("Puntos de Distribucion", "Eliminando ruta")
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


    private fun marcadorEnBusqueda(){
        val destino = etDestino.text.toString()
        val addressString: String = destino
        if (!addressString.isEmpty()) {
            try {

                val addresses = mGeocoder.getFromLocationName(
                    addressString,
                    2,
                    lowerLeftLatitude,
                    lowerLeftLongitude,
                    upperRightLatitude,
                    upperRigthLongitude
                )

                if (addresses != null && !addresses.isEmpty()) {
                    val addressResult = addresses[0]
                    markerPosition = LatLng(addressResult.latitude, addressResult.longitude)
                    if (mMap != null) {
                        // Limpiar marcadores anteriores
                        mMap.clear()

                        // Agregar Marcador al mapa
                        mMap.addMarker(MarkerOptions().position(markerPosition).title(destino))

                        // Hacer zoom en la ubicación buscada
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15f))
                    }
                } else {
                    Toast.makeText(
                        this@DomiciliooActivity,
                        "Dirección no encontrada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this@DomiciliooActivity, "La dirección está vacía", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        return locationRequest
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            settingsOK = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if ((e as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                val resolvable = e as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                getLocationSettings.launch(isr)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            if (settingsOK) {
                mLocationCallback?.let {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        it, null)
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        mLocationCallback?.let { mFusedLocationClient.removeLocationUpdates(it) }
    }

    // Función para realizar la solicitud al servicio REST
    fun requestWeatherForecast(latitude: Double, longitude: Double) {
        Log.i("VOLLEY","VOLLEY REQUEST $latitude $longitude")
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"

        // Crea la solicitud de tipo GET utilizando JsonObjectRequest
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Accede a los valores de "temperature" y "windspeed" en la respuesta
                val currentWeather = response.getJSONObject("current_weather")
                val temperature = currentWeather.getDouble("temperature")
                val windspeed = currentWeather.getDouble("windspeed")

                // Haz algo con los valores obtenidos
                Log.i("WEATHER","Temperatura: $temperature")
                Log.i("WINSPEED","Velocidad del viento: $windspeed")
                binding.textView.text = "$temperature °C ¦ $windspeed m/s"
            },
            { error ->
                // Maneja los errores de la solicitud
                error.printStackTrace()
            }
        )

        // Agrega la solicitud a la cola de Volley
        val requestQueue = Volley.newRequestQueue(baseContext)
        requestQueue.add(request)
    }

    override fun onMapLongClick(point: LatLng) {
        markerPosition = point
        if (mMap != null) {
            // Limpiar marcadores anteriores
            mMap.clear()

            // Agregar Marcador al mapa
            mMap.addMarker(MarkerOptions().position(markerPosition))

            // Hacer zoom en la ubicación buscada
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15f))
        }
    }



}