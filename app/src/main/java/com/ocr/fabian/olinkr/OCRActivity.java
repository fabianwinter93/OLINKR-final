package com.ocr.fabian.olinkr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class OCRActivity extends AppCompatActivity implements View.OnClickListener {
    static final int GET_CONTENT_REQUEST = 1;

    /*** ML ***/
    private FirebaseVisionImage image;
    private FirebaseVisionTextDetector detector;
    private TextClassificationManager tcm;
    private TextClassifier classifier;

    /*** Layout ***/
    private ConstraintLayout mainLayout;
    private ScrollView scrollView;
    private LinearLayout scrollLin;
    private SearchView searchView;

    private LayoutInflater layoutInflater;

    private ImageButton searchButton;

    private Bitmap lastImageTaken;

    private int maxTextLen = 40;

    int rnd = ((int) Math.random());
    int textViewCount = 0;

    private Intent getContentIntent;
    private Intent chooserIntent;

    private String completeString;

    private Textblock block;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        completeString = "";

        /*** setup layout ***/
        //layoutInflater = getLayoutInflater();
        mainLayout = findViewById(R.id.main_layout);
        scrollView = findViewById(R.id.scroll_view);
        scrollLin = findViewById(R.id.scroll_linear);
        scrollLin.setOrientation(LinearLayout.VERTICAL);

        searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                block.highlightText(searchView.getQuery().toString());
                return false;
            }
        });

        detector = FirebaseVision.getInstance().getVisionTextDetector();

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(chooserIntent, GET_CONTENT_REQUEST);
            }
        });


        getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("*/*");
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooserIntent = Intent.createChooser(getContentIntent, "Select a Picture");

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("cam_pic")) {
            lastImageTaken = (Bitmap) extras.get("cam_pic");
            runTextRecognition(lastImageTaken);
        } else {
            startActivityForResult(chooserIntent, GET_CONTENT_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == GET_CONTENT_REQUEST && resultCode == RESULT_OK) {
                /*** Chooser ***/
                Uri imageUri = data.getData();
                lastImageTaken = getBitmapFromUri(imageUri);
                runTextRecognition(lastImageTaken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof Textblock) {
            ((Textblock) v).onClick();
        }
    }

    private void addTextToScrollView(String text) {
        //completeText.add(text);
        block = new Textblock(this);

        block.setId(rnd + textViewCount);   // make id and inc
        textViewCount++;

        // add listeners
        if(text.length() > maxTextLen) {
            block.setOnClickListener(this);
        }
        block.setText(text);
        // reset
        completeString = "";
        scrollLin.removeAllViews();
        scrollLin.addView(block);
    }

    private void runTextRecognition(Bitmap bitmap) {
        //scrollLin.removeAllViews();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        Task<FirebaseVisionText> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                if (firebaseVisionText.getBlocks().isEmpty()) {
                                    completeString = "No Text found";
                                }
                                for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                                    String text = block.getText();
                                    completeString += text + " ";
                                    //completeText.add(text);
                                    //addTextToScrollView(text);
                                }
                                addTextToScrollView(completeString);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });

    }

    private void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}




