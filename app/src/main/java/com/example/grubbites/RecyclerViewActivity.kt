package com.example.grubbites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.grubbites.databinding.ActivityRecyclerViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.coroutines.NonCancellable.children

class RecyclerViewActivity : AppCompatActivity() {
    private lateinit var dataList: ArrayList<Products>
    private lateinit var binding: ActivityRecyclerViewBinding
    private lateinit var adapter: myAdapter
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val gridLayoutManager = GridLayoutManager(this@RecyclerViewActivity, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false).setMessage("Loading data...")
        val dialog = builder.create()



        dataList = ArrayList()
        adapter = myAdapter(this@RecyclerViewActivity, dataList)
        binding.recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Meals")

        eventListener = databaseReference!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                dataList.clear()
                for(itemSnapshot in snapshot.children){
                    val productClass = itemSnapshot.getValue(Products::class.java)
                    if (productClass != null){
                        dataList.add(productClass)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

    }
}





