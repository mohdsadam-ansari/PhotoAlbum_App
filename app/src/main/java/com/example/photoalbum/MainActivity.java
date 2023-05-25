package com.example.photoalbum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private FloatingActionButton fab;
    private ActivityResultLauncher<Intent> activityResultLauncherForAddImage;
    private ActivityResultLauncher<Intent> activityResultLauncherForUpdateImage;
    private MyImagesViewModel myImagesViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //registering for activity result from add and update activity
        registerActivityForAddImage();
        registerActivityForUpdateImage();
        //initializing the components of main activity
        rv=findViewById(R.id.rv);
        fab=findViewById(R.id.fab);
        //setting adapter to recyclerview and it will be displayed in linear format
        rv.setLayoutManager(new LinearLayoutManager(this));
        MyImagesAdapter adapter=new MyImagesAdapter();
        rv.setAdapter(adapter);
        //setting the object of myImagesViewModel class for Live data observe method in the recyclerview
        myImagesViewModel=new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(MyImagesViewModel.class);
        myImagesViewModel.getAllImages().observe(MainActivity.this, new Observer<List<MyImages>>() {
            @Override
            public void onChanged(List<MyImages> myImages) {
                //recyclerview
                adapter.setImagesList(myImages);
            }
        });
        //setting onClickListener to floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //defining and starting Intent for Result
                Intent intent=new Intent(MainActivity.this, AddImageActivity.class);
                activityResultLauncherForAddImage.launch(intent);
            }
        });

        //using ItemTouchHelper for enabling the feature of swiping cardView in recyclerview to delete data from database
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                myImagesViewModel.delete(adapter.getPosition(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(rv);
        //setting onClick feature in the recyclerview and passing the data to update activity
        adapter.setListener(new MyImagesAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(MyImages myImages) {
                Intent intent=new Intent(MainActivity.this, UpdateImageActivity.class);
                intent.putExtra("id",myImages.getImageId());
                intent.putExtra("title",myImages.getImageTitle());
                intent.putExtra("description",myImages.getImageDescription());
                intent.putExtra("image",myImages.getImage());
                activityResultLauncherForUpdateImage.launch(intent);
            }
        });
    }
    //for getting data from Add activity and inserting the data into database
    public void registerActivityForAddImage(){
        activityResultLauncherForAddImage=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode=result.getResultCode();
                Intent data=result.getData();
                if(resultCode==RESULT_OK && data!=null){
                    String title=data.getStringExtra("title");
                    String description=data.getStringExtra("description");
                    byte[] image=data.getByteArrayExtra("image");
                    MyImages myImages=new MyImages(title,description,image);
                    myImagesViewModel.insert(myImages);
                }
            }
        });
    }
    //for getting data from update activity and updating data into the database
    public void registerActivityForUpdateImage(){
        activityResultLauncherForUpdateImage=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode=result.getResultCode();
                Intent data=result.getData();
                if(resultCode==RESULT_OK && data!=null){
                    String tittle=data.getStringExtra("updateTitle");
                    String description=data.getStringExtra("updateDescription");
                    byte[] image=data.getByteArrayExtra("image");
                    int id=data.getIntExtra("id",-1);
                    MyImages myImages=new MyImages(tittle,description,image);
                    myImages.setImageId(id);
                    myImagesViewModel.update(myImages);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setIcon(R.drawable.baseline_exit_to_app_24);
        builder.setMessage("Do you really want to exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
