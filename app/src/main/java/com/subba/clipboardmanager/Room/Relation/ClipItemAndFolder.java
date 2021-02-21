package com.subba.clipboardmanager.Room.Relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;

public class ClipItemAndFolder {
    @Embedded public Folder folder;
    @Relation(
            parentColumn = "folderId",
            entityColumn = "clipId"
    )

    public ClipboardItem clipboardItem;
}
