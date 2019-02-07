package com.example.nzaidi.captureimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakeAPicture;
    private Button btnSaveThePicture;
    private ImageView imgPhoto;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView txtRedColorValue;
    private TextView txtGreenColorValue;
    private TextView txtBlueColorValue;
    private Button btnShare;

    private static final int CAMERA_IMAGE_REQUEST_CODE = 100;
    private Bitmap bitmap;
    private Colorful colorful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakeAPicture = (Button) findViewById(R.id.btnTakePicture);
        btnSaveThePicture = (Button) findViewById(R.id.btnSavePicture);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        redSeekBar = (SeekBar) findViewById(R.id.redColorSeekBar);
        greenSeekBar = (SeekBar) findViewById(R.id.greenColorSeekBar);
        blueSeekBar = (SeekBar) findViewById(R.id.blueColorSeekBar);
        txtRedColorValue = (TextView) findViewById(R.id.txtRedColorValue);
        txtGreenColorValue = (TextView) findViewById(R.id.txtGreenColorValue);
        txtBlueColorValue = (TextView) findViewById(R.id.txtBlueColorValue);
        btnShare = (Button) findViewById(R.id.btnShare);

        btnTakeAPicture.setOnClickListener(MainActivity.this);
        btnSaveThePicture.setOnClickListener(MainActivity.this);
        btnShare.setOnClickListener(MainActivity.this);

        btnSaveThePicture.setVisibility(View.INVISIBLE);
        redSeekBar.setVisibility(View.INVISIBLE);
        greenSeekBar.setVisibility(View.INVISIBLE);
        blueSeekBar.setVisibility(View.INVISIBLE);
        btnShare.setVisibility(View.INVISIBLE);
        txtRedColorValue.setVisibility(View.INVISIBLE);
        txtGreenColorValue.setVisibility(View.INVISIBLE);
        txtBlueColorValue.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnTakePicture)
        {
            int permissionResult = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
            if (permissionResult == PackageManager.PERMISSION_GRANTED)
            {
                PackageManager packageManager = getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Your device dont have a camera", Toast.LENGTH_SHORT).show();
                }
            }else
                {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA} , 1);
            }
        }

        else if (view.getId() == R.id.btnSavePicture)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    SaveFile.saveFile(MainActivity.this, bitmap);
                    Toast.makeText(MainActivity.this, "The image is now successfully saved to external storage",
                            Toast.LENGTH_SHORT).show();

                }catch (Exception e)
                {
                    e.printStackTrace();

                }
            }else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2000);
                }
        }
        else if (view.getId() == R.id.btnShare)
        {
            try {
                File myPictureFile = SaveFile.saveFile(MainActivity.this, bitmap);
                Uri myUri = Uri.fromFile(myPictureFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_STREAM,myUri);
                startActivity(Intent.createChooser(shareIntent,"Lets share your picture with others"));

            }
            catch (Exception e)
            {
                e.printStackTrace();

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this,"OnActivityResult is called" ,Toast.LENGTH_SHORT).show();

        if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK)
        {
            btnSaveThePicture.setVisibility(View.VISIBLE);
            redSeekBar.setVisibility(View.VISIBLE);
            greenSeekBar.setVisibility(View.VISIBLE);
            blueSeekBar.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            txtRedColorValue.setVisibility(View.VISIBLE);
            txtGreenColorValue.setVisibility(View.VISIBLE);
            txtBlueColorValue.setVisibility(View.VISIBLE);

            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            colorful = new Colorful(bitmap, 0.0f, 0.0f, 0.0f);
            imgPhoto.setImageBitmap(bitmap);
        }
    }

    private class ColorizationHandler implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser) {

                if (seekBar == redSeekBar) {



                    colorful.setRedColorValue(progress / 100.0f);
                    redSeekBar.setProgress( (int) (100 * (colorful.getRedColorValue())));
                    txtRedColorValue.setText(colorful.getRedColorValue() + "");



                } else if (seekBar == greenSeekBar) {


                    colorful.setGreenColorValue(progress / 100.0f);
                    greenSeekBar.setProgress((int) (100 * (colorful.getGreenColorValue())));
                    txtGreenColorValue.setText(colorful.getGreenColorValue() + "");


                } else if (seekBar == blueSeekBar) {


                    colorful.setBlueColorValue(progress / 100.0f);
                    blueSeekBar.setProgress((int) (100 * (colorful.getBlueColorValue())));
                    txtBlueColorValue.setText(colorful.getBlueColorValue() + "");

                }

                bitmap = colorful.returnTheColorizedBitmap();
                imgPhoto.setImageBitmap(bitmap);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
