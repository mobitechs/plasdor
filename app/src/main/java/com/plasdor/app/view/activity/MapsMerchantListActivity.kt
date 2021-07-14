package com.plasdor.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plasdor.app.R
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.model.UserModel

class MapsMerchantListActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var merchantListItems = ArrayList<AvailableMerchantListItem>()
    var userLat = ""
    var userLong = ""
    var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        merchantListItems = intent.getParcelableArrayListExtra("merchantListItems")!!

        userLat = intent.getStringExtra("userLat")!!
        userLong = intent.getStringExtra("userLong")!!
        userName = intent.getStringExtra("userName")!!
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        for(item in 0..merchantListItems.size-1){
          val sydney = LatLng(merchantListItems[item].latitude.toDouble(), merchantListItems[item].longitude.toDouble())
            mMap.addMarker(MarkerOptions().position(sydney).title(merchantListItems[item].name))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        val sydney = LatLng(userLat.toDouble(), userLong.toDouble())
        mMap.addMarker(MarkerOptions().position(sydney).title(userName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16F))




    }
}