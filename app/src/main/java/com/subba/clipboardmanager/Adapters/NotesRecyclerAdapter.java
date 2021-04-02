package com.subba.clipboardmanager.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;

import java.util.List;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder> {
    public static final String TAG = "NotesRecyclerAdapter";
    private List<String> notesList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public NotesRecyclerAdapter(List<String> noteList){
        this.notesList = noteList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
        NotesViewHolder notesViewHolder = new NotesViewHolder(view, mOnItemClickListener);
        return notesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.noteText.setText(notesList.get(position));
    }

    public void setNotesList(List<String> items){
        this.notesList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        private EditText noteText;
        public NotesViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            noteText = itemView.findViewById(R.id.note_edit_txt_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        Log.d(TAG, String.valueOf(position));
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(v);
                        }
                    }
                }
            });

        }
    }
}
