package com.example.shoppin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val productList : ArrayList<Product>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = productList[position]

        holder.myImg.setImageResource(currentItem.titleImage)
        holder.myTitle.text = "Product: " + currentItem.title
        holder.myQuantity.text = "Quantity: " + currentItem.quantity.toString()
        holder.myPrice.text = "Price: " + currentItem.price.toString()
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myImg : ImageView = itemView.findViewById(R.id.imgView)
        val myTitle : TextView = itemView.findViewById(R.id.titleTv)
        val myQuantity : TextView = itemView.findViewById(R.id.quantityTv)
        val myPrice : TextView = itemView.findViewById(R.id.priceTv)
    }
}