package com.example.editpart;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    private String fileName;
    private String mFilePath;
    String imagePath;

    Context context;
    ImageView imageView;
    ImageButton btn; //拍照按钮
    ImageButton btn2; //从相册中选择
    ImageButton btn3; //清除手写内容
    ImageButton btn4; //清除手写内容
    ImageButton btn5; //保存内容
    ImageButton btn6; //手动清除内容
    ImageButton btn7; //添加文字
    ImageButton btn8; //导出到pdf

    Uri uri; //显示拍的图片
    Uri uri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click_delete);

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
                    //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    //StrictMode.setVmPolicy(builder.build());
                    uri = FileProvider.getUriForFile(context, "com.example.editpart.fileprovider", file);
                    //uri2 = Uri.fromFile(file);
                    uri2 = FileProvider.getUriForFile(getApplicationContext(),getPackageName()+".fileprovider",file);
                } else {
                    uri = Uri.fromFile(file);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri2);
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
        //清除蓝色
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                //imageView.setImageBitmap(bm);
                Bitmap newBitmap = clearBlue(bm, Color.argb(255,255,255,255));
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(newBitmap);
                Toast.makeText(getApplicationContext(),R.string.ClearSuccess,Toast.LENGTH_SHORT).show();
            }
        });
        //清除红色
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)((ImageView) imageView).getDrawable()).getBitmap();
                //imageView.setImageBitmap(bm);
                Bitmap newBitmap = clearRed(bm, Color.argb(255,255,255,255));
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(newBitmap);
                Toast.makeText(getApplicationContext(),R.string.ClearSuccess,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(OneClickDelete.this, "No Photo!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(OneClickDelete.this, "No Photo!", Toast.LENGTH_SHORT).show();
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
        fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
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
            Toast.makeText(getApplicationContext(),R.string.SaveSuccess,Toast.LENGTH_SHORT).show();
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
                    crop(uri2);
                }
                break;
            case 2:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    crop(uri);
                    //imageView.setImageURI(uri);
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
            case 4:
                imageView.setImageURI(data.getData());
                break;
            case 5:
                if (data != null ){
                    ArrayList<String> imagesAddress = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    Uri uri = UriUtils.getImageContentUri(OneClickDelete.this, imagesAddress.get(0));
                    exportPDF(imagesAddress);
                    imagePath = getPAth(uri);
                    //imageView.setImageURI(uri);
                }
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
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 350);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为4
        Uri cropUri = Uri.fromFile(new File(getExternalCacheDir(), "test.jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,cropUri);
        startActivityForResult(intent, 4);
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
        System.out.println(pdfPath+"哈哈");
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
                int percent = getPercent2(height, width);
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
            Toast.makeText(this,"PDF is saved in:\n" + pdfPath,
                    Toast.LENGTH_LONG).show();
        }
    }
    //根据时间生成PDF文件名以达到唯一性
    private String generatePdfName(){
        String pdfFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        return "iText_"+pdfFileName+".pdf";
    }
    /**
     * 第二种图片裁剪解决方案：统一按照宽度压缩 这样来的效果是，所有图片的宽度是相等，测试显示效果较好。
     */
    private int getPercent2(float h, float w){
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
}
