package com.subba.clipboardmanager.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.subba.clipboardmanager.Room.Database.CopyTextDatabase;
import com.subba.clipboardmanager.Room.DAO.FolderDAO;
import com.subba.clipboardmanager.Room.Entity.Folder;

import java.util.List;

public class FolderRepository {
    private FolderDAO folderDAO;
    private LiveData<List<Folder>> folders;
    private static final String INSERT = "INSERT";
    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";

    public FolderRepository(Application application){
        CopyTextDatabase db = CopyTextDatabase.getInstance(application);
        folderDAO = db.getFolderDAO();
        folders = folderDAO.getAllFoldersAsLiveData();
     }

    public void insert(Folder folder){
        RoomTask task = new RoomTask(folderDAO, INSERT, folder);
        new Thread(task).start();
    }

    public void update(Folder folder){
        RoomTask task = new RoomTask(folderDAO, UPDATE, folder);
        new Thread(task).start();
    }

    public void delete(Folder folder){
        RoomTask task = new RoomTask(folderDAO, DELETE, folder);
        new Thread(task).start();
    }

    public LiveData<List<Folder>> getAllFoldersAsLiveData(){
        return this.folders;
    }

    public List<String> getFolderList(){
        return folderDAO.getFolderList();
    }

    public Folder getFolderWithName(String folderName) { return folderDAO.getFolderWithName(folderName); }

    private static class RoomTask implements Runnable{
        private FolderDAO folderDAO;
        private String operation;
        private Folder folder;

        private RoomTask(FolderDAO dao, String op, Folder folder){
            this.folderDAO = dao;
            this.operation = op;
            this.folder = folder;
        }

        @Override
        public void run() {
            switch(operation){
                case "INSERT":
                    folderDAO.insert(this.folder);
                    break;
                case "DELETE":
                    folderDAO.delete(this.folder);
                    break;
                case "UPDATE":
                    folderDAO.update(this.folder);
                    break;
           }
        }


    }





}
