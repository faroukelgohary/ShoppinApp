package com.example.shoppin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppin.model.ProductModel
import com.example.shoppin.R
import com.example.shoppin.eventbus.UpdateCartEvent
import com.example.shoppin.listener.ICartLoadListener
import com.example.shoppin.listener.IRecyclerClickListener
import com.example.shoppin.model.CartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.getDefault
import java.net.*
import java.util.*
import kotlin.collections.HashMap


class ProductAdapter(
    private val contect: Context,
    private val list: List<ProductModel>,
    private val cartListener: ICartLoadListener

):RecyclerView.Adapter<ProductAdapter.MyProductViewHolder>() {
    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
         var imageView: ImageView?=null
         var txtName:TextView?=null
         var txtPrice:TextView?=null

        private var clickListener:IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener)
        {
            this.clickListener = clickListener;
        }

          init {
              imageView = itemView.findViewById(R.id.imageView) as ImageView;

              txtName = itemView.findViewById(R.id.txtName) as TextView;
              txtPrice = itemView.findViewById(R.id.txtPrice) as TextView;

              itemView.setOnClickListener(this)
          }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(LayoutInflater.from(contect).inflate(R.layout.layout_product_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        Glide.with(contect).load(list[position].image).into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price)

        holder.setClickListener(object:IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }

        })
    }

    private fun addToCart(productModel: ProductModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID") // get from firebase authenticator

        userCart.child(productModel.key!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) // if item is already in the cart, just update
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()

                        cartModel!!.quantity = cartModel!!.quantity+1;
                        updateData["quantity"] = cartModel!!.quantity;
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()

                        userCart.child(productModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
//                                EventListener.getDefault().postSticky(UpdateCartEvent())
//                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)}
                    }
                    else // if item is not in the cart, then add new
                    {
                        val cartModel = CartModel()
                        cartModel.key = productModel.key
                        cartModel.name = productModel.name
                        cartModel.image = productModel.image
                        cartModel.price = productModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = productModel.price!!.toFloat()


                        userCart.child(productModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
//                                EventListener.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
