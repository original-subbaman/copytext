package com.subba.clipboardmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.Fade;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.subba.clipboardmanager.Fragments.DisplayClipsFragment;
import com.subba.clipboardmanager.Fragments.EditNoteFragment;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.databinding.ActivityNotesBinding;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

import static com.subba.clipboardmanager.Activities.MainActivity.mFolderList;

public class NotesActivity extends AppCompatActivity implements FlexibleAdapter.OnItemClickListener{

    private static final String TAG = "NotesActivity";
    private static List<IFlexible> clipsList;
    private ActivityNotesBinding mBinding;
    private String folder;
    private int folderId;
    public static FlexibleAdapter<IFlexible> mAdapter;
    private static final int DISPLAY_CLIPS_FRAGMENT = 1;
    private static final int EDIT_CLIPS_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        folder = getIntent().getStringExtra("folderName");
        folderId = getIntent().getIntExtra("folderId", -1);

        clipsList = new ArrayList<>();
        mAdapter = new FlexibleAdapter<>(clipsList);
        mAdapter.addListener(this);

        setSupportActionBar(mBinding.toolbar);
        setTitle(folder);
        setObserver(folder);




    }

    @Override
    protected void onStart() {
        super.onStart();
        DisplayClipsFragment displayClipsFragment = new DisplayClipsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("folder_id", folderId);
        displayClipsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.notes_fragment_container, displayClipsFragment, null)
                .commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    /*
   * Set up methods
   * */
    public void setObserver(String folder){
        MainActivity.viewModel.getClipsFromFolder(folder).observe(this, folderWithClips -> {
            clipsList.clear();
            if(folderWithClips.size() != 0){
                clipsList.addAll(folderWithClips.get(0).clips);
                for(IFlexible clip : clipsList){
                    ((ClipboardItem) clip).setIsNote(true);
                }
                mAdapter.updateDataSet(clipsList);
            }else{
                Log.d(TAG, "setObserver: folder deleted");
            }
        });

        MainActivity.viewModel.getFolderListAsLiveData().observe(this, folders -> {
            mFolderList.clear();
            mFolderList.addAll(folders);
        });
    }


    @Override
    public boolean onItemClick(View view, int position) {
        replaceCurrentFragmentWith(EDIT_CLIPS_FRAGMENT, (ClipboardItem) mAdapter.getItem(position));
        return true;
    }

    /*
    * Utility
    * */


    private void replaceCurrentFragmentWith(int fragmentId, ClipboardItem value){

        switch(fragmentId){
            case DISPLAY_CLIPS_FRAGMENT:
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack("display_note")
                        .replace(R.id.notes_fragment_container, DisplayClipsFragment.class, null)
                        .commit();
                break;
            case EDIT_CLIPS_FRAGMENT:
                EditNoteFragment editNoteFragment = new EditNoteFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", value);
                editNoteFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack("edit_note")
                        .replace(R.id.notes_fragment_container, editNoteFragment, null)
                        .commit(); 
                break;

        }

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
            Log.d(TAG, "onBackPressed: " + getSupportFragmentManager().getBackStackEntryCount());
        }else{
            super.onBackPressed();
        }
    }
}