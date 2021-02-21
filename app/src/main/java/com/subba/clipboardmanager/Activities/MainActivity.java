package com.subba.clipboardmanager.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subba.clipboardmanager.Adapters.ClipsRecyclerAdapter;
import com.subba.clipboardmanager.Adapters.FoldersRecyclerAdapter;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.Room.ViewModel.RoomViewModel;
import com.subba.clipboardmanager.Services.ClipboardMonitorService;
import com.subba.clipboardmanager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback, FlexibleAdapter.OnItemLongClickListener, FlexibleAdapter.OnItemClickListener {
    public static final String TAG = "Clip";

    private static final int TYPE_INPUT_STRING = 0;
    private static final int TYPE_SINGLE_CHOICE_ITEM = 1;

    private ActivityMainBinding binding;
    private List<IFlexible> mClips;
    private ClipsRecyclerAdapter mAdapter;
    private FoldersRecyclerAdapter mFolderAdapter;
    private FlexibleAdapter<IFlexible> mClipAdapter;
    private RecyclerView mClipsRecyclerView;
    private RecyclerView mFoldersRecyclerView;
    private RecyclerView.LayoutManager mClipsRecyclerViewLayoutManager;
    public static RoomViewModel viewModel;
    private List<String> folderList;
    private String currentFolder = "Other";
    private Toolbar toolbar;
    private ActionMode mActionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mClips = new ArrayList<>();
        folderList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentFolder);
        ContextCompat.startForegroundService(this, new Intent(this, ClipboardMonitorService.class));
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(RoomViewModel.class);
        setUpRoomObservers();
        setUpNavigationDrawer();
        setUpClipsRecyclerView();
    }

    /*
     * Set up Room observers, navigation drawer, recycler view for folder and clips
     * */

    private void setUpRoomObservers() {
        viewModel.getAllClipsForOtherFolder().observe(this, clips -> {
            mClips.addAll(clips);
            mClipAdapter.updateDataSet(mClips);
        });

        viewModel.getFolderListAsLiveData().observe(this, folders -> {
            mFolderAdapter.setFolderList(folders);
        });
    }

    private void setUpNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setUpFoldersRecyclerView();
    }

    private void setUpClipsRecyclerView() {

        mClipsRecyclerView = findViewById(R.id.clipsListRecyclerView);
        mClipsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mClipsRecyclerView.setHasFixedSize(true);
        mClipsRecyclerView.setLayoutManager(mClipsRecyclerViewLayoutManager);
        mClipAdapter = new FlexibleAdapter<>(mClips);
        mClipsRecyclerView.setAdapter(mClipAdapter);
        mClipAdapter.addListener(this);
    }

    private void setUpFoldersRecyclerView() {
        mFolderAdapter = new FoldersRecyclerAdapter();
        binding.folderListRecyclerView.setHasFixedSize(true);
        binding.folderListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.folderListRecyclerView.setAdapter(mFolderAdapter);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
     * Recycler Item click listeners. Long press to start Action Mode. On tap to select or deselect item.
     * */

    @Override
    public void onItemLongClick(int position) {
        if (mActionMode == null) {
            mActionMode = startSupportActionMode(this);
        }
        toggleSelection(position);
    }


    @Override
    public boolean onItemClick(View view, int position) {
        if (mActionMode != null && position != RecyclerView.NO_POSITION) {
            toggleSelection(position);
            return true;
        } else {

            return false;
        }


    }

    /*
     * ActionMode Methods from Flexible Adapter
     * */

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
        mClipAdapter.setMode(SelectableAdapter.Mode.MULTI);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()){
            case R.id.option_add:
                //create
                createAlertDialog(TYPE_SINGLE_CHOICE_ITEM);
                break;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mClipAdapter.setMode(SelectableAdapter.Mode.IDLE);
        mActionMode = null;
    }

    /*
    * Utility stuff
    * */

    private void toggleSelection(int position) {
        // Mark the position selected
        ClipboardItem clipboardItem = (ClipboardItem) mClips.get(position);
        if(clipboardItem.getSelected()){
            clipboardItem.setSelected(false);
        }else{
            clipboardItem.setSelected(true);
        }

        mClipAdapter.toggleSelection(position);

        int count = mClipAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            setContextTitle(count);
        }
    }

    private void setContextTitle(int count) {
        mActionMode.setTitle(String.valueOf(count) + " " + (count == 1 ?
                getString(R.string.action_selected_one) :
                getString(R.string.action_selected_many)));
    }

    private List<ClipboardItem> getSelectedItems(){
        List<ClipboardItem> selectedItems = new ArrayList<>();
        for(int i = 0; i < mClips.size(); i++){
            ClipboardItem item = ((ClipboardItem) mClips.get(i));
            if(item.getSelected()){
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    private void createAlertDialog(int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if(type == MainActivity.TYPE_INPUT_STRING){
            builder.setTitle("Add folder name");
            final EditText editText = new EditText(this);
            editText.setId(InputType.TYPE_CLASS_TEXT);
            builder.setView(editText);
            builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String folderName = editText.getText().toString();
                    //insert new folder in folder table.
                    Folder newFolder = new Folder(folderName);
                    viewModel.insert(newFolder);
                    //set selected items' folder attribute to folder name
                    List<ClipboardItem> selectedItems = getSelectedItems();
                    for(ClipboardItem item : selectedItems){
                        item.setFolderId(newFolder.getFolderId());
                    }
                    //call update of the selected items
                    viewModel.update((ClipboardItem[])selectedItems.toArray());
                }
            });
        }else if(type == MainActivity.TYPE_SINGLE_CHOICE_ITEM){
            builder.setTitle("Select a folder");
            List<String> folderList = new ArrayList<>();
            folderList.add("Add new folder");
            folderList.addAll(viewModel.getFolderList());
            final String[] folders = new String[folderList.size()];
            builder.setSingleChoiceItems(folderList.toArray(folders), 0, null)
            .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ListView listView = ((AlertDialog) dialog).getListView();
                    String selectedFolderName = (String) listView.getAdapter().getItem(listView.getCheckedItemPosition());
                    Folder selectedFolder = viewModel.getFolderWithName(selectedFolderName);
                    if(selectedFolderName.equals("Add new folder")){
                        createAlertDialog(MainActivity.TYPE_INPUT_STRING);
                    }
                    List<ClipboardItem> items = getSelectedItems();
                    for(ClipboardItem item : items){
                        item.setFolderId(selectedFolder.getFolderId());
                    }
                    viewModel.update((ClipboardItem[])items.toArray());
                }
            });

        }
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }
}