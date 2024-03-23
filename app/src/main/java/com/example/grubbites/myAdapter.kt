package com.example.grubbites

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

object SelectedItemHolder {
    var selectedItem: Products? = null
}

class myAdapter(private val context: android.content.Context, private val dataList:List<Products>) :
    RecyclerView.Adapter<myAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.meal_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: myAdapter.MyViewHolder, position: Int) {
        val products = dataList[position]
        holder.prodDesc.text = products.descrption
        holder.prodPrice.text = products.price
        holder.prodTitle.text = products.title

        // Load image using Glide
        Glide.with(context).load(dataList[position].imageDownloadUrl).into(holder.prodImage)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodDesc: TextView = itemView.findViewById(R.id.prodDesc)
        val prodImage: ImageView = itemView.findViewById(R.id.prodImage)
        val prodPrice: TextView = itemView.findViewById(R.id.prodPrice)
        val prodTitle: TextView = itemView.findViewById(R.id.prodTitle)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
        val fabButton:FloatingActionButton = itemView.findViewById(R.id.fabBtn)

        init {
            fabButton.setOnClickListener {
                val selectedItem = dataList[adapterPosition]
                SelectedItemHolder.selectedItem = selectedItem

                val intent = Intent(context, CartScreen::class.java)
                context.startActivity(intent)

            }
        }

    }
}


