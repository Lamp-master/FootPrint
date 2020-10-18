package com.gachon.footprint

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
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

    var btnFirst : String? = null
    var recommendCount : Int = 0
    var btnSecond : String? = null
    var listener : customClickListener? = null

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

            dialog_btn_first.text = btnFirst
            dialog_btn_first.setOnClickListener() {
                dismiss() //다이얼로그 꺼짐
                listener?.onFirstBtnClick()
            }
            dialog_btn_second.text = btnSecond
            dialog_btn_second.setOnClickListener() {
                //버튼색이 노란색이 되는데 노란색이고싶진않으니까 다른색할꺼에요
                dialog_btn_second.setBackgroundColor(Color.parseColor("#FFF000"))
/*                dismiss()*/
                listener?.onSecondBtnClick()
            }
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

        fun setFirstBtnText(first : String) : FootDialogBuild {
            dialog.btnFirst = first
            return this
        }

        fun setSecondBtnText(second : String) : FootDialogBuild {
            dialog.btnSecond = second
            return this
        }

        fun setButtonClick(listener : customClickListener) : FootDialogBuild {
            dialog.listener = listener
            return this
        }

        fun create() : FootDialog {
            return dialog
        }
    }

    interface customClickListener {
        fun onFirstBtnClick()
        fun onSecondBtnClick()
    }
}