package com.subba.clipboardmanager.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.subba.clipboardmanager.Room.Entity.Folder;

import java.util.List;

@Dao
public interface FolderDAO {

    @Insert
    void insert(Folder item);

    @Update
    void update(Folder item);

    @Delete
    void delete(Folder item);

    @Query("SELECT folderName FROM folder_table")
    LiveData<List<Folder>> getAllFoldersAsLiveData();

    @Query("SELECT * FROM folder_table WHERE folderName = :folderName")
    Folder getFolderWithName(String folderName);

    @Query("SELECT folderName FROM folder_table")
    List<String> getFolderList();

}
