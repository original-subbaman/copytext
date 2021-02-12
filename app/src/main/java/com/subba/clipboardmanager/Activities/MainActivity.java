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

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Clip";
    
    private ActivityMainBinding binding;
    private ArrayList<ClipboardItem> mClips;
    private ClipsRecyclerAdapter mAdapter;
    private FoldersRecyclerAdapter mFolderAdapter;
    private RecyclerView mClipsRecyclerView;
    private RecyclerView mFoldersRecyclerView;
    private RecyclerView.LayoutManager mClipsRecyclerViewLayoutManager;
    public static ClipboardViewModel viewModel;
    private List<String> folderList;
    private String currentFolder = "Other";
    private Toolbar toolbar;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mClips = new ArrayList<>();
        folderList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            mAdapter.setClips(clips);
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
        mAdapter = new ClipsRecyclerAdapter(mClips);
        mClipsRecyclerView.setHasFixedSize(true);
        mClipsRecyclerView.setLayoutManager(mClipsRecyclerViewLayoutManager);
        mClipsRecyclerView.setAdapter(mAdapter);
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
        });
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

}