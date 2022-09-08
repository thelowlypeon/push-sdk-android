package com.android.exampleapp.kotlin.fragments

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.exampleapp.kotlin.R
import com.android.exampleapp.kotlin.api.VibesAPI
import com.android.exampleapp.kotlin.modelViews.VibesViewModel
import com.android.exampleapp.kotlin.utils.SharedPrefsManager


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [VibesMainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [VibesMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class VibesMainFragment : androidx.fragment.app.Fragment() {
    @BindView(R.id.deviceIdView)
    @JvmField var deviceIdView: TextView? = null
    @BindView(R.id.authTokenView)
    @JvmField var authTokenView: TextView? = null
    @BindView(R.id.registeredLabelView)
    @JvmField var registeredLabelView: TextView? = null
    @BindView(R.id.deviceRegBtn)
    @JvmField var deviceRegBtn: Button? = null
    @BindView(R.id.pushRegBtn)
    @JvmField var pushRegBtn: Button? = null
    @BindView(R.id.loadingBar)
    @JvmField var loadingBar: ProgressBar? = null
    private var vibesVM: VibesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.vibes_main_fragment, container, false)
        ButterKnife.bind(this, view)
        vibesVM = ViewModelProvider(this).get(VibesViewModel::class.java)
        vibesVM!!.setAPI(VibesAPI(context))
        vibesVM!!.setSharedPrefs(SharedPrefsManager.getInstance(context))
        setupSubscribers()
        return view
    }

    private fun setupSubscribers() {
        val observerDisplayLoadingBar = Observer { isVisible: Boolean? -> loadingBar!!.visibility = if (isVisible!!) View.VISIBLE else View.GONE }
        val observerDeviceIdName = Observer { value: String? -> deviceIdView!!.text = value }
        val observerAuthToken = Observer { token: String? -> authTokenView!!.text = token }
        val observerDeviceRegName = Observer { value: Int? -> deviceRegBtn!!.setText(value!!) }
        val observerPusRegEnabled = Observer { value: Boolean? -> pushRegBtn!!.isEnabled = value!! }
        val observerPushRegColor = Observer { color: Int? -> pushRegBtn!!.setBackgroundColor(ContextCompat.getColor(requireContext(), color!!)) }
        val observerPushRegText = Observer { text: Int? -> pushRegBtn!!.setText(text!!) }
        val observerPushRegLabelColor = Observer { color: Int? -> registeredLabelView!!.setBackgroundColor(ContextCompat.getColor(requireContext(), color!!)) }
        val observerRegisterLabel = Observer { text: Int? -> registeredLabelView!!.setText(text!!) }
        vibesVM!!.getDisplayLoadingBarVisible().observe(viewLifecycleOwner, observerDisplayLoadingBar)
        vibesVM!!.getDeviceIDLabelValue().observe(viewLifecycleOwner, observerDeviceIdName)
        vibesVM!!.getAuthTokenLabelValue().observe(viewLifecycleOwner, observerAuthToken)
        vibesVM!!.getDeviceRegButtonName().observe(viewLifecycleOwner, observerDeviceRegName)
        vibesVM!!.getPushRegButtonEnabled().observe(viewLifecycleOwner, observerPusRegEnabled)
        vibesVM!!.getPushRegButtonColor().observe(viewLifecycleOwner, observerPushRegColor)
        vibesVM!!.getPushRegButtonName().observe(viewLifecycleOwner, observerPushRegText)
        vibesVM!!.getPushRegLabelColor().observe(viewLifecycleOwner, observerPushRegLabelColor)
        vibesVM!!.getPushRegLabelValue().observe(viewLifecycleOwner, observerRegisterLabel)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VibesMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                VibesMainFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    @OnClick(R.id.deviceRegBtn)
    fun RegDeviceClicked(view: View?) {
        vibesVM!!.registerDeviceButtonClicked()
    }

    @OnClick(R.id.pushRegBtn)
    fun RegPushClicked(view: View?) {
        vibesVM!!.registerPushButtonClicked()
    }
}
