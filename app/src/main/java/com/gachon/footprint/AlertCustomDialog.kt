package com.gachon.footprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.alert_custom_dialog.*
import kotlinx.android.synthetic.main.alert_custom_dialog.dialog_alert_message
import kotlinx.android.synthetic.main.alert_custom_dialog.dialog_alert_negative
import kotlinx.android.synthetic.main.alert_custom_dialog.dialog_alert_positive
import kotlinx.android.synthetic.main.alert_custom_dialog.dialog_alert_title
import kotlinx.android.synthetic.main.alert_custom_dialog.view.*
import kotlinx.android.synthetic.main.footprint_dialog.view.*

class AlertCustomDialog : DialogFragment() {

    var title : String? = null
    var alertContext : String? = null
    var positiveButton : String? = null
    var negativeButton : String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.alert_custom_dialog, container, false)
        return view.rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.apply {
            dialog_alert_title.text = title
            dialog_alert_message.text = alertContext
            dialog_alert_positive.text = positiveButton

            dialog_alert_positive.setOnClickListener() {
                dismiss()
            }
            dialog_alert_negative.text = negativeButton
            dialog_alert_negative.setOnClickListener() {
                dismiss()
            }
        }
    }

    class AlertCustomDialogBuild() {

        val dialog = AlertCustomDialog()

        fun setTitle(title : String): AlertCustomDialogBuild {
            dialog.title = title
            return this
        }

        fun setAlertContext(context: String): AlertCustomDialogBuild {
            dialog.alertContext = context
            return this
        }

        fun setPositive(positive : String) : AlertCustomDialogBuild {
            dialog.positiveButton = positive
            return this
        }

        fun setNegative(negative : String) : AlertCustomDialogBuild {
            dialog.negativeButton = negative
            return this
        }

        fun create() : AlertCustomDialog {
            return dialog
        }
    }

    fun setAlertDialog(title : String, context : String, posit : String, negat : String) {
        val alertdialog = AlertCustomDialog.AlertCustomDialogBuild()
            .setTitle(title)
            .setAlertContext(context)
            .setPositive(posit)
            .setNegative(negat)
            .create()
    }
}