package com.example.fyp10_2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.fyp10_2.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.Inet4Address;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Context context;
    ImageView imageView;
    Button btn; //拍照按钮
    Button btn2; //从相册中选择
    Button btn3; //清除手写内容
    Button btn4; //清除手写内容
    Button btn5; //清除手写内容
    Uri uri; //显示拍的图片
//ok
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
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);

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
                    uri = FileProvider.getUriForFile(context, "com.example.fyp10_2.fileprovider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
            }
        });

        //从相册中选择图片
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(context,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//                } else {
//                    openAlbum();
//                }
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                //imageView.setImageBitmap(bm);
                Bitmap newBitmap = clearBlue(bm, Color.argb(255,255,255,255));
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(newBitmap);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                //imageView.setImageBitmap(bm);
                Bitmap newBitmap = clearRed(bm, Color.argb(255,255,255,255));
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(newBitmap);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                //imageView.setImageBitmap(bm);
                Bitmap newBitmap = clearPencil(bm, Color.argb(255,255,255,255));
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(newBitmap);
            }
        });

    }
    //语言切换菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    //语言切换代码
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (item.getItemId()) {
            case R.id.SimplifiedChinese:
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            case R.id.TraditionalChinese:
                config.setLocale(Locale.TRADITIONAL_CHINESE);
                break;
            case R.id.English:
                config.setLocale(Locale.ENGLISH);
                break;
        }
        resources.updateConfiguration(config,dm);
        finish();
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);
        return false;
    }

//    /**
//     * 打开相册
//     */
//    private void openAlbum() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        startActivityForResult(intent, 3);
//    }
    private int[] getBackgroundColor(Bitmap bitmap){
        Bitmap mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int maxR = 0,maxG = 0,maxB = 0;
        int r,g,b;
        int[] red = new int[16];
        int[] blue = new int[16];
        int[] green = new int[16];
        //获取背景颜色
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = bitmap.getPixel(j,i);
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                red[r / 16]++;
                blue[b / 16]++;
                green[g / 16]++;
            }
        }
        //取最大值
        for (int i = 0; i < 16; i++) {
            if (red[i] > red[maxR]){
                maxR = i;
            }
            if (green[i] > green[maxG]){
                maxG = i;
            }
            if(blue[i] > blue[maxB]){
                maxB = i;
            }
        }
        return new int[]{maxR*16,maxG*16,maxB*16};
    }

    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri2 = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri2)) {
            String docID = DocumentsContract.getDocumentId(uri2);
            if ("com.android.providers.media.documents".equals(uri2.getAuthority())) {
                String id = docID.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri2.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docID));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri2.getScheme())) {
            imagePath = getImagePath(uri2, null);
        } else if ("file".equalsIgnoreCase(uri2.getScheme())) {
            imagePath = uri2.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBefore(Intent data) {
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


//    /**
//     * 手机权限结果回调
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 2) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openAlbum();
//            } else {
//                Toast.makeText(context, "You denied the permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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
                        Bitmap bitmap1 = replaceBitmapColor(bitmap, Color.argb(255,0,50,0),Color.argb(255,255,255,255));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImage(data);
                    } else {
                        handleImageBefore(data);
                    }
                }
                break;
            case 2:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    imageView.setImageURI(uri);
                }
        }


    }


    public Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = mBitmap.getPixel(j, i);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
//                if (g > r && g > b) {
//                    mBitmap.setPixel(j, i, (int) (newColor));
//                }
                if (r < 20 && g < 20 && b < 20) {
                    mBitmap.setPixel(j, i, (int) (newColor));
                }
            }
        }
        return mBitmap;
    }

    public Bitmap clearBlue(Bitmap oldBitmap,int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] c = getBackgroundColor(mBitmap);
        Random rand = new Random();
        int r;
        int g;
        int b;
        int color;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = mBitmap.getPixel(j, i);
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                if (b - r > 4 && b - g > 4) {
                    mBitmap.setPixel(j, i, Color.argb(255,c[0]+rand.nextInt(15),c[1]+rand.nextInt(15),c[2]+rand.nextInt(15)));
                }
                //安卓模拟器测试用代码
//                if (g - r > 20 && g - b > 20) {//清除绿色
//                    mBitmap.setPixel(j, i, Color.argb(255,c[0]+8,c[1]+8,c[2]+8));
//                }
            }
        }
        return mBitmap;
    }

    public Bitmap clearRed(Bitmap oldBitmap,int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] c = getBackgroundColor(mBitmap);
        Random rand = new Random();
        int r;
        int g;
        int b;
        int color;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = mBitmap.getPixel(j, i);
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                if (r - b > 5 && r - g > 5) {
                    mBitmap.setPixel(j, i,  Color.argb(255,c[0]+rand.nextInt(15),c[1]+rand.nextInt(15),c[2]+rand.nextInt(15)));
                }
            }
        }
        return mBitmap;
    }

    public Bitmap clearPencil(Bitmap oldBitmap,int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] c = getBackgroundColor(mBitmap);
        Random rand = new Random();
        int r;
        int g;
        int b;
        int color;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = mBitmap.getPixel(j, i);
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                if (Math.abs(r - g) < 5 && Math.abs(g - b) < 5 && Math.abs(r - b) < 5) {
                    mBitmap.setPixel(j, i,  Color.argb(255,c[0]+rand.nextInt(15),c[1]+rand.nextInt(15),c[2]+rand.nextInt(15)));
                }
            }
        }
        return mBitmap;
    }
}
