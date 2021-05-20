package com.galaxydefenders.konnex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxydefenders.konnex.R;
import com.galaxydefenders.konnex.model.Message;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;

    private List<Message> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Message obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterChat(Context context) {
        ctx = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text_content;
        public View lyt_parent;
        public View lyt_read;

        public ItemViewHolder(View v) {
            super(v);
            text_content = v.findViewById(R.id.text_content);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            lyt_read = v.findViewById(R.id.lyt_read);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == CHAT_ME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_you, parent, false);
            vh = new ItemViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Message m = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.text_content.setText(m.getContent());
            if (position == getItemCount() - 1 && m.isFromMe()) {
                vItem.lyt_read.setVisibility(View.VISIBLE);
            } else {
                vItem.lyt_read.setVisibility(View.INVISIBLE);
            }
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, m, position);
                    }
                }
            });
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).isFromMe() ? CHAT_ME : CHAT_YOU;
    }

    public void insertItem(Message item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
        if (getItemCount() > 1) notifyItemChanged(getItemCount() - 2);
    }

    public void setItems(List<Message> items) {
        this.items = items;
    }
}