package com.gachon.footprint


import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gachon.footprint.api.FoursquareAPI
import com.gachon.footprint.model.Geolocation
import com.gachon.footprint.model.VenueWrapper
import com.gachon.footprint.model.converter.VenueTypeConverter
import com.gachon.footprint.utils.AugmentedRealityLocationUtils
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.model.Venue
import com.gachon.footprint.utils.AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER
import com.gachon.footprint.utils.AugmentedRealityLocationUtils.INVALID_MARKER_SCALE_MODIFIER
import com.gachon.footprint.utils.PermissionUtils
import com.google.ar.core.*
import com.google.ar.core.ArCoreApk.InstallStatus
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_footprint.*
import kotlinx.android.synthetic.main.activity_footprint.add_footprint_context
import kotlinx.android.synthetic.main.activity_user_enter.*
import kotlinx.android.synthetic.main.footprint_dialog.*
import kotlinx.android.synthetic.main.location_layout_renderable.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.distance
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture

//AR실행 순서 ARCore와 호환되는지 검사->GPS/Camera권한획득->Sceneform및LocationScene SDK 설정
//LocationScene(장비위치요청/wait)->FoursquareAPI에서 장소를 가져오기
//arSceneView에 장소 추가->LocationMarker(장소레이아웃생성/렌더링)설정 및 마커 조정
//장면에 장소 추가(이전단계 반복)
class CameraActivity : AppCompatActivity(){
    //파이어베이스
    var photoUri: Uri? = null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user?.uid
    var footmsgInfo: ModelFoot? = ModelFoot()
    //Main의 GPS
    var lat: String? = null
    var lon: String? = null
    var dlat: Double? = null
    var dlon: Double? = null
    var flat: Double =0.0
    var flon: Double =0.0
    //ARScene을 그릴 뷰
    lateinit var mSession: Session
    private var mUserRequestInstall: Boolean = true
    private val MIN_OPENGL_VERSION = 3.0

    lateinit var arFragment: ArFragment
    lateinit var locationMarker: LocationMarker
    //firebase에서 Footmsg에서 각 위도 경도 가져와서 배열에 저장
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

    private var venuesSet: MutableSet<Venue> = mutableSetOf()
    private var areAllMarkersLoaded = false


    //AR 레이아웃과 연결
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        Timber.d("Test checked camera")
        //버전 체크
        if (!checkSupported(this)) {
            return;
        }
        setContentView(R.layout.activity_camera)
        //ARCORE-Location 라이브러리사용을 위해 중복 사용
        locationScene= LocationScene(this,arSceneView)
        arFragment = sceneform_fragment as ArFragment
        auth = FirebaseAuth.getInstance()
        getUserInfo()
        getLocationFromMain()

        //탭이벤트 발생시 탭한 곳의 GPS를 보여주고, arobject를 anchor한다.
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            Timber.d("Test checked TapClick")
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            //탭한 위치에 앵커를 설치한다.+*GPS를 받는다
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor)
        }

    }//onCreate

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

    fun addFireStore() {
        //사용자 이미지 업로드

        footmsgInfo?.let { it1 ->
            db.collection("FootMsg").add(it1).addOnSuccessListener { documentReference ->
            }
        }
        Toast.makeText(this, "발자취 등록에 성공했습니다", Toast.LENGTH_LONG).show()
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
    private fun getLocationFromMain(){
        Timber.d("Test checked in getLocationFromMain")
        //메인에서 GPS 데이터 가져오기
        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
            dlat = lat?.toDouble()
            dlon = lon?.toDouble()
            footmsgInfo?.latitude = dlat
            footmsgInfo?.longitude = dlon
            Timber.d("Test checked location ${footmsgInfo?.latitude} ${footmsgInfo?.longitude}")


        }
    }






    //보여주는 것은 어떻게 할 까?

    private fun placeObject(fragment: ArFragment, anchor: Anchor) {
        ViewRenderable.builder()
            .setView(fragment.context, R.layout.controls)
            .build()
            .thenAccept {
                it.isShadowCaster = false
                it.isShadowReceiver = false
                it.view.findViewById<ImageButton>(R.id.info_button).setOnClickListener {
                    val intent = Intent(this, FootMsgActivity::class.java)
                    intent.putExtra("LAT", "$dlat")
                    intent.putExtra("LON", "$dlon")
                    startActivity(intent)

                }
                addControlsToScene(fragment, anchor, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addControlsToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
    }


    // 버전 체크 자바 1.8이상, OpenGL3.0이상이 안될시 off
    fun checkSupported(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("VersionError", "Sceneform 은 안드로이드 N이상에서 동작합니다")
            Toast.makeText(activity, "Sceneform 은 안드로이드 N이상에서 동작합니다", Toast.LENGTH_SHORT).show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion

        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e("VersionError", "Sceneform은 OpenGL ES 3.0이상에서 작동합니다")
            Toast.makeText(activity, "Sceneform은 OpenGL ES 3.0이상에서 작동합니다", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        return true
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



    private fun setupSession() {
        if (arSceneView == null) {
            return
        }

        if (arSceneView.session == null) {
            try {
                val session = AugmentedRealityLocationUtils.setupSession(this, arCoreInstallRequested)
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
            locationScene= LocationScene(this,arSceneView)
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

    fun onResponse(call: Call<VenueWrapper>, response: Response<VenueWrapper>) {
        val venueWrapper = response.body() ?: VenueWrapper(listOf())
        venuesSet.clear()
        venuesSet.addAll(venueWrapper.venueList)
        areAllMarkersLoaded = false
        locationScene!!.clearMarkers()
        renderVenues()
    }

    fun onFailure(call: Call<VenueWrapper>, t: Throwable) {
        //handle api call failure
    }

    private fun renderVenues() {
        setupAndRenderVenuesMarkers()
        updateVenuesMarkers()
    }
    //AR마커 설정 및 렌더링 함수
    private fun setupAndRenderVenuesMarkers() {
        venuesSet.forEach { venue ->
            //렌더링할 레이아웃 설정
            val completableFutureViewRenderable = ViewRenderable.builder()
                .setView(this, R.layout.activity_camera)
                .build()
            //completableFuture 객체 생성
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    //here we know the renderable was built or not
                    if (throwable != null) {
                        // handle renderable load fail
                        return@handle null
                    }
                    try {
                        val venueMarker = LocationMarker(
                            venue.long.toDouble(),
                            venue.lat.toDouble(),
                            setVenueNode(venue, completableFutureViewRenderable)
                        )
                        arHandler.postDelayed({
                            attachMarkerToScene(
                                venueMarker,
                                completableFutureViewRenderable.get().view
                            )
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
        //setRenderEvent->프레임마다 호출/노드 업데이트
        locationMarker.setRenderEvent { locationNode ->
            layoutRendarable.distance.text = AugmentedRealityLocationUtils.showDistance(locationNode.distance)
            resumeArElementsTask.run {
                computeNewScaleModifierBasedOnDistance(locationMarker, locationNode.distance)
            }
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(locationMarker: LocationMarker, distance: Int) {
        val scaleModifier = AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(distance)
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

    //특정장소에 대응하는 locationMarker 생성+Click이벤트
    private fun setVenueNode(venue: Venue, completableFuture: CompletableFuture<ViewRenderable>): Node {
        val node = Node()
        node.renderable = completableFuture.get()

        val nodeLayout = completableFuture.get().view
        val venueName = nodeLayout.name
        val markerLayoutContainer = nodeLayout.pinContainer
        venueName.text = venue.name
        markerLayoutContainer.visibility = View.GONE
        //클릭이벤트
        nodeLayout.setOnTouchListener { _, _ ->
            Toast.makeText(this, venue.address, Toast.LENGTH_SHORT).show()
            false
        }

        Glide.with(this)
            .load(venue.iconURL)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
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
            activityWeakReference.get()!!.fetchVenues(deviceLatitude = geolocation[0], deviceLongitude = geolocation[1])
            super.onPostExecute(geolocation)
        }
    }
    private fun detachMarkerFromScene(locationMarker: LocationMarker) {
        locationMarker.anchorNode?.anchor?.detach()
        locationMarker.anchorNode?.isEnabled = false
        locationMarker.anchorNode = null
    }


}