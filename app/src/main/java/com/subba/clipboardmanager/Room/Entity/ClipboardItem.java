package com.subba.clipboardmanager.Room.Entity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.subba.clipboardmanager.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

@Entity(tableName = "clipboard_table")
public class ClipboardItem extends AbstractFlexibleItem<ClipboardItem.ClipViewHolder> {

    @PrimaryKey(autoGenerate = true)
    private int clipId;
    private String text;
    private String time;
    private int folderId;
    @Ignore
    private boolean isSelected;


    @Ignore
    public ClipboardItem(String text, String time) {
        this(text, time, 0001, false);
    }

    @Ignore
    public ClipboardItem(String text, String time, int folderId, boolean isSelected) {
        this.text = text;
        this.time = time;
        this.folderId = folderId;
        this.isSelected = isSelected;
    }

    public ClipboardItem(String text, String time, int folderId) {
        this.text = text;
        this.time = time;
        this.folderId = folderId;
    }

    public void setClipId(int clipId) {
        this.clipId = clipId;
    }

    public int getClipId() {
        return this.clipId;
    }

    public String getText() {
        return this.text;
    }

    public String getTime() {
        return this.time;
    }

    public int getFolderId() {
        return this.folderId;
    }

    public void setFolderId(int folderId) { this.folderId = folderId; }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean getSelected() {
        return this.isSelected;
    }

    @Ignore
    @Override
    public boolean equals(Object o) {
        if (o instanceof ClipboardItem) {
            ClipboardItem clipboardItem = (ClipboardItem) o;
            return (this.clipId == clipboardItem.clipId);
        }
        return false;
    }

    @Ignore
    @Override
    public int getLayoutRes() {
        return R.layout.clip_list_item;
    }

    @Ignore
    @Override
    public ClipboardItem.ClipViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ClipViewHolder(view, adapter);
    }

    @Ignore
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ClipboardItem.ClipViewHolder holder, int position, List<Object> payloads) {
        holder.mText.setText(this.text);
        holder.mTime.setText(this.time);

        if(position == 0){
            holder.itemView.setBackgroundResource(R.drawable.rounded_corner_top);
        }


    }

    public class ClipViewHolder extends FlexibleViewHolder {
        public TextView mText;
        public TextView mTime;

        public ClipViewHolder(@NonNull View itemView, FlexibleAdapter adapter) {
            super(itemView, adapter);
            mText = itemView.findViewById(R.id.clip_text);
            mTime = itemView.findViewById(R.id.clip_time);
        }
    }
}
