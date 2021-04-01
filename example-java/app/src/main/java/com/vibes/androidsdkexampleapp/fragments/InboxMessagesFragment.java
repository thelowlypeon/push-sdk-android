package com.vibes.androidsdkexampleapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.activities.MainActivity;
import com.vibes.androidsdkexampleapp.model.MessagesAdapter;
import com.vibes.vibes.InboxMessage;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link InboxMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxMessagesFragment extends Fragment implements MessagesAdapter.InboxMessageClickListener {
    private MessagesAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public InboxMessagesFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InboxMessagesFragment.
     */
    public static InboxMessagesFragment newInstance() {
        InboxMessagesFragment fragment = new InboxMessagesFragment();
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
        View view = inflater.inflate(R.layout.fragment_inbox_messages, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        progressBar = view.findViewById(R.id.progress_circular);

        recyclerView = view.findViewById(R.id.inbox_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchMessages();
        return view;
    }

    private void fetchMessages() {
        progressBar.setVisibility(View.VISIBLE);
        Vibes.getInstance().fetchInboxMessages(new VibesListener<Collection<InboxMessage>>() {
            @Override
            public void onSuccess(Collection<InboxMessage> value) {
                adapter = new MessagesAdapter(getContext(), new ArrayList<>(value));
                adapter.setInboxMessageClickListener(InboxMessagesFragment.this::onInboxMessageClick);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorText) {
                progressBar.setVisibility(View.GONE);
                Log.d("InboxActivity", "Error fetching Message:  "+errorText);
            }
        });
    }

    @Override
    public void onInboxMessageClick(View view, int position) {
        InboxMessage message = adapter.getMessage(position);
        if (message.getDetail() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MainActivity.INBOX_MESSAGE_KEY, message);
            Fragment fragment = new InboxDetailFragment();
            fragment.setArguments(bundle);
            loadFragment(fragment);
        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}