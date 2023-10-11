package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.databinding.ActivityDataBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var mapView2: MapView? = null
class DataActivity : AppCompatActivity() {
    lateinit var binding: ActivityDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView2 = binding.mapView2
        mapView2?.getMapboxMap()?.loadStyleUri(Style.SATELLITE_STREETS){
            mapView2?.getMapboxMap()!!.addOnMapClickListener(){
                    change()
            }
        }
        val handler = Handler()
        val delay = 10000
        val refreshRunnable = object : Runnable {
            override fun run() {
                getData()
                getCoordinate()

                handler.postDelayed(this, delay.toLong())
            }
        }
        handler.post(refreshRunnable)


    }


    //    for IOT device Data


    private fun getData() {
        RetrofitInstance.apiInterface.getData().enqueue(object : retrofit2.Callback<ResponseData?> {
            override fun onResponse(call: retrofit2.Call<ResponseData?>, response: retrofit2.Response<ResponseData?>) {
                binding.humidity.text = response.body()?.humidity+"%"
                binding.temperature.text = response.body()?.temperature+" C"
            }

//
            override fun onFailure(call: retrofit2.Call<ResponseData?>, t: Throwable) {
                Toast.makeText(this@DataActivity,
                    t.localizedMessage
                    , Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getCoordinate(){
        RetrofitInstance.apiInterface3.getCoordinate().enqueue(object : Callback<CoordinateResponseData?> {
            override fun onResponse(
                call: Call<CoordinateResponseData?>,
                response: Response<CoordinateResponseData?>
            ) {

                var x=response.body()?.Longitude
                var y=response.body()?.Latitude
                binding.BPM.text=response.body()?.BPM.toString()

                //for map camera
                val initialCameraOptions = CameraOptions.Builder()
                    .center(x?.let { y?.let { it1 -> Point.fromLngLat(it, it1) } })
                    .pitch(45.0)
                    .zoom(15.0)
                    .bearing(-17.6)
                    .build()

                var animationOptions = MapAnimationOptions.Builder().duration(10000).build()
                mapView2!!.getMapboxMap().flyTo(initialCameraOptions,animationOptions)


//                for marker
                val annotationApi = mapView2?.annotations
                val pointAnnotationManager = mapView2?.let { annotationApi?.createPointAnnotationManager(it) }
                val pointAnnotationOptions: PointAnnotationOptions? =
                    x?.let { y?.let { it1 -> Point.fromLngLat(it, it1) } }?.let {
                        PointAnnotationOptions()
                            .withPoint(it)
                            .withIconImage(getDrawable(R.drawable.cowlocation)!!.toBitmap())
                    }
                pointAnnotationOptions?.let { pointAnnotationManager?.create(it) }
            }

            override fun onFailure(call: Call<CoordinateResponseData?>, t: Throwable) {
                Toast.makeText(this@DataActivity,
                    t.localizedMessage
                    , Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun change(): Boolean {
        val intent = Intent(this@DataActivity, MainActivity::class.java)
        startActivity(intent)
        return true
    }
}