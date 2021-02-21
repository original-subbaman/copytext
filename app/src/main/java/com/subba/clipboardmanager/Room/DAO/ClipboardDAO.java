package com.subba.clipboardmanager.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Relation.ClipItemAndFolder;

import java.util.List;

@Dao
public interface ClipboardDAO {

    @Insert
    void insert(ClipboardItem... item);

    @Update
    void update(ClipboardItem... item);

    @Delete
    void delete(ClipboardItem... item);

    @Query("DELETE FROM clipboard_table WHERE folder = :folder AND clipId = :id")
    void deleteClipboardItemsFromFolder(String folder, int id);

    @Query("SELECT * FROM clipboard_table WHERE folder = :folder")
    LiveData<List<ClipboardItem>> getClipboardItemsForFolder(String folder);

    @Query("SELECT * FROM clipboard_table")
    List<ClipboardItem> getAllFolders();

    @Transaction
    @Query("SELECT * FROM clipboard_table")
    List<ClipItemAndFolder> getClipItemAndFolder();


}
