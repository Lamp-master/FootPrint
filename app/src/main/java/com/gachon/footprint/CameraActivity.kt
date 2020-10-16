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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_view_setting.*
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.N) class CameraActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var camera: Camera
    private lateinit var scene: Scene
    private var azimuth: Int = 0
    private var sensorManager: SensorManager? = null
    private var rotationV: Sensor? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rMat = FloatArray(9)
    private var orientation = FloatArray(3)
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private var isArPlaced = false

    //발자취 메세지 리사이클러뷰를 클릭시 보여준다.
    //


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0].toDouble()) + 360).toInt() % 360
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            lastAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnetometer)
            SensorManager.getOrientation(rMat, orientation)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0].toDouble()) + 360).toInt() % 360
        }

        if (!isArPlaced) {
            isArPlaced = true
            Handler().postDelayed({
                SmartLocation.with(this).location().start {
                    placeARByLocation(it, LatLng(35.757546, 51.410120), "Vanak")
                    placeARByLocation(it, LatLng(35.777144, 51.409349), "Mellat")
                    placeARByLocation(it, LatLng(35.765259, 51.419390), "Sattari")
                }
            }, 2000)
        }
    }

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        ViewRenderable.builder()
            .setView(fragment.context,R.layout.activity_camera)
            .build().thenAccept{
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






        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startSensor()
        val arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
        scene = arFragment.arSceneView.scene!!
        camera = scene.camera
    }
    //출처 : https://stackoverflow.com/questions/51888308/render-3d-objects-in-arcore-using-gps-location
    private fun addPointByXYZ(x: Float, y: Float, z: Float, name: String) {
        ViewRenderable.builder().setView(this, R.layout.sample_layout).build().thenAccept {
            val imageView = it.view.findViewById<ImageView>(R.id.imageViewSample)
            val textView = it.view.findViewById<TextView>(R.id.textViewSample)

            textView.text = name

            val node = AnchorNode()
            node.renderable = it
            scene.addChild(node)
            node.worldPosition = Vector3(x, y, z)

            val cameraPosition = scene.camera.worldPosition
            val direction = Vector3.subtract(cameraPosition, node.worldPosition)
            val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
            node.worldRotation = lookRotation
        }
    }

    private fun bearing(locA: Location, locB: Location): Double {
        val latA = locA.latitude * PI / 180
        val lonA = locA.longitude * PI / 180
        val latB = locB.latitude * PI / 180
        val lonB = locB.longitude * PI / 180

        val deltaOmega = ln(tan((latB / 2) + (PI / 4)) / tan((latA / 2) + (PI / 4)))
        val deltaLongitude = abs(lonA - lonB)

        return atan2(deltaLongitude, deltaOmega)
    }

    private fun placeARByLocation(myLocation: Location, targetLocation: LatLng, name: String) {
        val tLocation = Location("")
        tLocation.latitude = targetLocation.latitude
        tLocation.longitude = targetLocation.longitude

        val degree = (360 - (bearing(myLocation, tLocation) * 180 / PI))
        val distant = 3.0

        val y = 0.0
        val x = distant * cos(PI * degree / 180)
        val z = -1 * distant * sin(PI * degree / 180)
        addPointByXYZ(x.toFloat(), y.toFloat(), z.toFloat(), name)

        Log.i("ARCore_MyLat", myLocation.latitude.toString())
        Log.i("ARCore_MyLon", myLocation.longitude.toString())
        Log.i("ARCore_TargetLat", targetLocation.latitude.toString())
        Log.i("ARCore_TargetLon", targetLocation.longitude.toString())
        Log.i("ARCore_COMPASS", azimuth.toString())
        Log.i("ARCore_Degree", degree.toString())
        Log.i("ARCore_X", x.toString())
        Log.i("ARCore_Y", y.toString())
        Log.i("ARCore_Z", z.toString())
    }

    private fun startSensor() {
        if (sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager!!.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            rotationV = sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            sensorManager!!.registerListener(this, rotationV, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun stopSensor() {
        sensorManager!!.unregisterListener(this, accelerometer)
        sensorManager!!.unregisterListener(this, magnetometer)
    }



    private fun addControlsToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
    }


    override fun onPause() {
        super.onPause()
        stopSensor()
    }

    override fun onResume() {
        super.onResume()
        startSensor()
    }

}