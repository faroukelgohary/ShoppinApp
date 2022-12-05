package com.example.shoppin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.shoppin.databinding.ActivityLoginBinding
import com.example.shoppin.milestone1.MultiplyActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityLoginBinding

    // ActionBar
    private lateinit var actionBar: ActionBar

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    // FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Sign in"

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Signing In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //initialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click. open register activity
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        //handle click, begin sign in
        binding.loginBtn.setOnClickListener{
            //before signing in, validate data
            validateData()
        }

        //handle enter key, begin sign in
        binding.passwordEt.setOnClickListener {
            myEnter()
        }
        //handle click, calculate
        binding.multiBtn.setOnClickListener {
            startActivity(Intent(this, MultiplyActivity::class.java))
            finish()
        }

    }

    private fun myEnter() {
        binding.passwordEt.setOnKeyListener(View.OnKeyListener {v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                validateData()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        if (TextUtils.isEmpty(email)) {
            //no email entered
            binding.emailEt.error = "Please enter email"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email format
            binding.emailEt.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)){
            //no password entered
            binding.passwordEt.error = "Please enter password"
        }
        else{
            //data is validated, begin sign in
            firebaseSignin()
        }

    }

    private fun firebaseSignin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //sign in success
                progressDialog.dismiss()
                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Signed in as $email", Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                //sign in failure
                progressDialog.dismiss()
                Toast.makeText(this, "Sign in failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //if user is already signed in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user is already signed in
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {}

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }
}