package com.FilmManager.RFID.Scanner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.FilmManager.RFID.Scanner.entity.TagInfo;
import com.FilmManager.RFID.Scanner.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<TagInfo> tagList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    public TagAdapter(List<TagInfo> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_view, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagInfo tag = tagList.get(position);
        holder.index.setText(String.valueOf(tag.getIndex()));
        holder.epc.setText(tag.getEpc());

        holder.tid.setVisibility(View.GONE);

        holder.user.setVisibility(View.GONE);

        holder.reserved.setVisibility(View.GONE);

        holder.count.setText(String.valueOf(tag.getCount()));

        if (tag.isErrorTag()) {
            holder.epc.setBackgroundColor(Color.RED);
        } else {
            holder.epc.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                return onItemLongClickListener.onItemLongClick(v, position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView index, epc, tid, user, reserved, count;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.tag_index);
            epc = itemView.findViewById(R.id.tag_epc);
            tid = itemView.findViewById(R.id.tag_tid);
            user = itemView.findViewById(R.id.tag_user);
            reserved = itemView.findViewById(R.id.tag_reserved);
            count = itemView.findViewById(R.id.tag_count);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}