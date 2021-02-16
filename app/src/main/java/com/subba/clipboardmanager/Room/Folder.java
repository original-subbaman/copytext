package com.subba.clipboardmanager.Room;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="folder_table")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String folderName;

    public Folder(String name){
        this.folderName = name;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
