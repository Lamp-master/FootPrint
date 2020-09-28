package com.gachon.footprint

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_account_login.*

class AccountLoginActivity : AppCompatActivity() {
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_login)

        google_sign_in_button.setOnClickListener {
            //구글 첫번째
            (mContext as LoginActivity).googleLogin()
        }
        facebook_login_button.setOnClickListener {
            //페이스북 첫번째
            (mContext as LoginActivity).facebookLogin()
        }
    }

    fun googleFacebookLogin(context: Context?) {
        mContext = context
    }
}