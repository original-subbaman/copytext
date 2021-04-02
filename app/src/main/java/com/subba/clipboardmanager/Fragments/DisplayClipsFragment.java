package com.subba.clipboardmanager.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.subba.clipboardmanager.Activities.MainActivity;
import com.subba.clipboardmanager.Activities.NotesActivity;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.Folder;

import static com.subba.clipboardmanager.Activities.MainActivity.mFolderList;

public class DisplayClipsFragment extends Fragment {

    private int folderId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        folderId = getArguments().getInt("folder_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_clips_fragment_layout, null);
        RecyclerView recyclerView = view.findViewById(R.id.notes_recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL ));
        recyclerView.setAdapter(NotesActivity.mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.notes_activity_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete:
                createDeleteWarningDialog();
                return true;
            case R.id.edit_title:
                createRenameFolderDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void createRenameFolderDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.custom_single_input_dialog, null);

        final AlertDialog dialog = builder.setView(view).create();

        Button cancelButton = view.findViewById(R.id.cancel_dialog_button);
        Button renameButton = view.findViewById(R.id.positive_dialog_button);
        EditText editText = view.findViewById(R.id.rename_folder_edit_text);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editText.getText().toString();
                if(title.length() > 0){
                    renameFolder(title);
                    dialog.dismiss();
                }else{
                    Toast.makeText(getActivity(), R.string.empty_input, Toast.LENGTH_LONG).show();
                }
            }
        });


        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        dialog.show();
    }

    private void renameFolder(String folderTitle) {
        for(Folder folder : mFolderList){
            if(folder.getFolderId() == folderId){
                folder.setFolderName(folderTitle);
                MainActivity.viewModel.update(folder);
                break;
            }
        }
        getActivity().getActionBar().setTitle(folderTitle);
    }

    public void createDeleteWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle(R.string.delete_tite)
                .setMessage(R.string.delete_msg)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFolder();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        dialog.show();
    }

    private void deleteFolder() {
        for(Folder folder : mFolderList){
            if(folder.getFolderId() == folderId){
                MainActivity.viewModel.delete(folder);
                getActivity().finish();
                break;
            }
        }
    }

}
