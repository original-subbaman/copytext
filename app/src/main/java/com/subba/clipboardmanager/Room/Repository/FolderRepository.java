package com.subba.clipboardmanager.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.subba.clipboardmanager.Room.Database.CopyTextDatabase;
import com.subba.clipboardmanager.Room.DAO.FolderDAO;
import com.subba.clipboardmanager.Room.Entity.Folder;
import com.subba.clipboardmanager.Room.Relationship.FolderWithClips;

import java.util.List;

public class FolderRepository {
    private FolderDAO folderDAO;
    private LiveData<List<Folder>> folders;
    private LiveData<List<String>> folderNames;
    private List<FolderWithClips> folderWithClips;
    private static final String INSERT = "INSERT";
    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";
    public static final String SELECT = "SELECT";
    private RoomTask task;
    public FolderRepository(Application application){
        CopyTextDatabase db = CopyTextDatabase.getInstance(application);
        folderDAO = db.getFolderDAO();
        folders = folderDAO.getAllFoldersAsLiveData();
        folderNames = folderDAO.getFolderListAsString();
     }

    public void insert(Folder folder){
        task = new RoomTask(folderDAO, INSERT, folder);
        new Thread(task).start();
    }

    public void update(Folder folder){
        task = new RoomTask(folderDAO, UPDATE, folder);
        new Thread(task).start();
    }

    public void delete(Folder folder){
        task = new RoomTask(folderDAO, DELETE, folder);
        new Thread(task).start();
    }

    public LiveData<List<Folder>> getAllFoldersAsLiveData(){
        return this.folders;
    }

    public LiveData<List<FolderWithClips>> getClipsFromFolder(String folderName) { return folderDAO.getClipsFromFolder(folderName); }

    public List<FolderWithClips> getClipsFromFolderAsList(String folderName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                folderWithClips = folderDAO.getClipsFromFolderAsList(folderName);
            }
        }).start();
        return folderWithClips;
    }

    public LiveData<List<String>> getFolderListAsString(){
      return this.folderNames;
    }

    public LiveData<Folder> getFolderWithName(String folderName) { return folderDAO.getFolderWithName(folderName); }

    private static class RoomTask implements Runnable{
        private FolderDAO folderDAO;
        private String operation;
        private Folder folder;
        private String folderName;

        public RoomTask(FolderDAO dao, String op, Folder folder){
            this.folderDAO = dao;
            this.operation = op;
            this.folder = folder;
        }

        public RoomTask(FolderDAO dao, String op){
            this.folderDAO = dao;
            this.operation = op;
        }

        public void setFolderName(String folderName){
            this.folderName = folderName;
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
