package com.subba.clipboardmanager.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.Room.Relationship.FolderWithClips;

import java.util.List;

@Dao
public interface FolderDAO {

    @Insert
    void insert(Folder item);

    @Update
    void update(Folder item);

    @Delete
    void delete(Folder item);

    @Query("SELECT * FROM folder_table")
    LiveData<List<Folder>> getAllFoldersAsLiveData();

    @Query("SELECT * FROM folder_table WHERE folderName = :folderName")
    LiveData<Folder> getFolderWithName(String folderName);

    @Query("SELECT folderName FROM folder_table")
    LiveData<List<String>> getFolderListAsString();

    @Transaction
    @Query("SELECT * FROM folder_table WHERE folderName = :folderName")
    LiveData<List<FolderWithClips>> getClipsFromFolder(String folderName);

    @Transaction
    @Query("SELECT * FROM folder_table WHERE folderName = :folderName")
    List<FolderWithClips> getClipsFromFolderAsList(String folderName);

}
