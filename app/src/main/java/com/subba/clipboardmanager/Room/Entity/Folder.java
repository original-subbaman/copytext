package com.subba.clipboardmanager.Room.Entity;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="folder_table")
public class Folder{
    @PrimaryKey
    private int folderId;

    private String folderName;

    public Folder(String folderName){

        this.folderName = folderName;
        this.folderId = generateFolderId();

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

    @Ignore
    public int generateFolderId(){
        double a = Math.random() * (100 + 1);
        return (int) a;
    }
}
