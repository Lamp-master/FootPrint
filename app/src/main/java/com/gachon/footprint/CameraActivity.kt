package com.gachon.footprint

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
/*import uk.co.appoly.arcorelocation.LocationScene*/
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import kotlin.math.*
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : AppCompatActivity() {
/*    lateinit var locationScene: LocationScene*/
    lateinit var arFragment: ArFragment

    //AR 레이아웃과 연결
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        //ARCORE-Location 라이브러리사용을 위해 중복 사용
        arFragment = sceneform_fragment as ArFragment
/*        locationScene = sceneform_fragment as LocationScene*/
        //탭이벤트 발생시 탭한 곳의 GPS를 보여주고, arobject를 anchor한다.
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            //탭한 위치에 앵커를 설치한다.+*GPS를 받는다
            val anchor = hitResult.createAnchor()

            placeObject(arFragment, anchor)
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
}
