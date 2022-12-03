package com.example.shoppin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.shoppin.databinding.ActivityProfileBinding
import com.example.shoppin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    // ViewBinding
    private lateinit var binding: ActivityProfileBinding

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
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Profile"
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowHomeEnabled(true)

        //intialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, sign out
        binding.signOutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        //handle click, map
        binding.mapBtn.setOnClickListener {
            goMap()
        }

        //handle click, buyer
        binding.buyerFrag.setOnClickListener {
            replaceFragment(BuyerFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun goMap() {
        startActivity(Intent(this, MapsActivity::class.java))
        onPause()
    }

    private fun checkUser() {
        //if user is already signed in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user is already signed in, get user info
            val email = firebaseUser.email

            //set to text view
            binding.emailTv.text = email
        }
        else{
            //user is null, user is not signed in, go to sign in activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }
}