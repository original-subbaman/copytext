package com.subba.clipboardmanager.Room.Entity;

import android.animation.Animator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.subba.clipboardmanager.Activities.App;
import com.subba.clipboardmanager.Activities.MainActivity;
import com.subba.clipboardmanager.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.AnimatorAdapter;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

@Entity(tableName = "clipboard_table")
public class ClipboardItem extends AbstractFlexibleItem<ClipboardItem.ClipViewHolder> implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int clipId;
    private String text;
    private String time;
    private int folderId;
    @Ignore
    private boolean isSelected;
    @Ignore
    private boolean isNote;


    @Ignore
    public ClipboardItem(String text, String time) {
        this(text, time, 0001, false, false);
    }

    @Ignore
    public ClipboardItem(String text, String time, int folderId, boolean isSelected, boolean isNote) {
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

    public void setText(String text) {
        this.text = text;
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
    public void setIsNote(boolean isNote){
        this.isNote = isNote;
    }

    @Ignore
    public boolean getIsNote(){
        return this.isNote;
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
        if(isNote){
            return R.layout.notes_list_item;
        }
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
        final String TAG = "Note";
        if(isNote){
            holder.mNote.setText(this.text);
        }else{
            holder.mText.setText(this.text);
            holder.mTime.setText(this.time);
        }
    }

    public class ClipViewHolder extends FlexibleViewHolder {
        private TextView mText;
        private TextView mTime;
        private TextView mNote;
        private CardView mCardView;
        public ClipViewHolder(@NonNull View itemView, FlexibleAdapter adapter) {
            super(itemView, adapter);
            if(isNote){
                mNote = itemView.findViewById(R.id.note_edit_txt_view);
                mCardView = itemView.findViewById(R.id.card_view_parent);
            }else{
                mText = itemView.findViewById(R.id.clip_text);
                mTime = itemView.findViewById(R.id.clip_time);
            }

        }
    }
}
