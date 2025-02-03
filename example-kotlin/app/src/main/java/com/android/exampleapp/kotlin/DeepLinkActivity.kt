package com.android.exampleapp.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vibes.vibes.PushPayloadParser
import com.vibes.vibes.Vibes
import org.json.JSONException


class DeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_link)

        val pushMap =
            intent.getSerializableExtra(Vibes.VIBES_REMOTE_MESSAGE_DATA) as HashMap<*, *>?
        val payload = PushPayloadParser(pushMap as MutableMap<String, String>?)
        //this is for tracking which push messages have been opened by the user
        Vibes.getInstance().onPushMessageOpened(payload, this.applicationContext)
        try {
            val orderId = payload.customClientData.getString("orderId")
            //fetch the order with the above orderId and then render the view.
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
