package com.example.sikjipsa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sikjipsa.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

//    Initializing
    lateinit var binding: ActivityLoginBinding
    private lateinit var mDBRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
//    Google login sign code
    private var RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Initializing authentication
        auth = Firebase.auth

//        Initializing realtime database
        mDBRef = Firebase.database.reference

//        Setting request token for google login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initGoogleLoginButton()

        //Email Login button event
        binding.loginBtn.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            login(email, password)
        }

        //Register button event, you are moving to SignUpActivity
        binding.signup.setOnClickListener {
            val intent: Intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    //Function for automatic Login for quicker access
    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

//    If you click google sign in button
    private fun initGoogleLoginButton() {
        google_sign_in_button.setOnClickListener {
            signIn()
        }
    }

    //    If you login through google account. Inputting specific code.
    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

//    If you successfully log in app
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)!!
//            Success login, you are moving to MainActivity
            if (result.isSuccess) {
                var accout = result.signInAccount
                firebaseAuthWithGoogle(accout)
                val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
            } else {
//                Fail Login
                Toast.makeText(this, "Something is wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
//            Success login
                } else {
//                Fail Login
                }
            }
    }

        //    Email Log in
    private fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Success login, you are moving to MainActivity
                    val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
//                Fail Login

                    Toast.makeText(this, "Something is wrong!", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "Error: ${task.exception}")
                }
            }
    }

//    Maintaining automatic login status
   fun moveMainPage(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }


}