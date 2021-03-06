package com.example.editpart;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.UriUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class OneClickDelete extends AppCompatActivity {
    String fileName;
    String mFilePath;
    String imagePath;
    Context context;
    ImageView imageView;
    LinearLayout btn; //拍照按钮
    LinearLayout btn2; //从相册中选择
    LinearLayout btn3; //清除手写内容
    LinearLayout btn4; //清除手写内容
    LinearLayout btn5; //保存内容
    LinearLayout btn6; //手动清除内容
    LinearLayout btn7; //添加文字
    LinearLayout btn8; //导出到pdf
    Uri uri; //拍照图片的uri
    Uri uri2;//相册选择的图片的uri
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click_delete);
        requestWritePermission();
        context = OneClickDelete.this;
        //获取控件实例
        imageView = findViewById(R.id.img);
        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        //点击拍照
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个文件存放SD卡的应用关联缓存目录
                File file = new File(getExternalCacheDir(), "test.png");
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
                    //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    //StrictMode.setVmPolicy(builder.build());
                    uri = FileProvider.getUriForFile(context, "com.example.editpart.fileprovider", file);
                    //uri2 = Uri.fromFile(file);
                    //uri2 = FileProvider.getUriForFile(getApplicationContext(),getPackageName()+".fileprovider",file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri2);
                startActivityForResult(intent, 1);
            }
        });
        //从相册中选择图片
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,uri2);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri2);
                startActivityForResult(intent,2);
            }
        });
        //清除蓝色
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((BitmapDrawable)((ImageView) imageView).getDrawable() != null) {
                    Bitmap bm = ((BitmapDrawable) ((ImageView) imageView).getDrawable()).getBitmap();
                    //imageView.setImageBitmap(bm);
                    Bitmap newBitmap = clearBlue(bm, Color.argb(255, 255, 255, 255));
                    imageView.setImageDrawable(null);
                    imageView.setImageBitmap(BinaryGray(newBitmap));
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.ClearSuccess, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.NoImage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                    //Toast.makeText(getApplicationContext(), R.string.NoImage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //清除红色
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((BitmapDrawable)((ImageView) imageView).getDrawable() != null) {
                    Bitmap bm = ((BitmapDrawable) ((ImageView) imageView).getDrawable()).getBitmap();
                    //imageView.setImageBitmap(bm);
                    Bitmap newBitmap = clearRed(bm, Color.argb(255, 255, 255, 255));
                    imageView.setImageDrawable(null);
                    imageView.setImageBitmap(BinaryGray(newBitmap));
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.ClearSuccess, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.NoImage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                }
            }
        });
        //保存并清空图片
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("imageView==null?",(imageView==null)+"");
                if((BitmapDrawable)((ImageView) imageView).getDrawable() == null){
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.NoImage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                } else {
                    Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                    Intent i = new Intent(OneClickDelete.this, RemovedManually.class);
                    Bundle bundle = new Bundle();
                    bundle.putBinder("bitmap", new BitmapBinder(bm));
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("imageView==null?",(imageView==null)+"");
                if((BitmapDrawable)((ImageView) imageView).getDrawable() == null){
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.NoImage, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,-100);
                    toast.show();
                } else {
                    Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                    Intent i = new Intent(OneClickDelete.this, ModifyContent.class);
                    Bundle bundle = new Bundle();
                    bundle.putBinder("bitmap", new BitmapBinder(bm));
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
        //导出到pdf
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelector.builder()
                        .setSingle(false)  //设置是否单选
                        .start(OneClickDelete.this, 5); // 打开相册
            }
        });
    }

    //将处理完的照片存入系统相册
    public void save(){
        Drawable drawable = imageView.getDrawable();
        File fileDir = new File(Environment.getExternalStorageDirectory(),"Pictures");
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        fileName = "IMG_" + System.currentTimeMillis() + ".png";
        mFilePath = fileDir.getAbsolutePath() + "/" + fileName;
        Uri fileUri = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,fileName);
        contentValues.put(MediaStore.Images.Media.DATA,mFilePath);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/JPEG");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        try {
            OutputStream outputStream = imageView.getContext().getContentResolver().openOutputStream(fileUri);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            imageView.getContext().sendBroadcast(new Intent("com.android.camera.NEW_PICTURE",fileUri));
            imageView.setImageDrawable(null);
            Toast toast = Toast.makeText(getApplicationContext(), R.string.SaveSuccess, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-100);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //此菜单挪到了homepage的setting位置
    /*
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
        Intent intent = new Intent(OneClickDelete.this,OneClickDelete.class);
        startActivity(intent);
        return false;
    }

     */

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

    public Bitmap BinaryGray(Bitmap bm) {
        //得到图形的宽度和长度
        int width = bm.getWidth();
        int height = bm.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = bm.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                //对图像进行二值化处理
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

//    private void handleImage(Intent data) {
//        String imagePath = null;
//        Uri uri2 = data.getData();
//        if (DocumentsContract.isDocumentUri(this, uri2)) {
//            String docID = DocumentsContract.getDocumentId(uri2);
//            if ("com.android.providers.media.documents".equals(uri2.getAuthority())) {
//                String id = docID.split(":")[1];
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//            } else if ("com.android.providers.downloads.documents".equals(uri2.getAuthority())) {
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docID));
//                imagePath = getImagePath(contentUri, null);
//            }
//        } else if ("content".equalsIgnoreCase(uri2.getScheme())) {
//            imagePath = getImagePath(uri2, null);
//        } else if ("file".equalsIgnoreCase(uri2.getScheme())) {
//            imagePath = uri2.getPath();
//        }
//        displayImage(imagePath);
//    }
//
//    private void handleImageBefore(Intent data) {
//        Uri uri = data.getData();
//        String imagePath = getImagePath(uri, null);
//        displayImage(imagePath);
//    }

//    private String getImagePath(Uri uri, String selection) {
//        String path = null;
//        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
//    }

//    private void displayImage(String path) {
//        if (path != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            imageView.setImageBitmap(bitmap);
//        } else {
//            Toast.makeText(context, "failed to get image", Toast.LENGTH_SHORT).show();
//        }
//
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
                    crop(uri);
                }
                break;
            case 2:
                if (data != null) {
                    // 得到图片的全路径
                    uri2 = data.getData();
                    crop2(uri2);
                    //imageView.setImageURI(uri);
                }
                break;
//            case 3:
//                if (resultCode == RESULT_OK) {
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        handleImage(data);
//                    } else {
//                        handleImageBefore(data);
//                    }
//                }
//                break;
                //拍照裁剪后显示图片
            case 4:
                imageView.setImageDrawable(null);
                imageView.setImageURI(uri);
                Log.e("case 4", data.getData()+"哈哈");
                //System.out.println(data.getData()+"哈哈");
                break;
            case 5:
                if (data != null ){
                    ArrayList<String> imagesAddress = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    Uri uri = UriUtils.getImageContentUri(OneClickDelete.this, imagesAddress.get(0));
                    exportPDF(imagesAddress);
                    imagePath = getPAth(uri);
                    //imageView.setImageURI(uri);
                }
                break;
                //从相册选取图片后显示图片
            case 6:
                imageView.setImageDrawable(null);
                imageView.setImageURI(uri2);
                Log.e("case 6", data.getData()+"哈哈");
                //System.out.println(data.getData()+"哈哈");
                break;
        }
    }
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("scale", true);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        // 开启一个带有返回值的Activity，请求码为4
        uri = Uri.fromFile(new File(getExternalCacheDir(), "test.png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        //Log.e("crop()", cropUri+"哈哈");
        //System.out.println(cropUri+"哈哈");
        startActivityForResult(intent, 4);
    }

    private void crop2(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("scale", true);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        // 开启一个带有返回值的Activity，请求码为4
        uri2 = Uri.fromFile(new File(getExternalCacheDir(), "test.png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri2);
        //System.out.println(cropUri+"哈哈");
        startActivityForResult(intent, 6);
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

    //导出PDF
    private void exportPDF (ArrayList<String> imagesAddress)  {
        //PDF导出后的存放路径，根据Android Q 分区储存特性，将储存在该App的私有目录下
        String pdfPath = getExternalFilesDir(null) +"/" + generatePdfName();
        Document pdf = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(pdf,new FileOutputStream(pdfPath));
            pdf.open();
            for (int i = 0; i < imagesAddress.size(); i++){
                pdf.newPage();
//                pdf.add(new Paragraph("This is Page: " + (i+1)));
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(imagesAddress.get(i));
                float height  = image.getHeight();
                float width = image.getWidth();
                int percent = getPercent(height, width);
                image.setAlignment(Image.MIDDLE);
                image.scalePercent(percent);
                image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                pdf.add(image);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (DocumentException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            pdf.close();
            Toast toast = Toast.makeText(this,R.string.PDFSave, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,-100);
            toast.show();
        }
    }
    //根据时间生成PDF文件名以达到唯一性
    private String generatePdfName(){
        String pdfFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        return "iText_"+pdfFileName+".pdf";
    }
    /**
     * 图片裁剪解决方案：统一按照宽度压缩 所有图片的宽度是相等
     */
    private int getPercent(float h, float w){
        int p = 0; float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }
    public String getPAth(Uri uri) {
        String path = null;
        if (!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (null == cursor) {
                Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show();
                return null;
            }
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } else {
            path = uri.getPath();
        }
        return path;
    }

    public void finishPaint(View view) {
        finish();
    }
    public void requestWritePermission(){
        if (ActivityCompat.checkSelfPermission(OneClickDelete.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OneClickDelete.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

}
