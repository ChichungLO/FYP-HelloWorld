package com.example.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void pickImage(View v){
        Intent myIntent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent,120);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 120 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);
            String myPath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(myPath);

            imageView.setImageBitmap(bitmap);


            //Create PDF Document Object and define the page information. (The height & width & number of page)
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();

            //Starting the Page, link the Page to our PdfDocument object.
            PdfDocument.Page page = pdfDocument.startPage(pi);

            //Draw the Bitmap to the PDF file.
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawPaint(paint);

            bitmap = bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap,0,0,null);

            //Finish the page.
            pdfDocument.finishPage(page);


            //Save the bitmap image.
            File root = new File (Environment.getExternalStorageDirectory(),"HelloWorld PDF");
            if(!root.exists()){
                root.mkdir();
            }
            //Create File and Output to storage.
            File file = new File(root,"imageToPdf.pdf");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);
            }catch (IOException e){
                e.printStackTrace();
            }

            pdfDocument.close();

        }
    }
}//End of MainActivity Class