package com.subba.clipboardmanager.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.subba.clipboardmanager.Room.Relationship.FolderWithClips;
import com.subba.clipboardmanager.Room.Repository.ClipboardRepository;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.Room.Repository.FolderRepository;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {
    private ClipboardRepository clipboardRepo;
    private FolderRepository folderRepo;
    private LiveData<List<ClipboardItem>> allClips;
    private LiveData<List<Folder>> folderList;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        clipboardRepo = new ClipboardRepository(application);
        folderRepo = new FolderRepository(application);
        allClips = clipboardRepo.getClipboardItemsForFolder("Other");
        folderList = folderRepo.getAllFoldersAsLiveData();
    }

    /*
    * Database operations for Clipboard table
    * */

    public void insert(ClipboardItem... items){
        clipboardRepo.insert(items);
    }

    public void update(ClipboardItem... items){
        clipboardRepo.update(items);
    }

    public void delete(ClipboardItem... items){
        clipboardRepo.delete(items);
    }

    public void deleteClipboardItemsFromFolder(String folder, int id){
        clipboardRepo.deleteClipboardItemsFromFolder(folder, id);
    }

    /*
    * Database operations for Folder table
    * */

    public void insert(Folder folder) { folderRepo.insert(folder); }

    public void update(Folder folder) { folderRepo.update(folder); }

    public void delete(Folder folder) { folderRepo.delete(folder); }

    public LiveData<List<String>> getFolderListAsString(){
        return folderRepo.getFolderListAsString();
    }

    public LiveData<Folder> getFolderWithName(String folderName) { return folderRepo.getFolderWithName(folderName); }

    public LiveData<List<FolderWithClips>> getClipsFromFolder(String folder) {
        return folderRepo.getClipsFromFolder(folder);
    }

    public LiveData<List<Folder>> getFolderListAsLiveData() { return this.folderList; }

    public List<FolderWithClips> getClipsFromFolderAsList(String folder){
        return folderRepo.getClipsFromFolderAsList(folder);
    }
}
