package com.example.textrecognitionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.util.List;

//import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

public class MainActivity extends AppCompatActivity {

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView textView;
    private Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = findViewById(R.id.capture_image_btn);
        detectTextBtn = findViewById(R.id.detect_text_image_btn);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

        captureImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                runCloudTextRecognition();
//                detectTextFromImage();
            }
        });
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

//    private void detectTextFromImage()
//    {
//        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
//        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
//        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText firebaseVisionText)
//            {
//                displayTextFromImage(firebaseVisionText);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d("Error: ",e.getMessage());
//            }
//        });
//    }
//
//    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
//    {
//        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
//        if(blockList.size() == 0){
//            Toast.makeText(this,"no Text Found in Image",Toast.LENGTH_SHORT).show();
//        }else {
//            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
//            {
//                String text = block.getText();
//                textView.setText(text);
//            }
//        }
//    }

    private void runCloudTextRecognition(){
        detectTextBtn.setEnabled(false);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();
        recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
            @Override
            public void onSuccess(FirebaseVisionDocumentText firebaseVisionDocumentText) {
                detectTextBtn.setEnabled(true);
                processCloudTextRecognitionResult(firebaseVisionDocumentText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                detectTextBtn.setEnabled(true);
                e.printStackTrace();
            }
        });
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text){
        List<FirebaseVisionDocumentText.Block> blockList = text.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(this, "No Text Found in Image",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            for(FirebaseVisionDocumentText.Block block: text.getBlocks())
            {
                String texts = block.getText();
                textView.setText(texts);
            }
        }

    }


}
