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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.footprint_dialog, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.apply {
            findViewById<TextView>(R.id.footprint_dialog)?.text = main_Title
/*            findViewById<ImageView>(R.id.dialog_image)?.text = contentPic*/
            findViewById<TextView>(R.id.dialog_title)?.text = title
            findViewById<TextView>(R.id.dialog_content)?.text = content
            findViewById<TextView>(R.id.dialog_location)?.text = location
/*            findViewById<ImageView>(R.id.dialog_profilepic)?.text = profilePic*/
            findViewById<TextView>(R.id.dialog_nickname)?.text = nickname

        }
    }

    class FootDialogBuild() {

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

        fun create() : FootDialog {
            return dialog
        }
    }
}