package com.android.exampleapp.kotlin.modelViews

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.exampleapp.kotlin.R
import com.android.exampleapp.kotlin.api.VibesAPIContract
import com.android.exampleapp.kotlin.utils.SharedPrefsManager
import com.vibes.vibes.Credential
import com.vibes.vibes.VibesListener

class VibesViewModel : ViewModel() {
    var token = ""
    private lateinit var api: VibesAPIContract
    val deviceID: MutableLiveData<String> = MutableLiveData()
    val authToken: MutableLiveData<String> = MutableLiveData()
    val displayLoadingBar: MutableLiveData<Boolean> = MutableLiveData()
    val deviceRegName: MutableLiveData<Int> = MutableLiveData()
    val pushRegEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val pushRegnColor: MutableLiveData<Int> = MutableLiveData()
    val pushRegName: MutableLiveData<Int> = MutableLiveData()
    val pushRegLabelColor: MutableLiveData<Int> = MutableLiveData()
    val pushRegLabel: MutableLiveData<Int> = MutableLiveData()
    var isRegistered = false
    private lateinit var sharedPrefs: SharedPrefsManager

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER DEVICE'
     */
    fun registerDeviceButtonClicked() {
        displayLoadingBar.value = true
        if (token.isEmpty()) {
            api.registerDevice(registerDeviceCallback())
        } else {
            api.unregisterDevice(unregisterDeviceCallback())
        }
    }

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER PUSH'
     */
    fun registerPushButtonClicked() {
        displayLoadingBar.value = true
        if (isRegistered) {
            api.unregisterPush(unregisterPushCallback())
        } else {
            api.registerPush(registerPushCallback(), sharedPrefs.getToken())
        }
    }
    /**
     * Callback to register a device with Vibes. This gets passed to the controller to be called
     * upon success or failure. The SDK stores the credentials locally so if multiple calls to
     * register device get triggered, the credentials are grabbed from the local storage. Upon
     * success the device id and auth token are sent back as part of the credential object.
     */
    private fun registerDeviceCallback(): VibesListener<Credential> {
        return object : VibesListener<Credential> {
            override fun onSuccess(credential: Credential) {
                token = "[token]"
                deviceID.value = credential.deviceID
                authToken.value = credential.authToken
                deviceRegName.value = R.string.btn_unregister_device
                pushRegEnabled.value = true
                pushRegnColor.value = R.color.vibesButtonColor
                displayLoadingBar.value = false
            }

            override fun onFailure(error: String) {
                displayLoadingBar.value = false
            }
        }
    }
    /**
     * Callback to unregister a device with Vibes. The callback gets passed to the controller to be
     * called upon success or failure. If the device was not unregistered from push notifications
     * prior to the unregister device request, the device will also be unregistered from push
     * notifications. When the unregister device is successful, the local credentials stored during the
     * device registration are deleted.
     */
    private fun unregisterDeviceCallback(): VibesListener<Void> {
        return object : VibesListener<Void> {
            override fun onSuccess(value: Void?) {
                token = ""
                isRegistered = false
                deviceID.value = "[Not Registered]"
                authToken.value = "[Not Registered]"
                deviceRegName.value = R.string.btn_register_device
                pushRegEnabled.value = false
                pushRegnColor.value = R.color.vibesDisabledButtonColor
                pushRegName.value = R.string.btn_register_push
                pushRegLabelColor.value = R.color.red
                pushRegLabel.value = R.string.not_registered
                displayLoadingBar.value = false
            }

            override fun onFailure(error: String) {
                displayLoadingBar.value = false
            }
        }
    }

    /**
     * Callback to register push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device is ready to receive
     * push notifications.
     */
    private fun registerPushCallback(): VibesListener<Void> {
        return object : VibesListener<Void> {
            override fun onSuccess(value: Void?) {
                isRegistered = true
                pushRegName.value = R.string.btn_unregister_push
                pushRegLabelColor.value = R.color.green
                pushRegLabel.value = R.string.registered
                displayLoadingBar.value = false
            }

            override fun onFailure(errorText: String) {
                displayLoadingBar.value = false
            }
        }
    }

    /**
     * Callback to unregister push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device will no longer
     * receive push notifications.
     */
    private fun unregisterPushCallback(): VibesListener<Void> {
        return object : VibesListener<Void> {
            override fun onSuccess(value: Void?) {
                isRegistered = false
                pushRegName.value = R.string.btn_register_push
                pushRegLabelColor.value = R.color.red
                pushRegLabel.value = R.string.not_registered
                displayLoadingBar.value = false
            }

            override fun onFailure(errorText: String) {
                displayLoadingBar.value = false
            }
        }
    }

    /**
     * Getters/Setters
     */
    fun setAPI(api: VibesAPIContract) {
        this.api = api
    }

    fun setSharedPrefs(sharedPrefs: SharedPrefsManager) {
        this.sharedPrefs = sharedPrefs
    }
}
