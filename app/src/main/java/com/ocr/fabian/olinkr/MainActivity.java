package com.ocr.fabian.olinkr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView imgOCR;
    private ImageView imgCamera;

    private  Intent takePictureIntent;
    private Intent ocrIntent;

    private Bitmap lastImageTaken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgCamera = findViewById(R.id.img_cam);
        imgOCR = findViewById(R.id.img_load);

        /*** setup Intents ***/
        ocrIntent = new Intent(this, OCRActivity.class);
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        imgOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ocrIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*** Camera ***/
            Bundle extras = data.getExtras();
            lastImageTaken = (Bitmap) extras.get("data");
            //saveImageToDevice(lastImageTaken);
            ocrIntent.putExtra("cam_pic", lastImageTaken);
            startActivity(ocrIntent);
        }
    }

    private void saveImageToDevice(Bitmap image) {
        DateFormat df = DateFormat.getDateInstance();
        String date = df.format(Calendar.getInstance().getTime());
        //File outputFile = new File(this.getFilesDir(), date);
        try {
            FileOutputStream fout = openFileOutput(date, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
