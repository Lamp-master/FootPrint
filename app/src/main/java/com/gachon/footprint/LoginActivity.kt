package com.gachon.footprint

import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.gachon.footprint.data.ModelUser
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var GOOGLE_LOGIN_CODE = 9001
    private var callbackManager: CallbackManager? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        // configure Google Sign In
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("425957488645-nea69jaa49iv007nkcv4qs7co6pmv3ue.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        email_login_button.setOnClickListener {
            loginEmail()
        }

        sign_up_bun.setOnClickListener {
            startActivity(Intent(this, UserEnterActivity::class.java))
        }

        //구글 버튼 클릭시 구글 계정 인증 Activity 보여짐
        google_sign_in_button.setOnClickListener {
            var signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        }

        facebook_login_button.setOnClickListener {
            facebookLogin()
        }
        callbackManager = CallbackManager.Factory.create()
    }

    private fun addFireStore() {
        var userInfo = ModelUser()
        userInfo.uid = auth?.uid
        userInfo.email = auth?.currentUser?.email
        db.collection("User").document(auth?.uid.toString()).set(userInfo)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show() }
    }

    //이메일 로그인
    private fun loginEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                //인증 성공
                Toast.makeText(this, "로그인성공", Toast.LENGTH_SHORT).show()
                moveMainPage(it.result?.user)
            } else {
                //인증 실패시
                Toast.makeText(this, "로그인실패", Toast.LENGTH_SHORT).show()

            }
        }
    }

    //region Google Login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result?.isSuccess!!) {
                var account = result?.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()

            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        // account 안에 있는 토큰 아이디 넘김
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addFireStore()
                    moveMainPage(task.result?.user)
                } else {
                    //에러 메세지
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

                }
            }
    }
    //endregion

    //region facebook Login
    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    // 페이스북 2번째
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })
    }
    //endregion

    //region facebook token handler
    fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //페이스북 3번쨰
                    //login
                    moveMainPage(task.result?.user)
                } else {
                    //에러 메세지
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

                }
            }
    }
    //endregion
    // start on_start_check_user 유저가 앱에 이미 구글 로그인을 했는지 확인
    /* override fun onStart() {
         super.onStart()
         val account = GoogleSignIn.getLastSignedInAccount(this)
         if(account !==null){ // 이미 로그인 되어있을시 바로 메인 액티비티 이동
             startActivity(Intent(this, MainActivity::class.java))
         }
     }*/
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}