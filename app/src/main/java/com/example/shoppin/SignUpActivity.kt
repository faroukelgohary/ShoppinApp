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
import com.example.shoppin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    // ViewBinding
    private lateinit var binding: ActivitySignUpBinding

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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowHomeEnabled(true)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating your account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //intialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click, begin signup
        binding.signUpBtn.setOnClickListener {
            //validate data
            validateData()
        }

        //handle enter key, begin sign in
        binding.passwordEt.setOnClickListener {
            myEnter()
        }

        //handle click, sign in instead
        binding.AccountTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun myEnter() {
        binding.passwordEt.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
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
        else if (password.length < 6) {
            binding.passwordEt.error = "Password must be at least 6 characters long"
        }
        else{
            //data is validated, continue sign up
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create new account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //sign up success
                progressDialog.dismiss()

                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Account created with email $email", Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                //sign up failure
                progressDialog.dismiss()
                Toast.makeText(this, "Sign Up failed dure to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {}

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }
}