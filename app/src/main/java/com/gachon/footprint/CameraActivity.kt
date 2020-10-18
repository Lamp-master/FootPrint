package com.gachon.footprint

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_camera.*
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil.setContentView
import com.gachon.footprint.data.ModelAnchor
import com.gachon.footprint.settingfragment.CloudAnchorFragment
import com.gachon.footprint.settingfragment.ResolveDialogFragment
import com.gachon.footprint.settingfragment.SnackbarHelper
import com.google.ar.core.*
import com.google.ar.sceneform.FrameTime
import kotlinx.android.synthetic.main.activity_main.*



//AR이미지를 선택하고 정렬/위치정보를 MsgFoot으로 전달/
class CameraActivity : AppCompatActivity() {
    lateinit var arFragment: CloudAnchorFragment
    var cloudAnchor: Anchor? = null
    enum class AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }
    var appAnchorState = AppAnchorState.NONE
    var snackbarHelper = SnackbarHelper()
    var ModelAnchor = ModelAnchor()
    //cloud anchor 활성화
    //plane(카메라 화면 터치해서 ) anchor과 3D model 위치시키기
    //Scene 터치할때마다 Anchor Hosting 시작시키기(현재 앵커와 상태 업데이트)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as CloudAnchorFragment
        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)


        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING || appAnchorState != AppAnchorState.NONE) {
                return@setOnTapArPlaneListener
            }
            val anchor = arFragment.arSceneView.session?.hostCloudAnchor(hitResult.createAnchor())
            cloudAnchor(anchor)
            appAnchorState = AppAnchorState.HOSTING
            snackbarHelper.showMessage(this, "Hosting anchor")

            placeObject(arFragment, cloudAnchor!!)
        }

     //버튼 동작 추가 1.cloudAnchor 초기화 2. anchor에서 메세지 작성 화면으로 이동
        btn_clear.setOnClickListener{
            cloudAnchor(null)
        }
        btn_message_edit.setOnClickListener{
            //액티비티 이동시키기(arid 전달)
        }
        btn_resolve.setOnClickListener {
            if (cloudAnchor != null) {
                snackbarHelper.showMessageWithDismiss(this, "Please clear the anchor")
                return@setOnClickListener
            }
            val dialog = ResolveDialogFragment()
      //    dialog.setOkListener(this::onResolveOkPressed)
            dialog.show(supportFragmentManager, "Resolve")
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor) {
        ViewRenderable.builder()
            .setView(fragment.context, R.layout.controls)
            .build()
            .thenAccept {
                it.isShadowCaster = false
                it.isShadowReceiver = false
                it.view.findViewById<ImageButton>(R.id.info_button).setOnClickListener {
                    // TODO: do smth here
                    // 버튼 클릭시 액티비티 이동
                    val intent = Intent(this, FootDialog::class.java)
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
   /*  private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
      ModelRenderable.Builder()
          .setSource(fragment.context, model)
          .build()
          .thenAccept { renderable ->
              addNodeToScene(fragment, anchor, renderable)
          }
          .exceptionally {
              val builder = AlertDialog.Builder(this)
              builder.setMessage(it.message).setTitle("Error!")
              val dialog = builder.create()
              dialog.show()
              return@exceptionally null
          }
     }*/

    private fun cloudAnchor(newAnchor: Anchor?) {
        cloudAnchor?.detach()
        cloudAnchor = newAnchor
        appAnchorState = AppAnchorState.NONE
        snackbarHelper.hide(this)
    }

    private fun addControlsToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {
        val node = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(node)
        fragment.arSceneView.scene.addChild(node)
        transformableNode.select()
    }
    //Anchor가 클라우드에 성공적으로 호스팅되었는지 정기적으로 확인하는 함수
    //onUpdateListener를 통해 성공여부 보여주기
    fun onUpdateFrame(frameTime: FrameTime) {
        checkUpdatedAnchor()
    }
    @Synchronized
    //Cloud anchor의 Hosting/Resolving의 현재 상태 확인 후 호스팅 성공실패 여부 확인
    private fun checkUpdatedAnchor() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING)
            return
        val cloudState: Anchor.CloudAnchorState = cloudAnchor?.cloudAnchorState!!
        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudState.isError) {
                snackbarHelper.showMessageWithDismiss(this, "Error hosting anchor...")
                appAnchorState = AppAnchorState.NONE
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                val shortCode = ModelAnchor.nextShortCode(this)
                //cloudanchor 저장
                ModelAnchor.storeUsingShortCode(this, shortCode, cloudAnchor!!.cloudAnchorId)
                snackbarHelper.showMessageWithDismiss(this, "Anchor hosted: $shortCode")
                appAnchorState = AppAnchorState.HOSTED
            }
        } else if (appAnchorState == AppAnchorState.RESOLVING) {
            if (cloudState.isError) {
                snackbarHelper.showMessageWithDismiss(this, "Error resolving anchor...")
                appAnchorState = AppAnchorState.NONE
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                snackbarHelper.showMessageWithDismiss(this, "Anchor resolved...")
                appAnchorState = AppAnchorState.RESOLVED
            }
        }
    }
    fun onResolveOkPressed(dialogVal: String) {

        val shortCode = dialogVal.toInt()
        val cloudAnchorId = ModelAnchor.getCloudAnchorID(this, shortCode)
        val resolvedAnchor = arFragment.arSceneView.session?.resolveCloudAnchor(cloudAnchorId)
        cloudAnchor(resolvedAnchor)
        placeObject(arFragment, cloudAnchor!!)
        snackbarHelper.showMessage(this, "Now resolving anchor...")
        appAnchorState = AppAnchorState.RESOLVING
    }






}