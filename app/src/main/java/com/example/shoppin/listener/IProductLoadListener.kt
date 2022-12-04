package com.example.shoppin.listener

import com.example.shoppin.model.ProductModel

interface IProductLoadListener {
    // first step
    fun onProductLoadSuccess(productModelList: List<ProductModel>?)
    fun onProductLoadFailed(message:String?)

}