package com.subba.clipboardmanager.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ClipboardItem.class, Folder.class}, version = 1, exportSchema = false)
public abstract class ClipboardDatabase extends RoomDatabase {

    private static ClipboardDatabase instance;

    public abstract ClipboardDAO getClipboardDAO();

    public static synchronized ClipboardDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ClipboardDatabase.class, "clipboard_database")
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
