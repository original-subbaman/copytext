package com.subba.clipboardmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subba.clipboardmanager.R;

import java.util.List;

public class FoldersRecyclerAdapter extends RecyclerView.Adapter<FoldersRecyclerAdapter.FoldersViewHolder> {

    private List<String> mFolderList;

    public FoldersRecyclerAdapter(List<String> list){
        this.mFolderList = list;
    }

    @NonNull
    @Override
    public FoldersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list_item, parent, false);
        FoldersViewHolder foldersViewHolder = new FoldersViewHolder(view);
        return foldersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersViewHolder holder, int position) {
        holder.mFolderName.setText(mFolderList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }

    public void setFolderList(List<String> itemList){
        this.mFolderList = itemList;
        notifyDataSetChanged();
    }

    public static class FoldersViewHolder extends RecyclerView.ViewHolder{
        public TextView mFolderName;
        public FoldersViewHolder(@NonNull View itemView) {
            super(itemView);
            mFolderName = itemView.findViewById(R.id.folder_name);
        }
    }
}
