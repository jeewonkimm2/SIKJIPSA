package com.example.sikjipsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sikjipsa.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    var googleSignInClient: GoogleSignInClient? = null
//    구글 로그인 리퀘스트 코드
    var GOOGLE_LOGIN_CODE = 1004

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this,gso)


//        google login button
//        binding.googleSignInButton.setOnClickListener {
//            googleLogin()
//        }

//        login button
        binding.loginBtn.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            login(email, password)
        }


//        Register Button
        binding.signup.setOnClickListener {
            val intent: Intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
    }


//    fun googleLogin(){
//        var signInIntent = googleSignInClient?.signInIntent
//        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode==GOOGLE_LOGIN_CODE){
////            구글에서 주는 결과값
//            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
//            if(result!!.isSuccess){
////                성공하면 firebase에 넘기기
//                var account = result.signInAccount
//                firebaseAuthWithGoogle(account)
//
//            }
//        }
//    }

//    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
//        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
//        mAuth?.signInWithCredential(credential)
//            ?.addOnCompleteListener {
//                task ->
//                    if(task.isSuccessful){
//                        val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
//                        startActivity(intent)
//                    }else{
//                        Toast.makeText(this, "Something is wrong!", Toast.LENGTH_LONG).show()
//                    }
//            }
//    }

    private fun login(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Something is wrong!", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "Error: ${task.exception}")

                }
            }
    }
}