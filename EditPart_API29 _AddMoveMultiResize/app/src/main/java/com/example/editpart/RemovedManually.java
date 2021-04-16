package com.example.editpart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.editpart.Interface.ToolsListener;
import com.example.editpart.adapters.ToolsAdapters;
import com.example.editpart.common.Common;
import com.example.editpart.model.ToolsItem;
import com.example.editpart.widget.PaintView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RemovedManually extends AppCompatActivity implements ToolsListener  {

    private static final int REQUEST_PERMISSION = 1001;
    private static final int PICK_IMAGE = 1000;

    PaintView mPaintView;
    int colorBackground,colorBrush;
    int brushSize,eraserSize;

    Common common;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed_manually);

        initTools();

    }

    private void initTools() {

        colorBackground = Color.WHITE;
        colorBrush = Color.BLACK;

        eraserSize = brushSize = 12;

        mPaintView = findViewById(R.id.paint_view);

        common = new Common(getString(R.string.brush), getString(R.string.eraser), getString(R.string.colors), getString(R.string.background), getString(R.string._return), getString(R.string.delete), getString(R.string.add_image), getString(R.string.add_text));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            BitmapBinder bitmapBinder = (BitmapBinder) bundle.getBinder("bitmap");
            Bitmap bitmap = bitmapBinder.getBitmap();
            //Log.e("aft_height", bitmap.getHeight()+"");
            //Log.e("aft_width", bitmap.getWidth()+"");
            //Log.e("bitmap==null?",(bitmap==null)+"");
            Point p = new Point();
//获取窗口管理器
            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getSize(p);
            int width = p.x;
            Log.e("width", width+"");
            int mul = width/bitmap.getWidth();
            mPaintView.setImage(bitmap, bitmap.getHeight()*mul,bitmap.getWidth()*mul);
            //mPaintView.setImage(bitmap);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_tools);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        ToolsAdapters toolsAdapters = new ToolsAdapters(loadTools(), this);
        recyclerView.setAdapter(toolsAdapters);

    }

    private List<ToolsItem> loadTools() {

        List<ToolsItem> result = new ArrayList<>();

        result.add(new ToolsItem(R.drawable.ic_baseline_image_search_24,common.getImage()));
        result.add(new ToolsItem(R.drawable.ic_baseline_brush_24, common.getBrush()));
        result.add(new ToolsItem(R.drawable.ic_baseline_delete_forever_24,common.getEraser()));
        result.add(new ToolsItem(R.drawable.ic_baseline_palette_24,common.getColors()));
        result.add(new ToolsItem(R.drawable.ic_baseline_text_fields_24,common.getText()));
        result.add(new ToolsItem(R.drawable.ic_baseline_format_paint_24,common.getBackground()));
        result.add(new ToolsItem(R.drawable.ic_baseline_undo_24,common.get_return()));

        return result;

    }

    public void finishPaint(View view) {
        finish();
    }

    public void shareApp(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String bodyText = "http://play.google.com/store/apps/details?id="+getPackageName();
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT,bodyText);
        startActivity(Intent.createChooser(intent,"share this app"));
    }

    public void saveFile(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);

        }else {

            try {
                saveBitmap();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveBitmap() throws IOException {

        Bitmap bitmap = mPaintView.getBitmap();
        String file_name = UUID.randomUUID() + ".png";
        OutputStream outputStream;
        boolean saved;
        File folder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }else {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }
        if(!folder.exists()){
            folder.mkdirs();
        }

        File image = new File(folder+File.separator+file_name);
        Uri imageUri = Uri.fromFile(image);

        outputStream = new FileOutputStream(image);
        saved = bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,file_name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_name));
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            outputStream = resolver.openOutputStream(uri);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        }else {

            sendPictureToGallery(imageUri);

        }

        if(saved)
            Toast.makeText(this,"picture saved",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"picture not saved",Toast.LENGTH_SHORT).show();

        outputStream.flush();
        outputStream.close();

    }

    private void sendPictureToGallery(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSION && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            try {
                saveBitmap();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSelected(String name) {

        if (common.getBrush().equals(name)) {
            mPaintView.toMove = false;
            mPaintView.desableEraser();
            mPaintView.invalidate();
            showDialogSize(false);
        } else if (common.getEraser().equals(name)) {
            mPaintView.enableEraser();
            showDialogSize(true);
        } else if (common.get_return().equals(name)) {
            mPaintView.returnLastAction();
        } else if (common.getBackground().equals(name)) {
            updateColor(name);
        } else if (common.getColors().equals(name)) {
            updateColor(name);
        } else if (common.getImage().equals(name)) {
            getImage();
        } else if (common.getText().equals(name)) {/*
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mPaintView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent i = new Intent(RemovedManually.this, ModifyContent.class);
                i.putExtra("bitmap", byteArray);
                startActivity(i);

                 */

            Intent i = new Intent(RemovedManually.this, ModifyContent.class);
            Bitmap bitmap = mPaintView.getBitmap();
            Bundle bundle = new Bundle();
            //Log.i("MainActivity", "bitmap大小: " + bitmap.getByteCount() / 1024 + " kb");
            bundle.putBinder("bitmap", new BitmapBinder(bitmap));
            i.putExtras(bundle);
            startActivity(i);
        }

    }

    private void getImage() {
        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_IMAGE);

         */
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
                mPaintView.setImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateColor(String name) {

        int color;

        if(name.equals(common.getBackground())){
            color = colorBackground;
        }else{
            color = colorBrush;
        }



        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {

                        if(name.equals(common.getBackground())){
                            colorBackground = lastSelectedColor;
                            mPaintView.setColorBackground(colorBackground);
                        }else{
                            colorBrush = lastSelectedColor;
                            mPaintView.setBrushColor(colorBrush);
                        }

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).build()
                .show();



    }

    private void showDialogSize(final boolean isEraser) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog,null,false);

        TextView toolsSelected = view.findViewById(R.id.status_tools_selected);
        TextView statusSize = view.findViewById(R.id.status_size);
        ImageView ivTools = view.findViewById(R.id.iv_tools);
        SeekBar seekBar = view.findViewById(R.id.seekbar_size);
        seekBar.setMax(99);

        if(isEraser){

            toolsSelected.setText("Eraser Size");
            ivTools.setImageResource(R.drawable.ic_baseline_delete_forever_black);
            statusSize.setText("Selected Size : "+eraserSize);

        }else{

            toolsSelected.setText("Brush Size");
            ivTools.setImageResource(R.drawable.ic_baseline_brush_black);
            statusSize.setText("Selected Size : "+brushSize);

        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(isEraser){

                    eraserSize = i+1;
                    statusSize.setText("Selected Size : "+eraserSize);
                    mPaintView.setSizeEraser(eraserSize);

                }else {

                    brushSize = i+1;
                    statusSize.setText("Selected Size : "+brushSize);
                    mPaintView.setSizeBrush(brushSize);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(view);
        builder.show();

    }

}