package com.example.editpart;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.editpart.app.AppConstants;
import com.example.editpart.widget.MyRelativeLayout;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ModifyContent extends AppCompatActivity implements MyRelativeLayout.MyRelativeTouchCallBack {
    private MyRelativeLayout rela;
    public static final String TAG = "ModifyContent";
    private static final int REQUEST_PERMISSION = 1001;
    private static final int PICK_IMAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_content);
        //getSupportActionBar().hide();


        rela = (MyRelativeLayout) findViewById(R.id.id_rela);
        rela.setMyRelativeTouchCallBack(this);

        /*
        if (getIntent().hasExtra("bitmap")) {
            byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            rela.setBackGroundBitmap(bitmap);
        }

         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            BitmapBinder bitmapBinder = (BitmapBinder) bundle.getBinder("bitmap");
            Bitmap bitmap = bitmapBinder.getBitmap();
            rela.setBackGroundBitmap(bitmap);
            //og.i("MainActivity", "Mani2Activity bitmap 大小" + bitmap.getByteCount() / 1024 + " kb");
        }
    }

    /**
     * 当时重写这个方法是因为项目中有左右滑动切换不同滤镜的效果
     *
     * @param direction
     */
    @Override
    public void touchMoveCallBack(int direction) {
        if (direction == AppConstants.MOVE_LEFT) {
            //Toast.makeText(ModifyContent.this, "你在向左滑动！", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(ModifyContent.this, "你在向右滑动！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 这个方法可以用来实现滑到某一个地方删除该TextView的实现
     *
     * @param textView
     */
    @Override
    public void onTextViewMoving(TextView textView) {
        //Log.d(TAG, "TextView正在滑动");
    }

    @Override
    public void onTextViewMovingDone() {
        //Toast.makeText(ModifyContent.this, "标签TextView滑动完毕！", Toast.LENGTH_SHORT).show();
    }

    public void btnClickImport(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && data !=null && resultCode ==RESULT_OK){
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                rela.setBackGroundBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void btnClickSave(View view) {
        /*
        Bitmap bitmap = ImageUtils.createViewBitmap(rela, rela.getWidth(), rela.getHeight());
        String fileName = "CRETIN_" + new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date()) + ".png";
        String result = ImageUtils.saveBitmapToFile(bitmap, fileName);
        Toast.makeText(RemovedManually.this, "保存位置:" + result, Toast.LENGTH_SHORT).show();

         */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {

            try {
                saveBitmap();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveBitmap() throws IOException {

        Bitmap bitmap = ImageUtils.createViewBitmap(rela, rela.getWidth(), rela.getHeight());
        String file_name = UUID.randomUUID() + ".png";
        OutputStream outputStream;
        boolean saved;
        File folder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        } else {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File image = new File(folder + File.separator + file_name);
        Uri imageUri = Uri.fromFile(image);

        outputStream = new FileOutputStream(image);
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file_name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(uri);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        } else {

            sendPictureToGallery(imageUri);

        }

        if (saved)
            Toast.makeText(this, "picture saved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "picture not saved", Toast.LENGTH_SHORT).show();

        outputStream.flush();
        outputStream.close();

    }

    private void sendPictureToGallery(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
    }

    public void finishPaint(View view) {
        finish();
    }

    public void btnClickHint(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_baseline_text_fields_24));
        builder.setTitle("Hint:");
        builder.setMessage("Click anywhere to add text.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
