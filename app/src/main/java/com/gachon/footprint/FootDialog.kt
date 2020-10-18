package com.gachon.footprint

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.footprint_dialog.*
import kotlinx.android.synthetic.main.footprint_dialog.view.*
import org.w3c.dom.Text

class FootDialog : DialogFragment() {

    var main_Title : String? = null
    var contentPic : String? = null
    var title : String? = null
    var content : String? = null
    var location : String? = null
    var profilePic : String? = null
    var nickname : String? = null

    var btnRecommend : String? = null
    var recommendCount : Int = 0
    var btnComment : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.footprint_dialog, container, false)
        return view.rootView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.apply {
            /*findViewById<TextView>(R.id.footprint_dialog)?.text = main_Title*/
            footprint_dialog.text = main_Title
            /*dialog_image.text = contentPic*/
            dialog_title.text = title
            dialog_content.text = content
            dialog_location.text = location
            /*dialog_profilepic.text = profilePic*/
            dialog_nickname.text = nickname

            dialog_recommend.text = btnRecommend
            dialog_recommend.setOnClickListener() {
                //버튼이 눌려지면 추천수가 1 오르고 더 클릭할 수 없다.
                recommendCount += 1
                dialog_recommend.isClickable=false
            }
            dialog_comment.text = btnComment
        }
    }

    class FootDialogBuild() {
        //setString
        val dialog = FootDialog()

        fun setDialogTitle(dialog_title: String): FootDialogBuild {
            dialog.main_Title = dialog_title
            return this
        }

        fun setContentPic(content_pic: String): FootDialogBuild {
            dialog.contentPic = content_pic
            return this
        }

        fun setTitle(title: String): FootDialogBuild {
            dialog.title = title
            return this
        }

        fun setContent(content: String): FootDialogBuild {
            dialog.content = content
            return this
        }

        fun setLocation(location: String): FootDialogBuild {
            dialog.location = location
            return this
        }

        fun setProfilePic(pic: String): FootDialogBuild {
            dialog.profilePic = pic
            return this
        }

        fun setNickname(nickname: String): FootDialogBuild {
            dialog.nickname= nickname
            return this
        }

        fun setRecommend(recommend : String) : FootDialogBuild {
            dialog.btnRecommend = recommend
            return this
        }

        fun setComment(comment : String) : FootDialogBuild {
            dialog.btnComment = comment
            return this
        }

        fun create() : FootDialog {
            return dialog
        }
    }
}