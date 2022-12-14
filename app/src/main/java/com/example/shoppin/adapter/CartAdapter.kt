package com.example.shoppin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppin.R
import com.example.shoppin.eventbus.UpdateCartEvent
import com.example.shoppin.model.CartModel
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus

class CartAdapter(
    private val context: Context,
    private val cartModelList: List<CartModel>
): RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {



    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnMinus:ImageView?= null
        var btnPlus:ImageView?= null
        var imageView:ImageView?= null
        var txtName:TextView?= null
        var txtPrice:TextView?= null
        var txtQuantity:TextView?= null

        init {
            btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_cart_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].image)
            .into(holder.imageView!!)

        holder.txtName!!.text = StringBuilder().append(cartModelList[position].name)
        holder.txtPrice!!.text = StringBuilder("EGP").append(cartModelList[position].price)
        holder.txtQuantity!!.text = StringBuilder("").append(cartModelList[position].quantity)

        //handle click, Minus
        holder.btnMinus!!.setOnClickListener{_ -> minusCartItem(holder, cartModelList[position])}

        //handle click, Plus
        holder.btnPlus!!.setOnClickListener{_ -> plusCartItem(holder, cartModelList[position])}



    }

    private fun minusCartItem(holder: CartAdapter.MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1)
        {
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toFloat()

            holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
            updateFirebase(cartModel)

        }

    }

    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance()
            .getReference()
            .child("UNIQUE_USER_ID") //get from firebase authenticator
            .setValue(cartModel)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }

}