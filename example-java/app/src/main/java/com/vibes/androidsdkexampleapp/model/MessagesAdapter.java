package com.vibes.androidsdkexampleapp.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vibes.androidsdkexampleapp.R;
import com.vibes.vibes.InboxMessage;

import java.text.DateFormat;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<InboxMessage> messages;
    private LayoutInflater layoutInflater;
    private InboxMessageClickListener inboxMessageClickListener;
    private Context context;

    public MessagesAdapter(Context context, List<InboxMessage> messages) {
        this.messages = messages;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.inbox_messages_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxMessage inboxMessage = messages.get(position);
        holder.subject.setText(inboxMessage.getSubject());
        holder.content.setText(inboxMessage.getContent());
        Picasso.get().load(inboxMessage.getIconImage()).into(holder.iconImage);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        holder.expiresAt.setText(dateFormat.format(inboxMessage.getExpirationDate()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subject;
        TextView content;
        TextView expiresAt;
        ImageView iconImage;
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            subject = view.findViewById(R.id.subject);
            content = view.findViewById(R.id.content);
            iconImage = view.findViewById(R.id.imageIconView);
            expiresAt = view.findViewById(R.id.expires_at);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(inboxMessageClickListener != null) inboxMessageClickListener.onInboxMessageClick(view, getAdapterPosition());
        }
    }

    public InboxMessage getMessage(int id) {
        return messages.get(id);
    }

    public void setInboxMessageClickListener(InboxMessageClickListener inboxMessageClickListener) {
        this.inboxMessageClickListener = inboxMessageClickListener;
    }
    public interface InboxMessageClickListener {
        void onInboxMessageClick(View view, int position);
    }
}
