package com.subba.clipboardmanager.Room.Relationship;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;

import java.util.List;

public class FolderWithClips {
    @Embedded
    public Folder folder;

    @Relation(
            parentColumn = "folderId",
            entityColumn = "folderId"
    )
    public List<ClipboardItem> clips;

}
