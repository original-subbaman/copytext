package com.subba.clipboardmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.Folder;

import java.util.List;

public class FoldersRecyclerAdapter extends RecyclerView.Adapter<FoldersRecyclerAdapter.FoldersViewHolder> {

    private List<Folder> mFolderList;

    public FoldersRecyclerAdapter(List<Folder> list){
        this.mFolderList = list;
    }

    public FoldersRecyclerAdapter(){}

    @NonNull
    @Override
    public FoldersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list_item, parent, false);
        FoldersViewHolder foldersViewHolder = new FoldersViewHolder(view);
        return foldersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersViewHolder holder, int position) {
        holder.mFolderName.setText(mFolderList.get(position).getFolderName());
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }

    public void setFolderList(List<Folder> itemList){
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
