package com.example.shoppin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import com.example.shoppin.databinding.ActivityGoogleMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    //lateinit var mapFragment: SupportMapFragment

    private lateinit var mMap: GoogleMap

    // ViewBinding
    private lateinit var binding: ActivityGoogleMapsBinding

    // ActionBar
    private lateinit var actionBar: ActionBar

    // currentMarker
    private lateinit var currentMarker: Marker

    // fusedLocationProviderClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // currentLocation
    private lateinit var currentLocation: Location

    // permissionCode
    private val permissionCode = 101




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Your location"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }

        val getLocation = fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    currentLocation = location

                    Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                    currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latlong = LatLng(currentLocation.latitude, currentLocation.longitude)
        drawMarker(latlong)

        mMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null) {
                    currentMarker.remove()

                    val newLatLng = LatLng(p0.position.latitude, p0.position.longitude)
                    drawMarker(newLatLng)
                }

            }

            override fun onMarkerDragStart(p0: Marker) {

            }
        })
    }

    private fun drawMarker(latlong : LatLng) {
        val markerOption = MarkerOptions().position(latlong).title("I am here")
            .snippet(getAddress(latlong.latitude, latlong.longitude)).draggable(true)

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        currentMarker = mMap.addMarker(markerOption)!!
        currentMarker.showInfoWindow()
    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)

        return addresses[0].getAddressLine(0).toString()
    }

}

