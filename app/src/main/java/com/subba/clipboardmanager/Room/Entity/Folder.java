package com.subba.clipboardmanager.Room.Entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="folder_table")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    private int folderId;

    private String folderName;
    public int clipId;

    public Folder(String name){
        this.folderName = name;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }
}
