package com.android.exampleapp.kotlin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.exampleapp.kotlin.api.VibesAPI
import com.android.exampleapp.kotlin.modelViews.VibesViewModel
import com.android.exampleapp.kotlin.utils.SharedPrefsManager

class MainActivity : AppCompatActivity() {
    @BindView(R.id.deviceIdView)
    internal lateinit var deviceIdView: TextView
    @BindView(R.id.authTokenView)
    internal lateinit var authTokenView: TextView
    @BindView(R.id.registeredLabelView)
    internal lateinit var registeredLabelView: TextView
    @BindView(R.id.deviceRegBtn)
    internal lateinit var deviceRegBtn: Button
    @BindView(R.id.pushRegBtn)
    internal lateinit var pushRegBtn: Button
    @BindView(R.id.loadingBar)
    internal lateinit var loadingBar: ProgressBar
    private lateinit var vibesVM: VibesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)
        // ViewModel
        vibesVM = ViewModelProviders.of(this).get(VibesViewModel::class.java)
        vibesVM.setAPI(VibesAPI(this))
        vibesVM.setSharedPrefs(SharedPrefsManager.getInstance(applicationContext))
        setupSubscribers()
    }

    private fun setupSubscribers() {
        val observerDisplayLoadingBar = Observer<Boolean> { isVisible -> loadingBar.visibility = if (isVisible!!) View.VISIBLE else View.GONE }
        val observerDeviceIdName = Observer<String> { value -> deviceIdView.text = value }
        val observerAuthToken = Observer<String> { token -> authTokenView.text = token }
        val observerDeviceRegName = Observer<Int> { value -> deviceRegBtn.setText(value!!) }
        val observerPushRegEnabled = Observer<Boolean> { value -> pushRegBtn.isEnabled = value!! }
        val observerPushRegColor = Observer<Int> { color -> pushRegBtn.setBackgroundColor(ContextCompat.getColor(this, color!!)) }
        val observerPushRegText = Observer<Int> { text -> pushRegBtn.setText(text!!) }
        val observerPushRegLabelColor = Observer<Int> { color -> registeredLabelView.setBackgroundColor(ContextCompat.getColor(this, color!!)) }
        val observerRegisterLabel = Observer<Int> { text -> registeredLabelView.setText(text!!) }

        vibesVM.displayLoadingBar.observe(this, observerDisplayLoadingBar)
        vibesVM.deviceID.observe(this, observerDeviceIdName)
        vibesVM.authToken.observe(this, observerAuthToken)
        vibesVM.deviceRegName.observe(this, observerDeviceRegName)
        vibesVM.pushRegEnabled.observe(this, observerPushRegEnabled)
        vibesVM.pushRegnColor.observe(this, observerPushRegColor)
        vibesVM.pushRegName.observe(this, observerPushRegText)
        vibesVM.pushRegLabelColor.observe(this, observerPushRegLabelColor)
        vibesVM.pushRegLabel.observe(this, observerRegisterLabel)
    }

    @OnClick(R.id.deviceRegBtn)
    fun RegDeviceClicked() {
        vibesVM.registerDeviceButtonClicked()
    }

    @OnClick(R.id.pushRegBtn)
    fun RegPushClicked() {
        vibesVM.registerPushButtonClicked()
    }
}
