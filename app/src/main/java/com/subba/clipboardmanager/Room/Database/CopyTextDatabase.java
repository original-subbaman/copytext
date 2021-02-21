package com.subba.clipboardmanager.Room.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.subba.clipboardmanager.Room.DAO.ClipboardDAO;
import com.subba.clipboardmanager.Room.DAO.FolderDAO;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Room.Entity.Folder;

@Database(entities = {ClipboardItem.class, Folder.class}, version = 1, exportSchema = false)
public abstract class CopyTextDatabase extends RoomDatabase {

    private static CopyTextDatabase instance;

    public abstract ClipboardDAO getClipboardDAO();
    public abstract FolderDAO getFolderDAO();

    public static synchronized CopyTextDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CopyTextDatabase.class, "copytext_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    private static class PopulateDB implements Runnable{
        private ClipboardDAO dao;
        public PopulateDB(ClipboardDAO dao) {
            this.dao = dao;
        }

        @Override
        public void run() {
            dao.insert(new ClipboardItem("First", "9:00AM", "Other"));
            dao.insert(new ClipboardItem("Second", "10:00AM", "Other"));
        }
    }
}
