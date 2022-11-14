package com.example.sikjipsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sikjipsa.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

//    바인딩 객체
//    Firebase 객체
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        인증 초기화

        mAuth = FirebaseAuth.getInstance()

        binding.signup.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()

            if(email.isNotEmpty()&&password.isNotEmpty()){
                Log.d("ITM", "에러")
                signUp(email,password)

            }

        }
    }
//    회원가입 : password 최소 6자
    private fun signUp(email: String, password: String){
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // success
                Toast.makeText(this, "Welcome to our app!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // fail
                Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }


}