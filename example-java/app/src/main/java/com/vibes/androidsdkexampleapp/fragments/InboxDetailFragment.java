package com.vibes.androidsdkexampleapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.activities.MainActivity;
import com.vibes.vibes.InboxMessage;

import java.text.DateFormat;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InboxDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxDetailFragment extends Fragment {
    private InboxMessage inboxMessage;

    public InboxDetailFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment InboxDetailFragment.
     */
    public static InboxDetailFragment newInstance() {
        InboxDetailFragment fragment = new InboxDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            inboxMessage = (InboxMessage) bundle.getSerializable(MainActivity.INBOX_MESSAGE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_detail, container, false);
        TextView subject = view.findViewById(R.id.subject);
        TextView expirationDate = view.findViewById(R.id.expires_at);
        ImageView detailImage = view.findViewById(R.id.imageIcon);
        TextView content = view.findViewById(R.id.content);

        subject.setText(inboxMessage.getSubject());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        expirationDate.setText(dateFormat.format(inboxMessage.getExpirationDate()));
        Picasso.get().load(inboxMessage.getDetail()).into(detailImage);
        content.setText(inboxMessage.getContent());

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbarTitle.setText("Inbox Detail");

        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Objects.requireNonNull(getActivity()).onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
