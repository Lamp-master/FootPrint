package com.gachon.footprint

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
    private var fbfirestore: FirebaseFirestore? = null
    var userInfo = ModelUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            createEmail()
            //loginEmail() 나중에 loginEmail()로 체인지
        }

        account_login_button.setOnClickListener {
            // 액티비티 팝업 창이 뜬다.
            // 다른 레이아웃으로 넘어간다.
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("425957488645-nea69jaa49iv007nkcv4qs7co6pmv3ue.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // printHashKey()
        callbackManager = CallbackManager.Factory.create()
        // 페이스북 Hash 값 : ZNHQWY2e5GfJJWjgerEPCatjaTI=

        //현근 - 구글/페북 이메일,uid 정보 Firestore 전송
        if (true) {
            userInfo.userId = auth?.uid
            userInfo.userEmail = auth?.currentUser?.email
            fbfirestore?.collection("User")?.document(auth?.uid.toString())?.set(userInfo)
        }
    }

    //region facebook hashkey
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
    //endregion

    //region google login method
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result?.isSuccess!!) {
                var account = result?.signInAccount
                // 두번째
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
                    //login
                    moveMainPage(task.result?.user)
                } else {
                    //에러 메세지
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

                }
            }
    }

    //0930현근-회원가입과 회원로그인 이분화함
    //1. 이미 저장된 이메일을 이미 사용하고 있는 에러로 받아들임
    //2. 로그인 화면 : 회원로그인만/회원가입레이아웃을 추가 : 회원가입이 자연스러울 듯
    //연동 성공
    fun signInWithEmailAndPassword() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //회원가입성공
                val User = auth?.currentUser
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                moveMainPage(task.result?.user)
            } else if (task.exception?.message.isNullOrEmpty()) {
                // 에러 메세지 출력(양식오류/빈칸/DB내 ID 존재하지 않음)
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                createEmail()
                // 회원가입
            }
        }
    }


    //이메일 계정 생성
    private fun createEmail() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //계정생성 완료 후 메인페이지
                var user = auth?.currentUser
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                moveMainPage(task.result?.user)
            } else {
                //에러 메세지(중복 이메일/빈 칸/양식오류)
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    //이메일 로그인
    private fun loginEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                //인증 성공
                Toast.makeText(this, "인증성공", Toast.LENGTH_SHORT).show()
                var user = auth?.currentUser
                moveMainPage(it.result?.user)
            } else {
                //인증 실패시
                Toast.makeText(this, "인증실패", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}