package com.example.myapplication


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.databinding.AnnotationViewBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


var mapView: MapView? = null
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
//        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
        mapView?.getMapboxMap()?.loadStyleUri(Style.SATELLITE_STREETS){
            it.addSource(rasterDemSource("TERRAIN_SOURCE"){
                url("mapbox://styles/soumya5463/clmvhk6p302lt01nz9z3ofc6j")
            })
        }




        val handler = Handler()
        val delay = 10000
        val refreshRunnable = object : Runnable {
            override fun run() {
                getCoordinate()
                handler.postDelayed(this, delay.toLong())
            }
        }
        handler.post(refreshRunnable)

    }
    private fun getCoordinate(){
        RetrofitInstance.apiInterface3.getCoordinate().enqueue(object : Callback<CoordinateResponseData?> {
            override fun onResponse(
                call: Call<CoordinateResponseData?>,
                response: Response<CoordinateResponseData?>
            ) {

                var x=response.body()?.Longitude
                var y=response.body()?.Latitude

                //for map camera
                val initialCameraOptions = CameraOptions.Builder()
                    .center(x?.let { y?.let { it1 -> Point.fromLngLat(it, it1) } })
                    .pitch(45.0)
                    .zoom(18.0)
                    .bearing(-17.6)
                    .build()

                var animationOptions =MapAnimationOptions.Builder().duration(10000).build()
                mapView!!.getMapboxMap().flyTo(initialCameraOptions,animationOptions)


//                for marker
                val annotationApi = mapView?.annotations
                val pointAnnotationManager = mapView?.let { annotationApi?.createPointAnnotationManager(it) }
                val pointAnnotationOptions: PointAnnotationOptions? =
                    x?.let { y?.let { it1 -> Point.fromLngLat(it, it1) } }?.let {
                        PointAnnotationOptions()
                            .withPoint(it)
                            .withIconImage(getDrawable(R.drawable.cowlocation)!!.toBitmap())
                    }
                   pointAnnotationOptions?.let { pointAnnotationManager?.create(it) }
            }

            override fun onFailure(call: Call<CoordinateResponseData?>, t: Throwable) {
                Toast.makeText(this@MainActivity,
                    t.localizedMessage
                    , Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}