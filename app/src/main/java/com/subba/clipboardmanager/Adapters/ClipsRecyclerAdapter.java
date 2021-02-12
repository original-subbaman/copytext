package com.subba.clipboardmanager.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.subba.clipboardmanager.Room.ClipboardItem;
import com.subba.clipboardmanager.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ClipsRecyclerAdapter extends RecyclerView.Adapter<ClipsRecyclerAdapter.ClipsViewHolder> {

    public  List<ClipboardItem> mClipList;
    private OnItemLongClickListener mLongClickListener;
    private AdapterView.OnItemClickListener mItemClickListener;
    private List<Integer> mSelectedClipIdList;
    private static boolean isSelectionEnabled = false;

    public interface OnItemLongClickListener{
        boolean onItemLongClick(int position);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mLongClickListener = listener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public ClipsRecyclerAdapter(ArrayList<ClipboardItem> list){
        this.mSelectedClipIdList = new ArrayList<>();
        this.mClipList = list;
    }

    @NonNull
    @Override
    public ClipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clip_list_item, parent, false);
        ClipsViewHolder clipsViewHolder = new ClipsViewHolder(view, mLongClickListener);
        return clipsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClipsViewHolder holder, int position) {
        ClipboardItem currentItem = mClipList.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null){
                    if(!isSelectionEnabled){
                        isSelectionEnabled = true;
                        setClipSelected(position);
                    }
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectionEnabled){
                    setClipSelected(position);
                }else{
                    setClipUnselected(position);
                }
            }
        });

        if(currentItem.getSelected()){
            //change background
        }else{
            //do nothing
        }
        holder.mText.setText(currentItem.getText());
        holder.mTime.setText(currentItem.getTime());

    }

    @Override
    public int getItemCount() {
        return mClipList.size();
    }

    public void setClips(List<ClipboardItem> itemList){
        this.mClipList = itemList;
        notifyDataSetChanged();
    }

    public static class ClipsViewHolder extends RecyclerView.ViewHolder {
        public TextView mText;
        public TextView mTime;
        public ClipsViewHolder(@NonNull View itemView, OnItemLongClickListener listener) {
            super(itemView);
            mText = itemView.findViewById(R.id.clip_text);
            mTime = itemView.findViewById(R.id.clip_time);


        }
    }

    public void setClipSelected(int position){
        mClipList.get(position).setSelected(true);
        notifyItemChanged(position);
    }

    public void setClipUnselected(int position){
        mClipList.get(position).setSelected(false);
        notifyItemChanged(position);
    }


}
