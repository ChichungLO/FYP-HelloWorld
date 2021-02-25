package student.example.exportpdf;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Context context;
    ImageView imageView;
    Button btn; //拍照按钮
    Button btn2; //从相册中选择
    Button btn3; //将当前图片导出为pdf
    Uri uri; //显示拍的图片

    Bitmap bmp, scaledBitmap;

    String TAG = "MainActivity-----------";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        //获取控件实例
        imageView = findViewById(R.id.img);
        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        //点击拍照
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个文件存放SD卡的应用关联缓存目录
                File file = new File(getExternalCacheDir(), "test.jpg");
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判断安卓系统版本
                if (Build.VERSION.SDK_INT >= 24) {
                    //将File对象转换成一个封装过的Uri对象,接收3个参数，第一个是上下文，第二个是任意唯一字符串，第三个File对象
                    uri = FileProvider.getUriForFile(context, "com.example.fyp8_2.fileprovider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
                btn3.setEnabled(true);
            }
        });

        //从相册中选择图片
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openAlbum();
                }
            }
        });


        //bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        //bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    exportPDF();
                }
            }
        });


        //从相册中选择图片

    }


    private void exportPDF() {
        Log.i(TAG, imageView.getDrawable().toString());
        bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        scaledBitmap = Bitmap.createScaledBitmap(bmp, 150, 150, false);

        PdfDocument myPdfDocument =new PdfDocument();
        Paint myPaint = new Paint();

        PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(250, 400, 1).create();
        Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();

        canvas.drawBitmap(scaledBitmap, 50,50, myPaint);

        myPdfDocument.finishPage(myPage1);

        File file = new File("/storage/emulated/0/FirstPDF.pdf");
        try{
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e){
            e.printStackTrace();
        }

        myPdfDocument.close();

    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 3);
        btn3.setEnabled(true);
    }


    private void handeImage(Intent data) {
        String imagePath = null;
        Uri uri2 = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri2)) {
            String docid = DocumentsContract.getDocumentId(uri2);
            if ("com.android.providers.media.documents".equals(uri2.getAuthority())) {
                String id = docid.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri2.getAuthority())) {
                Uri contenUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docid));
                imagePath = getImagePath(contenUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri2.getScheme())) {
            imagePath = getImagePath(uri2, null);
        } else if ("file".equalsIgnoreCase(uri2.getScheme())) {
            imagePath = uri2.getPath();
        }
        displayImage(imagePath);

    }

    private void handleImageBefor(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);

        } else {
            Toast.makeText(context, "failed to get image", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 手机权限结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(context, "You denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 如果拍照成功，则回调该方法得到所拍照的图片
     *
     * @param requestCode 请求码
     * @param resultCode  响应码
     * @param data        所得到的图片数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handeImage(data);
                    } else {
                        handleImageBefor(data);
                    }
                }
                break;
        }


    }


}
