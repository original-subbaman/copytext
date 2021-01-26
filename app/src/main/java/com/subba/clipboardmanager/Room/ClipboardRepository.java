package com.subba.clipboardmanager.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ClipboardRepository {
    private ClipboardDAO clipDAO;
    private LiveData<List<ClipboardItem>> otherClips;
    private LiveData<List<String>> folderList;
    private static final String INSERT = "INSERT";
    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";
    private static final String MUL_DELETE = "DELETE";

    public ClipboardRepository(Application application){
        ClipboardDatabase db = ClipboardDatabase.getInstance(application);
        clipDAO = db.getClipboardDAO();
        otherClips = clipDAO.getClipboardItemsForFolder("Other");
        folderList = clipDAO.getFolderList();
    }

    public void insert(ClipboardItem clipboardItem){
        RoomTask task = new RoomTask(clipDAO, clipboardItem, INSERT);
        new Thread(task).start();
    }

    public void update(ClipboardItem clipboardItem){
        RoomTask task = new RoomTask(clipDAO, clipboardItem, UPDATE);
        new Thread(task).start();
    }

    public void delete(ClipboardItem clipboardItem){
        RoomTask task = new RoomTask(clipDAO, clipboardItem, DELETE);
        new Thread(task).start();
    }

    public void deleteClipboardItemsFromFolder(String folder, int id){
        RoomTask task = new RoomTask(clipDAO, folder, id, MUL_DELETE);
        new Thread(task).start();
    }

    public LiveData<List<ClipboardItem>> getClipboardItemsForFolder(String folder){
        return otherClips;
    }

    public LiveData<List<String>> getFolderList(){
        return folderList;
    }

    private static class RoomTask implements Runnable{
        private ClipboardDAO clipboardDAO;
        private ClipboardItem item;
        private String folder;
        private int clipId;
        private String operation;

        private RoomTask(ClipboardDAO dao, ClipboardItem item, String op){
            this.clipboardDAO = dao;
            this.item = item;
            this.operation = op;
        }

        private RoomTask(ClipboardDAO dao, String folder, int id, String op){
            this.clipboardDAO = dao;
            this.folder = folder;
            this.clipId = id;
        }

        @Override
        public void run() {
            switch(operation){
                case "INSERT":
                    clipboardDAO.insert(item);
                    break;
                case "DELETE":
                    clipboardDAO.delete(item);
                    break;
                case "UPDATE":
                    clipboardDAO.update(item);
                    break;
                case "MUL_DELETE":
                    clipboardDAO.deleteClipboardItemsFromFolder(folder, clipId);
                    break;
            }
        }


    }





}
