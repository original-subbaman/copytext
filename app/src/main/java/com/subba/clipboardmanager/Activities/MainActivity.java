package com.subba.clipboardmanager.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.subba.clipboardmanager.Adapters.FoldersRecyclerAdapter;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.Room.Relationship.FolderWithClips;
import com.subba.clipboardmanager.Room.ViewModel.RoomViewModel;
import com.subba.clipboardmanager.Services.ClipboardMonitorService;
import com.subba.clipboardmanager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback,
        FlexibleAdapter.OnItemLongClickListener,
        FlexibleAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener{
    public static final String TAG = "Clip";

    private ActivityMainBinding binding;
    private List<IFlexible> mClips;
    private List<String> mFolderNames;
    private FoldersRecyclerAdapter mFolderAdapter;
    private FlexibleAdapter<IFlexible> mClipAdapter;
    private RecyclerView mClipsRecyclerView;
    private RecyclerView mFolderRecyclerView;
    private RecyclerView.LayoutManager mClipsRecyclerViewLayoutManager;
    public static RoomViewModel viewModel;
    public static List<Folder> mFolderList;
    private static List<Integer> mViewPositionList;
    private Toolbar toolbar;
    private ActionMode mActionMode;
    private Intent serviceIntent;
    private SearchView mSearchView;
    private ClipboardItem mDeletedClip;
    private LayoutAnimationController mAnimationController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mClips = new ArrayList<>();
        mFolderList = new ArrayList<>();
        mFolderNames = new ArrayList<>();
        mViewPositionList = new ArrayList<>();

        mFolderRecyclerView = findViewById(R.id.folder_list_recycler_view);
        mClipsRecyclerView = findViewById(R.id.clipsListRecyclerView);

        toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        serviceIntent = new Intent(this, ClipboardMonitorService.class);
        serviceIntent.setAction(ClipboardMonitorService.ACTION_START_FOREGROUND_SERVICE);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(RoomViewModel.class);
        setUpNavigationDrawer();
        setClickListenerForButtons();
        setListenerForCollapsibleToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.setContext(this);
        ContextCompat.startForegroundService(this, serviceIntent);
        setUpRoomObservers();
        setUpClipsRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.setContext(null);
        mClipsRecyclerView.scheduleLayoutAnimation();
    }

    /*
     * Set up Room observers, navigation drawer, recycler view for folder and clips, and set date
     * */

    private void setUpRoomObservers() {

        viewModel.getClipsFromFolder("Recent").observe(this, folderWithClips -> {
            mClips.clear();
            for (FolderWithClips clips : folderWithClips) {
                mClips.addAll(clips.clips);
            }
            mClipAdapter.updateDataSet(mClips);
            if(mClipAdapter.getItemCount() == 0){
                setVisibility(mClipsRecyclerView, View.GONE);
                setVisibility(binding.emptyView, View.VISIBLE);
            }else{
                setVisibility(mClipsRecyclerView, View.VISIBLE);
                setVisibility(binding.emptyView, View.GONE);
            }
        });

        viewModel.getFolderListAsLiveData().observe(this, folders -> {
            mFolderList.clear();
            mFolderList.addAll(folders);
            if (mFolderList == null) {
                Log.d(TAG, "setUpRoomObservers: " + folders.size());
            } else {
                mFolderAdapter.setFolderList(mFolderList);
            }

        });

        viewModel.getFolderListAsString().observe(this, folderNames -> {
            mFolderNames.clear();
            mFolderNames.addAll(folderNames);
        });


    }

    private void setUpNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mFolderRecyclerView.scheduleLayoutAnimation();

            }
        };
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setUpFoldersRecyclerView();
    }

    private void setUpClipsRecyclerView() {

        mClipsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mClipsRecyclerView.setHasFixedSize(true);

        mAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_drop_from_top);
        mClipsRecyclerView.setLayoutAnimation(mAnimationController);

        mClipAdapter = new FlexibleAdapter<>(mClips);
        mClipsRecyclerView.setAdapter(mClipAdapter);
        mClipsRecyclerView.setLayoutManager(mClipsRecyclerViewLayoutManager);
        mClipAdapter.addListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mClipsRecyclerView);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.RIGHT:
                    mDeletedClip = (ClipboardItem) mClips.get(position);
                    mClipAdapter.removeItem(position);
                    Snackbar.make(mClipsRecyclerView, "Delete", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                mClips.add(position, mDeletedClip);
                                mClipAdapter.addItem(position, mDeletedClip);
                            })
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                                        viewModel.delete(mDeletedClip);
                                    }
                                }
                            })

                            .show();
                    break;
            }
        }

    };

    private void setClickListenerForButtons() {
        //Clear button
        binding.clearClipsBtn.setOnClickListener(v -> {
            mAnimationController = AnimationUtils.loadLayoutAnimation(MainActivity.this, R.anim.layout_fade_from_top);
            mClipsRecyclerView.setLayoutAnimation(mAnimationController);
            mClipAdapter.clear();
            mClipsRecyclerView.scheduleLayoutAnimation();
            clearClipsFromRecent();
        });

        binding.reportBtn.setOnClickListener(v -> {
            Intent reportActivityIntent = new Intent(MainActivity.this, GuideActivity.class);
            startActivity(reportActivityIntent);
        });
    }

    private void setUpFoldersRecyclerView() {
        mFolderAdapter = new FoldersRecyclerAdapter();
        mFolderAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, NotesActivity.class);
            String folderName = mFolderList.get(position).getFolderName();
            int folderId = mFolderList.get(position).getFolderId();
            intent.putExtra("folderName", folderName);
            intent.putExtra("folderId", folderId);
            startActivity(intent);
            binding.drawerLayout.closeDrawer(GravityCompat.START, false);
        });
        mFolderRecyclerView.setHasFixedSize(true);
        mFolderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFolderRecyclerView.setAdapter(mFolderAdapter);
    }

    private void setListenerForCollapsibleToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsible_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                    isShow = true;
                } else if(isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /*
    * Room database operation
    * */

    private void clearClipsFromRecent()
    {
        for(IFlexible item : mClips) {
            viewModel.delete((ClipboardItem) item);
        }
    }
    /*
     * Options menu
     * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        initSearchView(menu);

        return true;
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
            showClipDialog(position);
            return false;
        }


    }

    public void showClipDialog(int position){
        ClipboardItem selectedItem = (ClipboardItem) mClips.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.show_clip_dialog, null);
        builder.setCancelable(true);
        builder.setView(view);

        ((TextView) view.findViewById(R.id.clip_time)).setText(selectedItem.getTime());
        ((TextView) view.findViewById(R.id.clip_text)).setText(selectedItem.getText());

        final AlertDialog showClipDialog = builder.create();

        showClipDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        showClipDialog.show();
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
        switch (item.getItemId()) {
            case R.id.option_add:
                //create
                createSelectFolderDialog();
                return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mClipAdapter.setMode(SelectableAdapter.Mode.IDLE);
        mActionMode = null;
        resetViewSelectedBackground();
    }

    private void toggleSelection(int position) {
        ClipboardItem clipboardItem = (ClipboardItem) mClips.get(position);
        View view = mClipsRecyclerView.getLayoutManager().findViewByPosition(position);
        if (clipboardItem.getSelected()) {
            clipboardItem.setSelected(false);
            view.setBackgroundResource(R.drawable.card_unselected_background);
        } else {
            clipboardItem.setSelected(true);
            view.setBackgroundResource(R.drawable.card_selected_background);
            mViewPositionList.add(position);
        }

        mClipAdapter.toggleSelection(position);

        int count = mClipAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            setContextTitle(count);
        }
    }

    /*
     * Utility stuff
     * */

    private void resetViewSelectedBackground() {
        if (mClipAdapter.getItemCount() == 0) return;
        for (Integer pos : mViewPositionList) {
            mClipsRecyclerView.
                    getLayoutManager()
                    .findViewByPosition(pos)
                    .setBackgroundResource(R.drawable.card_unselected_background);
            ((ClipboardItem) mClipAdapter.getItem(pos)).setSelected(false);
        }
        mViewPositionList.clear();
    }

    private void setContextTitle(int count) {
        mActionMode.setTitle(count + " " + (count == 1 ?
                getString(R.string.action_selected_one) :
                getString(R.string.action_selected_many)));
    }

    private List<ClipboardItem> getSelectedItems() {
        List<ClipboardItem> selectedItems = new ArrayList<>();
        for (int i = 0; i < mClips.size(); i++) {
            ClipboardItem item = ((ClipboardItem) mClips.get(i));
            if (item.getSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public void createAddNewFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View customDialogView = LayoutInflater.from(this).inflate(R.layout.custom_single_input_dialog, null);
        builder.setCancelable(false);
        builder.setView(customDialogView);

        final AlertDialog dialog = builder.create();

        EditText editText = customDialogView.findViewById(R.id.rename_folder_edit_text);
        ((TextView) customDialogView.findViewById(R.id.dialog_title_text_view)).setText(R.string.new_folder);
        ((Button) customDialogView.findViewById(R.id.cancel_dialog_button)).setOnClickListener((View.OnClickListener) v -> dialog.dismiss());

        ((Button) customDialogView.findViewById(R.id.positive_dialog_button)).setText(R.string.add);
        ((Button) customDialogView.findViewById(R.id.positive_dialog_button))
                .setOnClickListener((View.OnClickListener) v -> {
                    String folderName = editText.getText().toString();
                    if (folderName.length() == 0) {
                        Toast.makeText(MainActivity.this, R.string.empty_input, Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isFolderExist = false;
                        //check if folder already exists in data
                        for(Folder folder : mFolderList){
                            if(folder.getFolderName().toLowerCase().equals(folderName.toLowerCase())){
                                isFolderExist = true;
                                break;
                            }
                        }

                        if(isFolderExist){
                            Toast.makeText(MainActivity.this, R.string.folder_exists, Toast.LENGTH_LONG).show();
                        }else{

                            Folder newFolder = new Folder(folderName);

                            viewModel.insert(newFolder);
                            List<ClipboardItem> selectedItems = getSelectedItems();
                            for (ClipboardItem item : selectedItems) {
                                item.setFolderId(newFolder.getFolderId());
                                viewModel.update(item);
                            }

                            if (mActionMode != null) {
                                mActionMode.finish();
                            } else {
                                mClipAdapter.clearSelection();
                            }

                            dialog.dismiss();
                        }

                    }
                });

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        dialog.show();
    }

    public void createSelectFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select a folder");
        if (!this.mFolderNames.contains("Add new folder")) {
            this.mFolderNames.add(0, "Add new folder");
        }
        String[] folders = new String[mFolderNames.size()];
        builder.setSingleChoiceItems(mFolderNames.toArray(folders), 0, null)
                .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView listView = ((AlertDialog) dialog).getListView();
                        String selectedFolderName = (String) listView.getAdapter().getItem(listView.getCheckedItemPosition());
                        if (selectedFolderName.equals("Add new folder")) {
                            createAddNewFolderDialog();
                        } else {
                            List<ClipboardItem> items = getSelectedItems();
                            Folder selectedFolder = null;
                            for (Folder folder : mFolderList) {
                                if (folder.getFolderName().equals(selectedFolderName)) {
                                    selectedFolder = folder;
                                    break;
                                }
                            }
                            for (ClipboardItem item : items) {
                                item.setFolderId(selectedFolder.getFolderId());
                                viewModel.update(item);
                            }
                        }

                        if (mActionMode != null) {
                            mActionMode.finish();
                        } else {
                            mClipAdapter.clearSelection();
                        }

                        dialog.dismiss();


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        dialog.show();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mClipAdapter.hasNewFilter(newText)){
            mClipAdapter.setFilter(newText);
            mClipAdapter.filterItems();
        }
        return true;
    }

    private void initSearchView(final Menu menu){
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        if(searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(this);
        }
    }

    private void setVisibility(View view, int visible){
        view.setVisibility(visible);
    }
}