package com.subba.clipboardmanager.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.subba.clipboardmanager.Activities.MainActivity;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;

public class EditNoteFragment extends Fragment {

    private ClipboardItem mNote;
    private Toolbar mToolbar;
    private EditText mEditText;
    public static final String TAG = "EditNoteFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        Bundle value = getArguments();
        mNote = (ClipboardItem) value.getSerializable("note");
        mToolbar = getActivity().findViewById(R.id.toolbar);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note_fragment_layout, null);
        mEditText = view.findViewById(R.id.note_edit_text);
        mEditText.setText(mNote.getText(), TextView.BufferType.EDITABLE);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_note_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.save_note:
                Log.d(TAG, "onOptionsItemSelected: options clicked");
                saveChanges();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveChanges(){
        String changedText = mEditText.getText().toString();
        if(changedText.equals(mNote)){
            return;
        }else{
            mNote.setText(changedText);
            MainActivity.viewModel.update(mNote);
            Toast.makeText(getActivity(), R.string.note_updated, Toast.LENGTH_SHORT).show();
        }
    }


}
