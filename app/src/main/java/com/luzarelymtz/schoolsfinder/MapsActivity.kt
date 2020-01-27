package com.luzarelymtz.schoolsfinder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.Place
//import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
//import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
//import com.google.android.libraries.places.api.net.PlacesClient
//import com.google.android.libraries.places.compat.GeoDataClient
//import com.google.android.libraries.places.compat.Place
//import com.google.android.libraries.places.compat.PlaceDetectionClient
import com.google.android.material.snackbar.Snackbar
import com.luzarelymtz.schoolsfinder.PlaceApi.MapClient
import com.luzarelymtz.schoolsfinder.adapters.SchoolRecyclerAdapter
import com.luzarelymtz.schoolsfinder.model.School
import com.luzarelymtz.schoolsfinder.viewholders.SchoolRecyclerViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList



class MapsActivity() : AppCompatActivity(),OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener, SchoolRecyclerViewHolder.ItemClickListener {
    companion object {
        val MULTIPLE_PERMISSIONS = 10
        val SCHOOLDETAILACTIVITY_REQUEST=3
        private val REQUEST_CHECK_SETTINGS:Int=50
        private val DEFAULT_ZOOM: Float = 15f
        private val TAG:String="MapsActivity"


    }

    private lateinit var mPlacesClient: PlacesClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var map: GoogleMap

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val defaultLocation by lazy { LatLng(-34.0, 151.0) }
    private  var lastKnownLocation: Location? =null

    private var locationPermissionGranted: Boolean = false

    private lateinit var locationCallback:LocationCallback

    private var marker: Marker? = null

    var listPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var schoolAdapter: RecyclerView.Adapter<SchoolRecyclerViewHolder>

    private val recycleListener= RecyclerView.RecyclerListener { holder ->
        val schoolHolder= holder as SchoolRecyclerViewHolder

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_layout) as SupportMapFragment
        mapFragment.getMapAsync(this)

        schoolAdapter= SchoolRecyclerAdapter(this,this,listSchool)
        recyclerView=findViewById<RecyclerView>(R.id.schoolRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = schoolAdapter
            setRecyclerListener(recycleListener)
        }

        Places.initialize(getApplicationContext(), "AIzaSyBqvpsAsR8irQ5wMz8aEp7_dzqaITcQgo4")
        mPlacesClient = Places.createClient(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        lastKnownLocation = location

                        if (fusedLocationProviderClient != null) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setIndoorEnabled(true)
        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)

        updateLocationUI()
    }

    private fun checkPermissions() {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (permission in listPermissions) {
            result = ContextCompat.checkSelfPermission(this.applicationContext, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)

        }else {
            locationPermissionGranted = true
            updateLocationUI()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsDenied = ""
                    val listPermissionsDenied: MutableList<String> = ArrayList()
                    for (permission in permissionsList) {
                        if (grantResults[permissionsList.indexOf(permission)] != PackageManager.PERMISSION_GRANTED) {
                            listPermissionsDenied.add(permission)
                            permissionsDenied += "\n" + permission
                            Log.d(TAG, "Permissions denied: "+permission)
                        }
                    }

                    if (listPermissionsDenied.isEmpty()) {
                        locationPermissionGranted = true
                    }

                }

            }
        }
        updateLocationUI()
    }



    override fun onMapClick(position: LatLng?) {

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        /*// Retrieve the data from the marker.
        var clickCount: Int? = marker?.getTag() as Int

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount!! + 1
            marker?.setTag(clickCount)
            Toast.makeText(
                this,
                marker.getTitle() +
                        " has been clicked " + clickCount + " times.",
                Toast.LENGTH_SHORT
            ).show()
        }*/
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS-> {

                createLocationRequest()

            }
            SCHOOLDETAILACTIVITY_REQUEST-> {

            }
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true)
                map.getUiSettings().setMyLocationButtonEnabled(true)

                createLocationRequest()
            } else {
                map.setMyLocationEnabled(false)
                map.getUiSettings().setMyLocationButtonEnabled(false)
                checkPermissions()
                //puedo mostrar mensaje de que no accepto permisos
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Exception: "+e.message)
        }
    }


    private fun getDeviceLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastKnownLocation=location
                    val position = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))

                    marker = map.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title("Posicion actual")
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location))
                            .snippet("Estoy aqui")
                    )
                    getWSchoolsFromService(location.latitude.toString(),location.longitude.toString())


                }else{
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))
                    Log.d(TAG,"Localizacion es null ")
                    getWSchoolsFromService(defaultLocation.latitude.toString(),defaultLocation.longitude.toString())
                    //fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG,"Exception:  "+ e.message)
        }
    }

    protected fun createLocationRequest() {
        if (locationPermissionGranted) {
            locationRequest = LocationRequest.create()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 5000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener { locationSettingsResponse ->
                val states = locationSettingsResponse.locationSettingsStates
                if (states.isLocationPresent) {
                    getDeviceLocation()
                    //getSchool()


                }
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e(TAG, "Exception createLocationRequest IntentSender.SendIntentException: "+ sendEx)
                    }
                }
            }
        }
    }
    private fun getSchool() {
        // Use fields to define the data types to return.
        var placeFields = Collections.singletonList(Place.Field.LAT_LNG)

        // Use the builder to create a FindCurrentPlaceRequest.
        var request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)


        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (locationPermissionGranted) {
            val placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val response = task.result
                    for (placeLikelihood in response!!.placeLikelihoods) {
                        Log.i(
                            TAG, String.format(
                                "Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()
                            )
                        )
                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        val apiException: ApiException = exception
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode())
                    }
                }
            }
        }
    }

    var listSchool = ArrayList<School>()

    private fun getWSchoolsFromService(_lat:String,_lng:String) {
        val service = MapClient.provideApiService()
        //val lat = "19.421981"
        //val lng = "-99.128895"
        val url = "json?query=school&radius=10000&location=${_lat},${_lng}&key=AIzaSyBqvpsAsR8irQ5wMz8aEp7_dzqaITcQgo4"
        val call = service.getSchools(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val schools = it.results
                for (school in schools) {
                    var name=school.name
                    var address=school.formattedAddress
                    var lat=0.0
                    var lng=0.0
                    var photo_width=0
                    var photo_height=0
                    var photo_reference=""
                    var openNow=false
                    if(school.geometry!=null) {
                        if(school.geometry.location!=null) {
                            lat=school.geometry.location.lat
                            lng=school.geometry.location.lng
                        }
                    }
                    if(school.photos!=null) {
                        if(school.photos.size>0) {
                            photo_width=school.photos[0].width
                            photo_height=school.photos[0].height
                            photo_reference=school.photos[0].photoReference
                        }
                    }
                    if(school.openingHours!=null){
                        openNow=school.openingHours.openNow
                    }
                    var rating=school.rating


                    showLog("Escuela: ${name}, Direccion: ${address}, " +
                            "latitud: ${lat}, longitud: ${lng}," +
                            "referencia photo: ${photo_reference}," +
                            "width photo: ${photo_width}," +
                            "height photo: ${photo_height}," +
                            "opening_now: ${openNow}," +
                            "rating ${rating}")
                    (listSchool as ArrayList<School>).apply {
                        add(School(name,address,lat,lng,photo_width,photo_height,photo_reference,openNow,rating))
                    }
                    marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(lat,lng))
                            .title(name)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.school_pin))
                            .snippet(address))
                    recyclerView.adapter=SchoolRecyclerAdapter(this,this, listSchool)

                }
            }


    }
    private fun showLog(message: String) {
        Log.d(TAG, message)
    }

    override fun onItemClick(view: View, position: Int) {
        //Toast.makeText(applicationContext, "position $position was tapped", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SchoolDetailActivity::class.java)
        intent.putExtra("name",listSchool[position].getName())
        intent.putExtra("address",listSchool[position].getAddress())
        intent.putExtra("openNow",listSchool[position].openNow())
        intent.putExtra("rating",listSchool[position].rating().toString())
        //intent.putParcelableArrayListExtra("schools",listSchool)
        startActivityForResult(intent, SCHOOLDETAILACTIVITY_REQUEST)
    }
}


fun View.showSnackbar(
    msg: Int,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }
    }
    snackbar.show()
}
