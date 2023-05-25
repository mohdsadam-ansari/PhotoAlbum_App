package com.example.photoalbum;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddImageActivity extends AppCompatActivity {
    private ImageView addImage;
    private EditText addTitle,addDescription;
    private Button save;
    private Bitmap selectedImage;
    private Bitmap scaledImage;
    ActivityResultLauncher<Intent>activityResultLauncherForSelectImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Add Image");
        setContentView(R.layout.activity_add_image);
        //registering activity for displaying data from database
        registerActivityForSelectImage();
        //initializing the components of addImage activity
        addImage=findViewById(R.id.imageViewAddImage);
        addTitle=findViewById(R.id.editTextAddTitle);
        addDescription=findViewById(R.id.editTextDescription);
        save=findViewById(R.id.buttonSave);
        //setting onClick feature on Image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* checking the Read External Storage permission is given or not than requesting the permission for
                 Read External Storage in case when it is not given */
                if(ContextCompat.checkSelfPermission(AddImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddImageActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    //defining and starting implicit intent
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncherForSelectImage.launch(intent);
                }
            }
        });
        //setting onClick feature on save button and sending data to MainActivity
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage==null){
                    Toast.makeText(AddImageActivity.this,"please Select an Image",Toast.LENGTH_LONG).show();
                }
                else {
                    String title = addTitle.getText().toString();
                    String description = addDescription.getText().toString();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    scaledImage = makeSmall(selectedImage, 300);
                    scaledImage.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
                    byte[] image = outputStream.toByteArray();
                    Intent intent=new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("description",description);
                    intent.putExtra("image",image);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
    //setting the image into imageView
    public void registerActivityForSelectImage(){
        activityResultLauncherForSelectImage=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode=result.getResultCode();
                        Intent data=result.getData();
                        if(resultCode==RESULT_OK && data!=null){
                            try {
                                selectedImage=MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                                addImage.setImageBitmap(selectedImage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
    }
    //checking the permission and setting implicit intent for mediaStore for selecting image
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncherForSelectImage.launch(intent);
        }
    }
    //reducing the size of the image that can easily stored into the database
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