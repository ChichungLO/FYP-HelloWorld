package com.example.editpart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.UriUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    LinearLayout delete;
    LinearLayout remove;
    LinearLayout modify;
    LinearLayout manual;

    ImageView btnExportPdf;
    ImageView setting;

    String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        //隐藏状态栏
        //this.getSupportActionBar().hide();

        initialView();

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //加内容（intent或者干嘛干嘛）
                Intent intent = new Intent(HomePage.this, OneClickDelete.class);
                startActivity(intent);
            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //加内容（intent或者干嘛干嘛）
                Intent intent = new Intent(HomePage.this, RemovedManually.class);
                startActivity(intent);
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //加内容（intent或者干嘛干嘛）
                Intent intent = new Intent(HomePage.this, ModifyContent.class);
                startActivity(intent);
            }
        });

        btnExportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelector.builder()
                        .setSingle(false)  //设置是否单选
                        .start(HomePage.this, 2); // 打开相册
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View v) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, v);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.app_menu, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
                Intent intent = new Intent(HomePage.this,HomePage.class);
                startActivity(intent);
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                //Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

    public void initialView(){
        delete = findViewById(R.id.delete);
        remove = findViewById(R.id.remove);
        modify = findViewById(R.id.modify);
        manual = findViewById(R.id.manual);
        btnExportPdf = findViewById(R.id.exportPdf);

        setting = findViewById(R.id.setting);
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
                    //imageView.setImageURI(uri);
                    btnExportPdf.setEnabled(true);
                }
            case 2:
                if (data != null ){
                    ArrayList <String> imagesAddress = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    Uri uri = UriUtils.getImageContentUri(HomePage.this, imagesAddress.get(0));
                    exportPDF(imagesAddress);
                    imagePath = getPAth(uri);
                    //imageView.setImageURI(uri);
                }
        }
    }
}
