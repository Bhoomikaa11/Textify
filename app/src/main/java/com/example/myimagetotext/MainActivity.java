package com.example.myimagetotext;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.cast.framework.media.ImagePicker.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.DataOutput;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ImageView clearbtn, camerabtn, copybtn;
    EditText answerid;
    Uri imageUri;
    TextRecognizer textrecognizer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        clearbtn = findViewById(R.id.clearbtn);
        camerabtn = findViewById(R.id.camerabtn);
        copybtn = findViewById(R.id.copybtn);
        answerid = findViewById(R.id.answerid);
       textrecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);



        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(MainActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        copybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = answerid.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to copy!!", Toast.LENGTH_SHORT).show();
                }else {
                    ClipboardManager clipboardmanager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);

                    ClipData clipdata = ClipData.newPlainText("Data",answerid.getText().toString());
                    clipboardmanager.setPrimaryClip(clipdata);

                    Toast.makeText(MainActivity.this, "Text copied to clipboard!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = answerid.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to clear !!", Toast.LENGTH_SHORT).show();
                }else {
                    answerid.setText("");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if(data!=null){
                    imageUri = data.getData();
                Toast.makeText(this,"Image selected",Toast.LENGTH_LONG).show();

                recognizeText();
            }

        }
        else{
            Toast.makeText(this,"Image not selected",Toast.LENGTH_LONG).show();

        }
    }

    private void recognizeText() {
        if(imageUri!=null){
            try {
                InputImage inputimage = InputImage.fromFilePath(MainActivity.this,imageUri);
                Task<Text> result = textrecognizer.process(inputimage).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
            String reconizeText = text.getText();
            answerid.setText(reconizeText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}