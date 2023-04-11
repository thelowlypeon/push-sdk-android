package com.vibes.androidsdkexampleapp.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.api.VibesAPI;
import com.vibes.androidsdkexampleapp.model.SharedPrefsManager;
import com.vibes.androidsdkexampleapp.modelViews.VibesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VibesMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VibesMainFragment extends Fragment {
    @BindView(R.id.deviceIdView) TextView deviceIdView;
    @BindView(R.id.authTokenView) TextView authTokenView;
    @BindView(R.id.registeredLabelView) TextView registeredLabelView;
    @BindView(R.id.deviceRegBtn) Button deviceRegBtn;
    @BindView(R.id.pushRegBtn) Button pushRegBtn;
    @BindView(R.id.loadingBar) ProgressBar loadingBar;
    private VibesViewModel vibesVM;

    public VibesMainFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment VibesMainFragment.
     */
    public static VibesMainFragment newInstance() {
        VibesMainFragment fragment = new VibesMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vibes_main, container, false);
        ButterKnife.bind(this, view);
        vibesVM = new ViewModelProvider(this).get(VibesViewModel.class);
        vibesVM.setAPI(new VibesAPI(getContext()));
        vibesVM.setSharedPrefs(SharedPrefsManager.getInstance(getContext()));
        setupSubscribers();
        return view;
    }

    private void setupSubscribers() {
        final Observer<Boolean> observerDisplayLoadingBar = isVisible ->
                loadingBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        final Observer<String> observerDeviceIdName = value -> deviceIdView.setText(value);
        final Observer<String> observerAuthToken = token -> authTokenView.setText(token);
        final Observer<Integer> observerDeviceRegName = value -> deviceRegBtn.setText(value);
        final Observer<Boolean> observerPusRegEnabled = value -> pushRegBtn.setEnabled(value);
        final Observer<Integer> observerPushRegColor = color ->
                pushRegBtn.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        final Observer<Integer> observerPushRegText = text -> pushRegBtn.setText(text);
        final Observer<Integer> observerPushRegLabelColor = color ->
                registeredLabelView.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        final Observer<Integer> observerRegisterLabel = text -> registeredLabelView.setText(text);

        vibesVM.getDisplayLoadingBarVisible().observe(getViewLifecycleOwner(), observerDisplayLoadingBar);
        vibesVM.getDeviceIDLabelValue().observe(getViewLifecycleOwner(), observerDeviceIdName);
        vibesVM.getAuthTokenLabelValue().observe(getViewLifecycleOwner(), observerAuthToken);
        vibesVM.getDeviceRegButtonName().observe(getViewLifecycleOwner(), observerDeviceRegName);
        vibesVM.getPushRegButtonEnabled().observe(getViewLifecycleOwner(), observerPusRegEnabled);
        vibesVM.getPushRegButtonColor().observe(getViewLifecycleOwner(), observerPushRegColor);
        vibesVM.getPushRegButtonName().observe(getViewLifecycleOwner(), observerPushRegText);
        vibesVM.getPushRegLabelColor().observe(getViewLifecycleOwner(), observerPushRegLabelColor);
        vibesVM.getPushRegLabelValue().observe(getViewLifecycleOwner(), observerRegisterLabel);
    }

    @OnClick(R.id.deviceRegBtn)
    public void RegDeviceClicked(View view) {
        vibesVM.registerDeviceButtonClicked();
    }

    @OnClick(R.id.pushRegBtn)
    public void RegPushClicked(View view) {
        vibesVM.registerPushButtonClicked();
    }
}
