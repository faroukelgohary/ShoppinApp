package com.example.shoppin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppin.listener.IProductLoadListener
import com.example.shoppin.model.ProductModel
import com.example.shoppin.utils.SpaceItemDecoration
import com.example.shoppin.adapter.ProductAdapter
import com.example.shoppin.eventbus.UpdateCartEvent
import com.example.shoppin.listener.ICartLoadListener
import com.example.shoppin.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), IProductLoadListener, ICartLoadListener {

    lateinit var cartLoadListener: ICartLoadListener
    lateinit var productLoadListener: IProductLoadListener


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent)
    {
        countCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        loadProductFromFirebase()
        countCartFromFirebase()

    }

    private fun countCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID") // get from firebase authenticator
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadProductFromFirebase() {
        val productModels : MutableList<ProductModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Drink").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(productSnapshot in snapshot.children){
                        val productModel = productSnapshot.getValue(ProductModel::class.java)
                        productModel!!.key = productSnapshot.key
                        productModels.add(productModel)
                    }
                    productLoadListener.onProductLoadSuccess(productModels)
                }
                else{
                    productLoadListener.onProductLoadFailed("Product items do not exist!!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                productLoadListener.onProductLoadFailed(error.message)
            }

        })
    }

    private fun init(){
        productLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_product.layoutManager = gridLayoutManager
        recycler_product.addItemDecoration(SpaceItemDecoration())

        btnCart.setOnClickListener{ startActivity(Intent(this,CartActivity::class.java) )}
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
        val adapter = ProductAdapter(this,productModelList!!,cartLoadListener)
        recycler_product.adapter = adapter

    }


    override fun onProductLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList!!) cartSum+= cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()    }

}