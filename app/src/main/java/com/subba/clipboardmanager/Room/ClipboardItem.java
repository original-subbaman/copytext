package com.subba.clipboardmanager.Room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "clipboard_table")
public class ClipboardItem {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private String time;
    private String folder;
    @Ignore
    private boolean isSelected;


    @Ignore
    public ClipboardItem(String text, String time){
        this(text, time, "Other", false);
    }

    @Ignore
    public ClipboardItem(String text, String time, String folder, boolean isSelected){
        this.text = text;
        this.time = time;
        this.folder = folder;
        this.isSelected = isSelected;
    }

    public ClipboardItem(String text, String time, String folder){
        this.text = text;
        this.time = time;
        this.folder = folder;
    }


    public void setId(int id) { this.id = id; }

    public int getId() { return this.id; }
    public String getText(){
        return this.text;
    }
    public String getTime() { return this.time; }
    public String getFolder() { return this.folder; }

    public void setSelected(boolean selected){
        this.isSelected = selected;
    }

    public boolean getSelected(){
        return this.isSelected;
    }


}
