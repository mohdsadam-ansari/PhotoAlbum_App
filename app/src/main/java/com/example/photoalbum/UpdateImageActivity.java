package com.example.photoalbum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateImageActivity extends AppCompatActivity {
    private ImageView updateImage;
    private EditText updateTitle,updateDescription;
    private Button update;
    private  String title,description;
    private int id;
    private byte[] image;
    private Bitmap selectedImage;
    private Bitmap scaledImage;
    ActivityResultLauncher<Intent> activityResultLauncherForSelectImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Update Image");
        setContentView(R.layout.activity_update_image);
        //initializing the components of update activity
        updateImage=findViewById(R.id.imageViewUpdateImage);
        updateTitle=findViewById(R.id.editTextUpdateTitle);
        updateDescription=findViewById(R.id.editTextUpdateDescription);
        update=findViewById(R.id.buttonUpdate);
        //registering activity for select image
        registerActivityForSelectImage();
        //getting the data from mainActivity
        id=getIntent().getIntExtra("id",-1);
        title=getIntent().getStringExtra("title");
        description=getIntent().getStringExtra("description");
        image=getIntent().getByteArrayExtra("image");
        //setting the data to the components of the update activity
        updateTitle.setText(title);
        updateDescription.setText(description);
        updateImage.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
        //onClick feature for imageView in update Activity
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncherForSelectImage.launch(intent);
            }
        });
        //onClick feature for update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }
    //function for getting data from user and send to MainActivity
    public void updateData(){
        if(id== -1){
            Toast.makeText(UpdateImageActivity.this,"There is  a problem",Toast.LENGTH_LONG).show();
        }
        else{
            String updateTitle1 = updateTitle.getText().toString();
            String updateDescription1 = updateDescription.getText().toString();
            Intent intent=new Intent();
            intent.putExtra("id",id);
            intent.putExtra("updateTitle",updateTitle1);
            intent.putExtra("updateDescription",updateDescription1);
            if(selectedImage==null){
                intent.putExtra("image",image);
            }
            else {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                scaledImage = makeSmall(selectedImage, 300);
                scaledImage.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
                byte[] image = outputStream.toByteArray();
                intent.putExtra("image",image);
            }
            setResult(RESULT_OK,intent);
            finish();
        }

    }
    //selecting image from MediaStore
    public void registerActivityForSelectImage() {
        activityResultLauncherForSelectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if (resultCode == RESULT_OK && data != null) {
                            try {
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                                updateImage.setImageBitmap(selectedImage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
    }
    //reducing the size of the Image
    public Bitmap makeSmall(Bitmap image,int maxSize){
        int width=image.getWidth();
        int height=image.getHeight();
        float ratio=(float) width/(float) height;
        if(ratio>1){
            width=maxSize;
            height=(int)(width/ratio);
        }
        else {
            height=maxSize;
            width=(int)(height*ratio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}