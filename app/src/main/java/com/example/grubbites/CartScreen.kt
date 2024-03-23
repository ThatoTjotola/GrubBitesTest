package com.example.grubbites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.grubbites.databinding.ActivityCartScreenBinding
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class CartScreen : AppCompatActivity() {
    private lateinit var binding: ActivityCartScreenBinding
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize all this nonsense
        // Initialize Stripe PaymentConfiguration again cause wow
        PaymentConfiguration.init(applicationContext, "pk_test_51OGbbcKmvrHpopBQi2kJUde9zyrB4ZhTbyDZ5H3L6kbdRt8RIBIBGUBjsZm7BM6DlVsmgg7va97QRNixVneIiNCv00trsLOJ7a")

        stripe = Stripe(this, PaymentConfiguration.getInstance(this).publishableKey)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        // Fetch Payment Intent So We Can do the things
        fetchPaymentIntent()

        binding.btnConfirmOrder.setOnClickListener {

            onConfirmOrderClicked()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeScreen::class.java))
                    true
                }
                R.id.cart -> {

                    true
                }
                R.id.profile -> true
                R.id.search -> true
                else -> false
            }
        }

        val selectedItem = SelectedItemHolder.selectedItem

        if(selectedItem!= null){
            binding.selectedTitle.text = "Title: ${selectedItem.title}"
            binding.selectedPrice.text = "Price: ${selectedItem.price}"
            binding.selectedDesc.text = "Description: ${selectedItem.descrption}"
            Glide.with(this).load(selectedItem.imageDownloadUrl).into(binding.selectedImage)

            binding.tvPrice.text = "${selectedItem.price}"
        }
    }

    private fun fetchPaymentIntent() {
        val url = "http://10.0.2.2:4242/create-payment-intent"

        val selectedItem = SelectedItemHolder.selectedItem
        if (selectedItem != null) {
            val itemJson = JSONObject().apply {
                put("id", selectedItem.title)
                put("price", selectedItem.price)
            }

            val shoppingCartContent = """
            {
                "items": [$itemJson]
            }
        """

            val mediaType = "application/json; charset=utf-8".toMediaType()

            val body = shoppingCartContent.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle failure
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()
                        paymentIntentClientSecret = responseJson.getString("clientSecret")

                        // Enable Confirm Order button after paymentIntentClientSecret is initialized
                        runOnUiThread { binding.btnConfirmOrder.isEnabled = true }
                    } else {
                        // Handle unsuccessful response
                    }
                }
            })
        } else {
            showToast("No item selected.")
        }
    }
     private fun onConfirmOrderClicked() {


        if (::paymentIntentClientSecret.isInitialized) {
            // Present PaymentSheet when Pay  is clicked
            val configuration = PaymentSheet.Configuration("BigBites, Inc.")

            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
        } else {
            showToast("Payment information is not available.Probably didn't launch the server like i said")
        }
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                // Payment successful
                showToast("Payment complete!")
                //Redirect too the necessary page please
                //add those intents here
                intent = Intent(this, DeliveryScreen::class.java)
                startActivity(intent)
                //We need too launch server so its constantly running
                //confirmOrderOnServer()
            }
            is PaymentSheetResult.Canceled -> {
                showToast("Payment canceled by the user.")
            }
            is PaymentSheetResult.Failed -> {
                showToast("Payment failed. ${paymentResult.error?.localizedMessage}")
            }
        }
    }

    //This was just for testinging purposes


    private fun confirmOrderOnServer() {
        val url = "http://10.0.2.2:4242/confirm-order"

        // Create a JSON object with relevant data for order confirmation
        //further details about order
        val orderData = JSONObject().apply {
            put("items", listOf("xl-tshirt"))  // Adjust based on your actual order data
            put("paymentIntentClientSecret", paymentIntentClientSecret)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = orderData.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showAlert("Failed to confirm order", "Error: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    showToast("Order confirmed successfully!")
                    // You may navigate to the next screen or perform additional actions
                } else {
                    showAlert("Failed to confirm order", "Error: ${response.message}")
                }
            }
        })
    }
    private fun showToast(message: String) {
        runOnUiThread {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showAlert(title: String, message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
