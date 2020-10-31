package com.gachon.footprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.alert_custom_dialog.view.*

class AlertCustomDialog : DialogFragment() {

    var title: String? = null
    var alertContext: String? = null
    var positiveButton: String? = null
    var negativeButton: String? = null
    var listener: AlertCustomDialogListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                listener?.clickPositive()
            }
            dialog_alert_negative.text = negativeButton
            dialog_alert_negative.setOnClickListener() {
                dismiss()
                listener?.clickNegative()
            }
        }
    }

    class AlertCustomDialogBuild() {

        val dialog = AlertCustomDialog()

        fun setTitle(title: String): AlertCustomDialogBuild {
            dialog.title = title
            return this
        }

        fun setAlertContext(context: String): AlertCustomDialogBuild {
            dialog.alertContext = context
            return this
        }

        fun setPositive(positive: String): AlertCustomDialogBuild {
            dialog.positiveButton = positive
            return this
        }

        fun setNegative(negative: String): AlertCustomDialogBuild {
            dialog.negativeButton = negative
            return this
        }

        fun setButtonClick(listener: AlertCustomDialogListener): AlertCustomDialogBuild {
            dialog.listener = listener
            return this
        }

        fun create(): AlertCustomDialog {
            return dialog
        }
    }

    interface AlertCustomDialogListener {
        fun clickPositive()
        fun clickNegative()
    }
}