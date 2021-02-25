package com.example.imgselecter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private  static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private ImageView imageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSelected = findViewById(R.id.selectedImage);

        //On apps that target Android 6.0 (API lever 23) or higher, we need to check for and request
        //permissions at runtime.
        findViewById(R.id.buttonSelectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    selectImage();
                }

            }
        });
    }

    private void selectImage(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else {
                Toast.makeText(this,"没有权限!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && requestCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {

                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageSelected.setImageBitmap(bitmap);
                        Toast.makeText(this, "执行完毕", Toast.LENGTH_SHORT).show();

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}//End of MainActivity