package com.subba.clipboardmanager.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ClipboardDAO {

    @Insert
    void insert(ClipboardItem item);

    @Update
    void update(ClipboardItem item);

    @Delete
    void delete(ClipboardItem item);

    @Query("DELETE FROM clipboard_table WHERE folder = :folder AND id = :id")
    void deleteClipboardItemsFromFolder(String folder, int id);

    @Query("SELECT * FROM clipboard_table WHERE folder = :folder")
    LiveData<List<ClipboardItem>> getClipboardItemsForFolder(String folder);

    @Query("SELECT * FROM clipboard_table")
    LiveData<List<ClipboardItem>> getAllClips();

    @Query("SELECT DISTINCT folder FROM clipboard_table")
    LiveData<List<String>> getFolderList();
}
