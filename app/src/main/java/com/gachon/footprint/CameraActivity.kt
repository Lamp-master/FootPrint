package com.gachon.footprint


import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gachon.footprint.api.FoursquareAPI
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.data.ModelReview
import com.gachon.footprint.model.Geolocation
import com.gachon.footprint.model.Venue
import com.gachon.footprint.model.VenueWrapper
import com.gachon.footprint.model.converter.VenueTypeConverter
import com.gachon.footprint.utils.AugmentedRealityLocationUtils
import com.gachon.footprint.utils.AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER
import com.gachon.footprint.utils.AugmentedRealityLocationUtils.INVALID_MARKER_SCALE_MODIFIER
import com.gachon.footprint.utils.PermissionUtils
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.location_layout_renderable.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.distance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.CompletableFuture

//AR실행 순서 ARCore와 호환되는지 검사->GPS/Camera권한획득->Sceneform및LocationScene SDK 설정
//LocationScene(장비위치요청/wait)->FoursquareAPI에서 장소를 가져오기
//arSceneView에 장소 추가->LocationMarker(장소레이아웃생성/렌더링)설정 및 마커 조정
//장면에 장소 추가(이전단계 반복)
class CameraActivity : AppCompatActivity(), Callback<VenueWrapper> {

    var photoUri: Uri? = null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user?.uid
    var footmsgInfo: ModelFoot? = ModelFoot()

    var reviewList = ArrayList<ModelReview>()

    var footmsgList: Venue? = Venue()
    var distance: Double? = null
    var lat: String? = null
    var lon: String? = null
    var dlat: Double? = null
    var dlon: Double? = null
    var flat: Double = 0.0
    var flon: Double = 0.0

    private var arCoreInstallRequested = false

    // Our ARCore-Location scene
    private var locationScene: LocationScene? = null

    private var arHandler = Handler(Looper.getMainLooper())

    lateinit var loadingDialog: AlertDialog

    private val resumeArElementsTask = Runnable {
        locationScene?.resume()
        arSceneView.resume()
    }
    lateinit var foursquareAPI: FoursquareAPI
    private var apiQueryParams = mutableMapOf<String, String>()
    private var userGeolocation = Geolocation.EMPTY_GEOLOCATION

    private var footMsgList: MutableSet<Venue> = mutableSetOf()
    private var venuesSet: MutableSet<Venue> = mutableSetOf()
    private var areAllMarkersLoaded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_augmented_reality_location)
        getUserInfo()
        getFootMsgGps()
        setupRetrofit()
        setupLoadingDialog()
        fetchVenues(37.5118, 126.8518)
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
    }

    override fun onPause() {
        super.onPause()
        arSceneView.session?.let {
            locationScene?.pause()
            arSceneView?.pause()
        }
    }

    private fun setupRetrofit() {
        //apiQueryParams["client_id"] = //YOUR CLIENT ID
        //apiQueryParams["client_secret"] =// YOUR CLIENT SECRET
        apiQueryParams["v"] = "20190716"
        apiQueryParams["limit"] = "10"
        apiQueryParams["categoryId"] = "52e81612bcbc57f1066b79f1"

        val gson = GsonBuilder()
            .registerTypeAdapter(
                VenueWrapper::class.java,
                VenueTypeConverter()
            )
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(FoursquareAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        foursquareAPI = retrofit.create(FoursquareAPI::class.java)
    }

    private fun setupLoadingDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogHintMainView =
            LayoutInflater.from(this).inflate(R.layout.loading_dialog, null) as LinearLayout
        alertDialogBuilder.setView(dialogHintMainView)
        loadingDialog = alertDialogBuilder.create()
        loadingDialog.setCanceledOnTouchOutside(false)
    }

    private fun setupSession() {
        if (arSceneView == null) {
            return
        }

        if (arSceneView.session == null) {
            try {
                val session =
                    AugmentedRealityLocationUtils.setupSession(this, arCoreInstallRequested)
                if (session == null) {
                    arCoreInstallRequested = true
                    return
                } else {
                    arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                AugmentedRealityLocationUtils.handleSessionException(this, e)
            }
        }

        if (locationScene == null) {
            locationScene = LocationScene(this, arSceneView)
            locationScene!!.setMinimalRefreshing(true)
            locationScene!!.setOffsetOverlapping(true)
//            locationScene!!.setRemoveOverlapping(true)
            locationScene!!.anchorRefreshInterval = 2000
        }

        try {
            resumeArElementsTask.run()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(this, "Unable to get camera", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (userGeolocation == Geolocation.EMPTY_GEOLOCATION) {
            LocationAsyncTask(WeakReference(this@CameraActivity)).execute(locationScene!!)
        }
    }

    private fun fetchVenues(deviceLatitude: Double, deviceLongitude: Double) {

        loadingDialog.dismiss()
        userGeolocation = Geolocation(deviceLatitude.toString(), deviceLongitude.toString())
        apiQueryParams["ll"] = "$deviceLatitude,$deviceLongitude"
        foursquareAPI.searchVenues(apiQueryParams).enqueue(this)
    }

    override fun onResponse(call: Call<VenueWrapper>, response: Response<VenueWrapper>) {
        val venueWrapper = response.body() ?: VenueWrapper(listOf())
        venuesSet.clear()
        venuesSet.addAll(footMsgList)
        areAllMarkersLoaded = false
        locationScene!!.clearMarkers()
        renderVenues()
    }

    override fun onFailure(call: Call<VenueWrapper>, t: Throwable) {
        //handle api call failure
    }

    private fun renderVenues() {
        setupAndRenderVenuesMarkers()
        updateVenuesMarkers()
    }

    private fun setupAndRenderVenuesMarkers() {
        venuesSet.forEach { venue ->
            val completableFutureViewRenderable = ViewRenderable.builder()
                .setView(this, R.layout.location_layout_renderable)
                .build()
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    //here we know the renderable was built or not
                    if (throwable != null) {
                        // handle renderable load fail
                        return@handle null
                    }
                    try {
                        val venueMarker = venue.longitude?.toDouble()?.let {
                            venue.latitude?.toDouble()?.let { it1 ->
                                LocationMarker(
                                    it,
                                    it1,
                                    setVenueNode(venue, completableFutureViewRenderable)
                                )
                            }
                        }
                        arHandler.postDelayed({
                            if (venueMarker != null) {
                                attachMarkerToScene(
                                    venueMarker,
                                    completableFutureViewRenderable.get().view
                                )
                            }
                            if (venuesSet.indexOf(venue) == venuesSet.size - 1) {
                                areAllMarkersLoaded = true
                            }
                        }, 200)

                    } catch (ex: Exception) {
                        //                        showToast(getString(R.string.generic_error_msg))
                    }
                    null
                }
        }
    }

    private fun updateVenuesMarkers() {
        arSceneView.scene.addOnUpdateListener()
        {
            if (!areAllMarkersLoaded) {
                return@addOnUpdateListener
            }

            locationScene?.mLocationMarkers?.forEach { locationMarker ->
                locationMarker.height =
                    AugmentedRealityLocationUtils.generateRandomHeightBasedOnDistance(
                        locationMarker?.anchorNode?.distance ?: 0
                    )
            }


            val frame = arSceneView!!.arFrame ?: return@addOnUpdateListener
            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@addOnUpdateListener
            }
            locationScene!!.processFrame(frame)
        }
    }

    private fun attachMarkerToScene(
        locationMarker: LocationMarker,
        layoutRendarable: View
    ) {
        resumeArElementsTask.run {
            locationMarker.scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN
            locationMarker.scaleModifier = INITIAL_MARKER_SCALE_MODIFIER

            locationScene?.mLocationMarkers?.add(locationMarker)
            locationMarker.anchorNode?.isEnabled = true


            arHandler.post {
                locationScene?.refreshAnchors()
                layoutRendarable.pinContainer.visibility = View.VISIBLE
            }
        }
        locationMarker.setRenderEvent { locationNode ->
            layoutRendarable.distance.text =
                AugmentedRealityLocationUtils.showDistance(locationNode.distance)
            resumeArElementsTask.run {
                computeNewScaleModifierBasedOnDistance(locationMarker, locationNode.distance)
            }
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(
        locationMarker: LocationMarker,
        distance: Int
    ) {
        val scaleModifier =
            AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(distance)
        return if (scaleModifier == INVALID_MARKER_SCALE_MODIFIER) {
            detachMarker(locationMarker)
        } else {
            locationMarker.scaleModifier = scaleModifier
        }
    }

    private fun detachMarker(locationMarker: LocationMarker) {
        locationMarker.anchorNode?.anchor?.detach()
        locationMarker.anchorNode?.isEnabled = false
        locationMarker.anchorNode = null
    }


    private fun setVenueNode(
        venue: Venue,
        completableFuture: CompletableFuture<ViewRenderable>
    ): Node {
        val node = Node()
        node.renderable = completableFuture.get()

        val nodeLayout = completableFuture.get().view
        val venueName = nodeLayout.name
        val markerLayoutContainer = nodeLayout.pinContainer
        venueName.text = venue.title
        markerLayoutContainer.visibility = View.GONE
        nodeLayout.setOnTouchListener { _, _ ->
            val intent = Intent(this, RecyclerFootMsgView::class.java)
            intent.putExtra("FootMsgId", "${venue.footmsgid}")
            startActivity(intent)
            false
        }

        Timber.d("Test checked image url ${venue.imageUrl}")
        Glide.with(this)
            .load(venue.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(nodeLayout.categoryIcon)

        return node
    }


    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(this)) {
            PermissionUtils.requestCameraAndLocationPermissions(this)
        } else {
            setupSession()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(this)) {
            Toast.makeText(
                this, R.string.camera_and_location_permission_request, Toast.LENGTH_LONG
            )
                .show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                PermissionUtils.launchPermissionSettings(this)
            }
            finish()
        }
    }

    class LocationAsyncTask(private val activityWeakReference: WeakReference<CameraActivity>) :
        AsyncTask<LocationScene, Void, List<Double>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            activityWeakReference.get()!!.loadingDialog.show()
        }

        override fun doInBackground(vararg p0: LocationScene): List<Double> {
            var deviceLatitude: Double?
            var deviceLongitude: Double?
            do {
                deviceLatitude = p0[0].deviceLocation?.currentBestLocation?.latitude
                deviceLongitude = p0[0].deviceLocation?.currentBestLocation?.longitude
            } while (deviceLatitude == null || deviceLongitude == null)
            return listOf(deviceLatitude, deviceLongitude)
        }

        override fun onPostExecute(geolocation: List<Double>) {
            activityWeakReference.get()!!
                .fetchVenues(deviceLatitude = geolocation[0], deviceLongitude = geolocation[1])
            super.onPostExecute(geolocation)
        }
    }

    private fun getUserInfo() {
        if (user != null) {
            db.collection("User").document(user!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    footmsgInfo = documentSnapshot.toObject(ModelFoot::class.java)
                }
        }
    }

    //메인에서 사용자 gps 넘겨받음
    private fun getLocationFromMain() {
        //메인에서 GPS 데이터 가져오기
        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
            dlat = lat?.toDouble()
            dlon = lon?.toDouble()
            footmsgInfo?.latitude = dlat
            footmsgInfo?.longitude = dlon
        }
    }

    //주변 메시지 위치 받아옴
    private fun getFootMsgGps() {
        db.collection("FootMsg").get().addOnSuccessListener { documents ->
            for (document in documents) {
                var item = document.toObject(Venue::class.java)
                item.footmsgid = document.id

                var map: Map<String, Any> = document.data
                var tempLat = map["latitude"].toString()
                var tempLon = map["longitude"].toString()
                val cur = footmsgInfo?.latitude?.toDouble()?.let {
                    footmsgInfo?.longitude?.toDouble()?.let { it1 ->
                        LatLng(
                            it,
                            it1
                        )
                    }
                }
                val tempGps = LatLng(tempLat.toDouble(), tempLon.toDouble())
                if (tempGps != null) {
                    if (cur != null) {
                        var distance = SphericalUtil.computeDistanceBetween(cur, tempGps) / 1000
                        if (distance < 1) {
                            footMsgList.add(item)
                        }
                    }
                }
            }
        }
    }
}