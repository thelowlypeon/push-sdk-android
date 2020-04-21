package com.android.exampleapp.kotlin.modelViews

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.exampleapp.kotlin.R
import com.android.exampleapp.kotlin.api.VibesAPIContract
import com.android.exampleapp.kotlin.utils.SharedPrefsManager
import com.vibes.vibes.Credential
import com.vibes.vibes.VibesListener

class VibesViewModel : ViewModel() {
    var token = ""
    private var api: VibesAPIContract? = null
    private var deviceID: MutableLiveData<String?>? = null
    private var authToken: MutableLiveData<String?>? = null
    private var displayLoadingBar: MutableLiveData<Boolean?>? = null
    private var deviceRegName: MutableLiveData<Int?>? = null
    private var pushRegEnabled: MutableLiveData<Boolean?>? = null
    private var pushRegnColor: MutableLiveData<Int?>? = null
    private var pushRegName: MutableLiveData<Int?>? = null
    private var pushRegLabelColor: MutableLiveData<Int?>? = null
    private var pushRegLabel: MutableLiveData<Int?>? = null
    var isRegistered = false
    private var sharedPrefs: SharedPrefsManager? = null

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER DEVICE'
     */
    fun registerDeviceButtonClicked() {
        getDisplayLoadingBarVisible().value = true
        if (token.isEmpty()) {
            api!!.registerDevice(registerDeviceCallback())
        } else {
            api!!.unregisterDevice(unregisterDeviceCallback())
        }
    }

    /**
     * Method called when the user clicks on the button 'REGISTER/UNREGISTER PUSH'
     */
    fun registerPushButtonClicked() {
        getDisplayLoadingBarVisible().value = true
        if (isRegistered) {
            api!!.unregisterPush(unregisterPushCallback())
        } else {
            Log.d("My Token ---->", "" + sharedPrefs!!.getToken())
            api!!.registerPush(registerPushCallback(), sharedPrefs!!.getToken())
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
                getDeviceIDLabelValue().value = credential.deviceID
                getAuthTokenLabelValue().value = credential.authToken
                Log.d("Auth Token --->>", credential.authToken)
                getDeviceRegButtonName().value = R.string.btn_unregister_device
                getPushRegButtonEnabled().value = true
                getPushRegButtonColor().value = R.color.vibesButtonColor
                getDisplayLoadingBarVisible().value = false
            }

            override fun onFailure(error: String) {
                getDisplayLoadingBarVisible().value = false
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
    private fun unregisterDeviceCallback(): VibesListener<Void?> {
        return object : VibesListener<Void?> {
            override fun onSuccess(value: Void?) {
                token = ""
                isRegistered = false
                getDeviceIDLabelValue().value = "[Not Registered]"
                getAuthTokenLabelValue().value = "[Not Registered]"
                getDeviceRegButtonName().value = R.string.btn_register_device
                getPushRegButtonEnabled().value = false
                getPushRegButtonColor().value = R.color.vibesDisabledButtonColor
                getPushRegButtonName().value = R.string.btn_register_push
                getPushRegLabelColor().value = R.color.red
                getPushRegLabelValue().value = R.string.not_registered
                getDisplayLoadingBarVisible().value = false
            }

            override fun onFailure(error: String) {
                getDisplayLoadingBarVisible().value = false
            }
        }
    }

    /**
     * Callback to register push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device is ready to receive
     * push notifications.
     */
    private fun registerPushCallback(): VibesListener<Void?> {
        return object : VibesListener<Void?> {
            override fun onSuccess(value: Void?) {
                isRegistered = true
                Log.d("Register Push", "Register Push successful")
                getPushRegButtonName().value = R.string.btn_unregister_push
                getPushRegLabelColor().value = R.color.green
                getPushRegLabelValue().value = R.string.registered
                getDisplayLoadingBarVisible().value = false
            }

            override fun onFailure(errorText: String) {
                getDisplayLoadingBarVisible().value = false
            }
        }
    }

    /**
     * Callback to unregister push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device will no longer
     * receive push notifications.
     */
    private fun unregisterPushCallback(): VibesListener<Void?> {
        return object : VibesListener<Void?> {
            override fun onSuccess(value: Void?) {
                isRegistered = false
                getPushRegButtonName().value = R.string.btn_register_push
                getPushRegLabelColor().value = R.color.red
                getPushRegLabelValue().value = R.string.not_registered
                getDisplayLoadingBarVisible().value = false
            }

            override fun onFailure(errorText: String) {
                getDisplayLoadingBarVisible().value = false
            }
        }
    }

    /**
     * Getters/Setters
     */
    fun setAPI(api: VibesAPIContract?) {
        this.api = api
    }

    fun setSharedPrefs(sharedPrefs: SharedPrefsManager?) {
        this.sharedPrefs = sharedPrefs
    }

    /**
     * LiveData
     */
    fun getDeviceIDLabelValue(): MutableLiveData<String?> {
        if (deviceID == null) {
            deviceID = MutableLiveData()
        }
        return deviceID!!
    }

    fun getAuthTokenLabelValue(): MutableLiveData<String?> {
        if (authToken == null) {
            authToken = MutableLiveData()
        }
        return authToken!!
    }

    fun getDisplayLoadingBarVisible(): MutableLiveData<Boolean?> {
        if (displayLoadingBar == null) {
            displayLoadingBar = MutableLiveData()
        }
        return displayLoadingBar!!
    }

    fun getDeviceRegButtonName(): MutableLiveData<Int?> {
        if (deviceRegName == null) {
            deviceRegName = MutableLiveData()
        }
        return deviceRegName!!
    }

    fun getPushRegButtonEnabled(): MutableLiveData<Boolean?> {
        if (pushRegEnabled == null) {
            pushRegEnabled = MutableLiveData()
        }
        return pushRegEnabled!!
    }

    fun getPushRegButtonColor(): MutableLiveData<Int?> {
        if (pushRegnColor == null) {
            pushRegnColor = MutableLiveData()
        }
        return pushRegnColor!!
    }

    fun getPushRegButtonName(): MutableLiveData<Int?> {
        if (pushRegName == null) {
            pushRegName = MutableLiveData()
        }
        return pushRegName!!
    }

    fun getPushRegLabelColor(): MutableLiveData<Int?> {
        if (pushRegLabelColor == null) {
            pushRegLabelColor = MutableLiveData()
        }
        return pushRegLabelColor!!
    }

    fun getPushRegLabelValue(): MutableLiveData<Int?> {
        if (pushRegLabel == null) {
            pushRegLabel = MutableLiveData()
        }
        return pushRegLabel!!
    }
}
