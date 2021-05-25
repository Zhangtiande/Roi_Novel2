package com.example.roinovel_2.Novel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roinovel_2.DetailActivity;
import com.example.roinovel_2.R;

import java.util.List;

public class NovelAdapter extends RecyclerView.Adapter<NovelAdapter.ViewHolder> {

    private List<Novel> mNovelList = null;
    private Context context = null;

    public NovelAdapter(List<Novel> mNovelList) {
        this.mNovelList = mNovelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.novelitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.Novel.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            int position = holder.getAdapterPosition();
            Novel novel = mNovelList.get(position);
            intent.putExtra("DETAIL",novel);
            v.getContext().startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Novel novel = mNovelList.get(position);
        holder.Novel.setText(novel.string());
    }

    @Override
    public int getItemCount() {
        return mNovelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView Novel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Novel = itemView.findViewById(R.id.novelitem);

        }

    }

}
