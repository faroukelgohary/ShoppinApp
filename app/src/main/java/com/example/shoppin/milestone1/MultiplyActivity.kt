package com.example.shoppin.milestone1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppin.databinding.ActivityMultiplyBinding

class MultiplyActivity : AppCompatActivity() {

    // Viewbinding
    private lateinit var binding: ActivityMultiplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calculateBtn.setOnClickListener {
            multiply()
        }


    }

    private fun multiply() {
        var x = (binding.variable1).text.toString().toDouble()
        var y = (binding.variable2).text.toString().toDouble()

        val multiplication = x * y
        Toast.makeText(this, "${multiplication}", Toast.LENGTH_LONG ).show()

    }
}