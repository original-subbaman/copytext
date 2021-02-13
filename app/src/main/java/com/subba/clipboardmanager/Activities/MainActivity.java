package com.subba.clipboardmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.subba.clipboardmanager.Adapters.ClipsRecyclerAdapter;
import com.subba.clipboardmanager.Adapters.FoldersRecyclerAdapter;
import com.subba.clipboardmanager.Room.ClipboardItem;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.ClipboardViewModel;
import com.subba.clipboardmanager.Services.ClipboardMonitorService;
import com.subba.clipboardmanager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback, FlexibleAdapter.OnItemLongClickListener, FlexibleAdapter.OnItemClickListener{
    public static final String TAG = "Clip";
    
    private ActivityMainBinding binding;
    private List<IFlexible> mClips;
    private ClipsRecyclerAdapter mAdapter;
    private FoldersRecyclerAdapter mFolderAdapter;
    private FlexibleAdapter<IFlexible> mClipAdapter;
    private RecyclerView mClipsRecyclerView;
    private RecyclerView mFoldersRecyclerView;
    private RecyclerView.LayoutManager mClipsRecyclerViewLayoutManager;
    public static ClipboardViewModel viewModel;
    private List<String> folderList;
    private String currentFolder = "Other";
    private Toolbar toolbar;
    private BottomSheetBehavior bottomSheetBehavior;
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
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(ClipboardViewModel.class);
        setUpRoomObservers();
        setUpNavigationDrawer();
        setUpClipsRecyclerView();
        setUpBottomSheet();
    }

    private void setUpRoomObservers(){
        viewModel.getAllClipsForOtherFolder().observe(this, clips -> {
//            mAdapter.setClips(clips);
            mClips.addAll(clips);
            mClipAdapter.updateDataSet(mClips);
        });

        viewModel.getFolderList().observe(this, folders -> {
            mFolderAdapter.setFolderList(folders);
        });
    }

    private void setUpNavigationDrawer(){
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setUpFoldersRecyclerView();
    }

    private void setUpClipsRecyclerView(){

        mClipsRecyclerView = findViewById(R.id.clipsListRecyclerView);
        mClipsRecyclerViewLayoutManager = new LinearLayoutManager(this);
//        mAdapter = new ClipsRecyclerAdapter(mClips);
        mClipsRecyclerView.setHasFixedSize(true);
        mClipsRecyclerView.setLayoutManager(mClipsRecyclerViewLayoutManager);

        mClipAdapter = new FlexibleAdapter<>(mClips);
        mClipsRecyclerView.setAdapter(mClipAdapter);
        mClipAdapter.addListener(this);
        /*mClipsRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemLongClickListener(new ClipsRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                ClipboardItem item = mAdapter.mClipList.get(position);
                item.setSelected(!item.getSelected());
                return item.getSelected();
            }
        });*/
    }

    private void setUpFoldersRecyclerView(){
        mFolderAdapter = new FoldersRecyclerAdapter(folderList);
        binding.folderListRecyclerView.setHasFixedSize(true);
        binding.folderListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.folderListRecyclerView.setAdapter(mFolderAdapter);
    }

    private void setUpBottomSheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void toggleBottomSheet(){
       if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
           bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
       }else{
           bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
       }
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

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

    private void toggleSelection(int position) {
        // Mark the position selected
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
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mClipAdapter.setMode(SelectableAdapter.Mode.IDLE);
        mActionMode = null;
    }
}