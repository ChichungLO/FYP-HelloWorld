package com.example.itext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//ImageSelector
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.UriUtils;
//iText
import com.itextpdf.text.*;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnChooseImage, btnExportPdf;    //请求码：btnChooseImage为1，btnExportPdf为2
    String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.chooseImg);
        btnExportPdf = findViewById(R.id.exportPdf);

        //定义按钮监听:选择图片并在imageView预览
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,1);
            }
        });

        //定义按钮监听:选择图片，并将图片导出为PDF
        btnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelector.builder()
                        .setSingle(false)  //设置是否单选
                        .start(MainActivity.this, 2); // 打开相册
            }
        });
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
                Image image = Image.getInstance(imagesAddress.get(i));
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
     * 第一种图片裁剪方案：在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩。
     */
    private  int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    imagePath = getPAth(uri);
                    imageView.setImageURI(uri);
                    btnExportPdf.setEnabled(true);
                }
            case 2:
                if (data != null ){
                    ArrayList <String> imagesAddress = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    Uri uri = UriUtils.getImageContentUri(MainActivity.this, imagesAddress.get(0));
                    exportPDF(imagesAddress);
                    imagePath = getPAth(uri);
                    imageView.setImageURI(uri);
                }
        }
    }
}