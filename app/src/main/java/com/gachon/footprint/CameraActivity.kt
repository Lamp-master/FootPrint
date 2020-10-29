package com.gachon.footprint

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.ArCoreApk.InstallStatus
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.rendering.AnnotationRenderer


class CameraActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    var lat: String? = null
    var lon: String? = null

    lateinit var locationScene: LocationScene
    lateinit var locationMarker: LocationMarker

    lateinit var mSession:Session
    private var mUserRequestInstall: Boolean = true
    private val MIN_OPENGL_VERSION = 3.0

    //메인액티비티에서 가져온 위/경도 값

    //AR 레이아웃과 연결
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //버전 체크
        if(!checkSupported(this)){
            return;
        }
/*        setContentView(R.layout.activity_camera)
        //ARCORE-Location 라이브러리사용을 위해 중복 사용
        arFragment = sceneform_fragment as ArFragment

        //탭이벤트 발생시 탭한 곳의 GPS를 보여주고, arobject를 anchor한다.
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            //탭한 위치에 앵커를 설치한다.+*GPS를 받는다
            val anchor = hitResult.createAnchor()

            placeObject(arFragment, anchor)
        }*/
        //메인에서 GPS 데이터 가져오기
        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
        }
        // double 자료형으로 변환
        val dlat = lat?.toDouble()
        val dlon = lon?.toDouble()



        //locationScene 인스턴스
        locationScene = LocationScene(this, this, mSession)
        //메인에서 가져온 GPS 저장 후 화면에 출력
        val buckinghamPalace = LocationMarker(
            0.1419,
            51.5014,
            AnnotationRenderer("Buckingham Palace")
        )
        buckinghamPalace.setOnTouchListener {
            Toast.makeText(
                this@CameraActivity,
                "Touched Buckingham Palace", Toast.LENGTH_SHORT
            ).show()
        }
        locationScene.mLocationMarkers.add(buckinghamPalace)



    }//onCreate

    override fun onResume() {
        super.onResume()
        try {
            if (mSession == null) {
                when (ArCoreApk.getInstance().requestInstall(this, mUserRequestInstall)) {
                    InstallStatus.INSTALLED -> {
                        mSession = Session(this)
                        Toast.makeText(this, "세션생성성공", Toast.LENGTH_SHORT).show()
                        //여기서 onResume() 작업 작성
                        locationScene.resume()


                    }
                    InstallStatus.INSTALL_REQUESTED -> {
                        mUserRequestInstall = false
                        Toast.makeText(this, "세션생성실패", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "에러 매새지$e", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationScene != null) {
            locationScene.pause()
        }
        try {
            if (mSession != null) {
                mSession.pause()
            }
        } catch (unused: java.lang.Exception) {
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
                    // TODO: do smth here
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


}
