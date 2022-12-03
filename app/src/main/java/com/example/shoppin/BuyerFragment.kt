package com.example.shoppin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class BuyerFragment : Fragment() {

    private lateinit var dbref : DatabaseReference
    private lateinit var adapter : MyAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var productsArrayList : ArrayList<Product>

    lateinit var imageId : Array<Int>
    lateinit var title : Array<String>
    lateinit var quantity : Array<Int>
    lateinit var price : Array<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buyer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = MyAdapter(productsArrayList)
        recyclerView.adapter = adapter

        productsArrayList = arrayListOf<Product>()
        getProductData()

    }

    private fun getProductData() {
        dbref = FirebaseDatabase.getInstance().getReference("Products")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (productSnapshot in snapshot.children){

                        val product = productSnapshot.getValue(Product::class.java)
                        productsArrayList.add(product!!)

                    }

                    recyclerView.adapter = MyAdapter(productsArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

//    private fun dataInitialize(){
//        productsArrayList = arrayListOf<Product>()
//
//        imageId = arrayOf(
//            R.drawable.img,
//            R.drawable.img,
//            R.drawable.img,
//            R.drawable.img
//        )
//        title = arrayOf(
//            "Brunoo",
//            "Pogbaa",
//            "Brunoooo",
//            "McTominayyy"
//        )
//        quantity = arrayOf(
//            4,4,4,4
//        )
//        price = arrayOf(
//            10,10,10,10
//        )
//
//        for(i in imageId.indices)
//        {
//            val product = Product(imageId[i], title[i], quantity[i], price[i])
//            productsArrayList.add(product)
//        }
//    }

}