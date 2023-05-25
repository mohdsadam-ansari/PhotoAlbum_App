package com.example.photoalbum;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.photoalbum.MyImages;
import com.example.photoalbum.MyImagesDao;
import com.example.photoalbum.MyImagesDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyImagesRepository {
    //using ExecutorService class to run insert,update,delete operation on different thread other than main thread.
    ExecutorService executorService= Executors.newSingleThreadExecutor();
    //creating object of Dao interface.
    private MyImagesDao myImagesDao;
    private LiveData<List<MyImages>> imageList;
    public MyImagesRepository(Application application){
        MyImagesDatabase database= MyImagesDatabase.getInstance(application);
        myImagesDao=database.myImagesDao();
        imageList=myImagesDao.getAllImages();
    }
    public void insert(MyImages myImages){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.insert(myImages);
            }
        });
    }
    public void delete(MyImages myImages){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.delete(myImages);
            }
        });
    }
    public void update(MyImages myImages){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.update(myImages);
            }
        });
    }
    public LiveData<List<MyImages>> getAllImages(){
        return imageList;
    }

}
