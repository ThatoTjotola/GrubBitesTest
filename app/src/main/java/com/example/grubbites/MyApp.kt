package com.example.grubbites

import android.app.Application
import com.stripe.android.PaymentConfiguration

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51OGbbcKmvrHpopBQi2kJUde9zyrB4ZhTbyDZ5H3L6kbdRt8RIBIBGUBjsZm7BM6DlVsmgg7va97QRNixVneIiNCv00trsLOJ7a"
        )
    }
}