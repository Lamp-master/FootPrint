package com.gachon.footprint

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.footprint_dialog.*


//AR이미지를 선택하고 정렬/위치정보를 MsgFoot으로 전달/
class CameraActivity : AppCompatActivity() {
    private lateinit var arFragment : ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        arFragment=supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        arFragment.setOnTapArPlaneListener(BaseArFragment.OnTapArPlaneListener { hitResult, plane, motionEvent ->
            var anchor : Anchor = hitResult.createAnchor()

            // Sceneform 라이브러리에 있는 ModelRenderable을 이용하여 sfb파일을 Renderable로 변환
            ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept { addModelToScence(anchor, it) }
                .exceptionally {
                    val builder : AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setMessage(it.localizedMessage)
                        .show()
                    return@exceptionally null
                }
        })

        btn_see_footprint.setOnClickListener() {
            val dialg = FootDialog.FootDialogBuild()

            val dialog = FootDialog.FootDialogBuild()

                .setDialogTitle("발자취 보기")
                .setTitle("이건 발자취 제목")
                .setLocation("이건 발자취 위치")
                .setContent("이건 발자취 내용")
                .setNickname("발자취 쓴 사람 닉네임")
                .setFirstBtnText("댓글")
                .setSecondBtnText("추천")
                .setButtonClick(object : FootDialog.customClickListener {
                    override fun onFirstBtnClick() {
                        //댓글버튼
                    }

                    override fun onSecondBtnClick() {
                        //추천버튼
                    }
                })

                .create()
            dialog.show(supportFragmentManager, dialog.tag)
        }
    }

    //오브젝트를 추가
    private fun addModelToScence(anchor: Anchor, it:ModelRenderable?){
        val anchorNode : AnchorNode = AnchorNode(anchor)
        val transform : TransformableNode = TransformableNode(arFragment.transformationSystem)
        transform.setParent(anchorNode)
        transform.renderable = it
        arFragment.arSceneView.scene.addChild(anchorNode)
        transform.select()
    }



}