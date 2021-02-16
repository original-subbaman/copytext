package com.subba.clipboardmanager.Room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ClipboardViewModel extends AndroidViewModel {
    private ClipboardRepository repository;
    private LiveData<List<ClipboardItem>> allClips;
    private LiveData<List<String>> folderList;

    public ClipboardViewModel(@NonNull Application application) {
        super(application);
        repository = new ClipboardRepository(application);
        allClips = repository.getClipboardItemsForFolder("Other");
        folderList = repository.getFolderList();
    }

    public LiveData<List<ClipboardItem>> getAllClipsForOtherFolder(){
        return allClips;
    }

    public LiveData<List<String>> getFolderList() { return folderList; }

    public void insert(ClipboardItem... items){
        repository.insert(items);
    }

    public void update(ClipboardItem... items){
        repository.update(items);
    }

    public void delete(ClipboardItem... items){
        repository.delete(items);
    }

    public void deleteClipboardItemsFromFolder(String folder, int id){
        repository.deleteClipboardItemsFromFolder(folder, id);
    }

    public List<String> getFolderListWithoutObserver(){
        return repository.getFolderListWithoutObserver();
    }


}
