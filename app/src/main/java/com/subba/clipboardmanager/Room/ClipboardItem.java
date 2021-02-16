package com.subba.clipboardmanager.Room;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.material.drawable.DrawableUtils;
import com.subba.clipboardmanager.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

@Entity(tableName = "clipboard_table")
public class ClipboardItem extends AbstractFlexibleItem<ClipboardItem.ClipViewHolder> {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private String time;
    private String folder;
    @Ignore
    private boolean isSelected;


    @Ignore
    public ClipboardItem(String text, String time) {
        this(text, time, "Other", false);
    }

    @Ignore
    public ClipboardItem(String text, String time, String folder, boolean isSelected) {
        this.text = text;
        this.time = time;
        this.folder = folder;
        this.isSelected = isSelected;
    }

    public ClipboardItem(String text, String time, String folder) {
        this.text = text;
        this.time = time;
        this.folder = folder;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getTime() {
        return this.time;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) { this.folder = folder; }

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
            return (this.id == clipboardItem.id);
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
