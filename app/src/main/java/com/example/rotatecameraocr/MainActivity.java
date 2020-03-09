package com.example.rotatecameraocr;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Element;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import com.google.android.gms.vision.text.Text;


import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath;
    private String text = "vuoto";
    private TextRecognizer textRecognizer;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); //hide the title bar

        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("hello");
            text = savedInstanceState.getString("prova");
            if( currentPhotoPath != null) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                if( imageView != null && imageBitmap !=null){
                    Uri photoURI = Uri.parse(currentPhotoPath).buildUpon().authority(this.getPackageName() +".fileprovider").build();
                    int orientaionPhoto = getCameraPhotoOrientation(this, photoURI, currentPhotoPath);
                    Matrix matrix=new Matrix();
                    matrix.preRotate(orientaionPhoto);
                    imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

                    imageView.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("hello", currentPhotoPath);
        savedInstanceState.putString("prova", text);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void CapturePhoto(View view) {
        this.dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            Uri photoURI = Uri.parse(currentPhotoPath).buildUpon().authority(this.getPackageName()+".fileprovider").build();

            float orientaionPhoto = getRotationAngle();

            Matrix matrix=new Matrix();
            matrix.setRotate(orientaionPhoto);
            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

            imageView.setImageBitmap(imageBitmap);
            textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if(!textRecognizer.isOperational()) {
                Log.d("PrimoLog", "onActivityResult: textRecognizer non operativo");
            }
            else {
                Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();

                SparseArray<TextBlock> items1 = textRecognizer.detect(frame);
                textRecognizer.release();
                frame = null;

                StringBuilder sb = new StringBuilder();

                List<Text> e = new ArrayList<Text>();
                for( int i =0; i<items1.size(); i++){
                    TextBlock item = items1.valueAt(i);
                    e.addAll( item.getComponents());
                }

                List<Text> k = new ArrayList<Text>();
                for( int i =0; i<e.size(); i++){
                    Text item = e.get(i);
                    k.addAll(item.getComponents());

                }

                Text item2 = null;
                Text item3 = null;

                for (int i = 0; i < k.size(); i++) {
                    Text item = k.get(i);
                    //Log.d("PrimoLog", "onActivityResult: "+item.getValue());

                    if (item.getValue().toLowerCase().contains("provincia")) {
                        item2 = item;
                    } else if (item.getValue().toLowerCase().contains("codice")) {
                        item3 = item;
                    }
                }

                if ( item2 != null && item3 != null) {
                    int cx,cy;
                    cx =  (item2.getBoundingBox().left -item3.getBoundingBox().left);
                    cy =  (item2.getBoundingBox().bottom -item3.getBoundingBox().bottom);

                    Log.d("PrimoLog", "onActivityResult: "+ cx);
                    Log.d("PrimoLog", "onActivityResult: "+ cy);

                    float tg = ((float) cx/cy);
                    Log.d("PrimoLog", "onActivityResult: "+tg+" arcotangente " + atan(tg)*(180/3.14));
                    float h = (float) (sqrt(cx * cx + cy * cy));

                    float x = item3.getBoundingBox().left;
                    float y = item3.getBoundingBox().bottom;

                    int xC = (int) (h * 0.85 + x);
                    int yC = (int) (h * 0.22 + y);

                    int xF = (int) (h * 1.3 + x);
                    int yF = (int) (-h * 0.05 + y);

                    int xN = (int) (h * 0.83 + x);
                    int yN = (int) (h * 0.47 + y);

                    int xS = (int) (-h * 0.48 + x);
                    int yS = (int) (h * 0.97 + y);
/*
                    Log.d("PrimoLog", "onActivityResult: C " + xC);
                    Log.d("PrimoLog", "onActivityResult:   " + yC);
                    Log.d("PrimoLog", "onActivityResult: F " + xF);
                    Log.d("PrimoLog", "onActivityResult:   " + yF);
                    Log.d("PrimoLog", "onActivityResult: N " + xN);
                    Log.d("PrimoLog", "onActivityResult:   " + yN);
*/

                    for (int j = 0; j < k.size(); j++) {
                        Text item = k.get(j);
                        //Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox() + "   " + item.getValue());

                        if (item.getBoundingBox().contains(xC, yC)) {
                            Log.d("PrimoLog", "onActivityResult: ");

                            Log.d("PrimoLog", "onActivityResult: C " + xC);
                            Log.d("PrimoLog", "onActivityResult:   " + yC);
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterX());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterY());
                            Log.d("PrimoLog", "onActivityResult: " + item.getValue());
                            Log.d("PrimoLog", "onActivityResult: ");
                            sb.append("il cognome è : " + item.getValue() + "\n");

                        } else if (item.getBoundingBox().contains(xF, yF)) {
                            Log.d("PrimoLog", "onActivityResult: ");

                            Log.d("PrimoLog", "onActivityResult: F " + xF);
                            Log.d("PrimoLog", "onActivityResult:   " + yF);
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterX());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterY());
                            Log.d("PrimoLog", "onActivityResult: " + item.getValue());
                            Log.d("PrimoLog", "onActivityResult: ");
                            sb.append("il codice fiscale è : " + item.getValue() + "\n");
                        } else if (item.getBoundingBox().contains(xS, yS)) {
                            Log.d("PrimoLog", "onActivityResult: ");

                            Log.d("PrimoLog", "onActivityResult: S " + xS);
                            Log.d("PrimoLog", "onActivityResult:   " + yS);
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterX());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterY());
                            Log.d("PrimoLog", "onActivityResult: " + item.getValue());
                            Log.d("PrimoLog", "onActivityResult: ");
                            sb.append("la data di scadenza è : " + item.getValue() + "\n");

                        } else if (item.getBoundingBox().contains(xN, yN)) {
                            Log.d("PrimoLog", "onActivityResult: ");

                            Log.d("PrimoLog", "onActivityResult: N " + xN);
                            Log.d("PrimoLog", "onActivityResult:   " + yN);
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterX());
                            Log.d("PrimoLog", "onActivityResult: " + item.getBoundingBox().exactCenterY());
                            Log.d("PrimoLog", "onActivityResult: " + item.getValue());
                            Log.d("PrimoLog", "onActivityResult: ");
                            sb.append("il nome è : " + item.getValue() + "\n");
                        }
                    }

                    text = sb.toString();
                }
            }
        }
    }

    private float getRotationAngle() {
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
        Uri photoURI = Uri.parse(currentPhotoPath).buildUpon().authority(this.getPackageName() + ".fileprovider").build();
        int orientaionPhoto = getCameraPhotoOrientation(this, photoURI, currentPhotoPath);
        Matrix matrix = new Matrix();
        matrix.setRotate(orientaionPhoto);
        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.d("PrimoLog", "onActivityResult: textRecognizer non operativo");
        } else {
            Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> items1 = textRecognizer.detect(frame);
            textRecognizer.release();

            List<Text> e = new ArrayList<Text>();
            for (int i = 0; i < items1.size(); i++) {
                TextBlock item = items1.valueAt(i);
                e.addAll(item.getComponents());
            }

            List<Text> k = new ArrayList<Text>();
            for (int i = 0; i < e.size(); i++) {
                Text item = e.get(i);
                k.addAll(item.getComponents());

            }

            Text item2 = null;
            Text item3 = null;


            for (int i = 0; i < k.size(); i++) {
                Text item = k.get(i);
                //Log.d("PrimoLog", "onActivityResult: "+item.getValue());

                if (item.getValue().toLowerCase().contains("provincia")) {
                    item2 = item;
                } else if (item.getValue().toLowerCase().contains("codice")) {
                    item3 = item;
                }
            }


            if ( item2 != null && item3 != null) {
                int cx,cy;
                cx =  (item2.getBoundingBox().left -item3.getBoundingBox().left);
                cy =  (item2.getBoundingBox().bottom -item3.getBoundingBox().bottom);

                Log.d("PrimoLog", "onActivityResult: "+ cx);
                Log.d("PrimoLog", "onActivityResult: "+ cy);

                float tg = ((float) cx/cy);
                double a =  atan(tg);
                Log.d("PrimoLog", "onActivityResult: "+tg+" arcotangente " + a*(180/3.14));
                float h = (float) (sqrt(cx * cx + cy * cy));
                float ang = (float) (orientaionPhoto + a*(180/3.14));
                float x =ang;

                return x;
            }
            else {
                Log.d("PrimoLog", "onActivityResult: errore calcolo angolo");

            }
        }

        return 0;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getPackageName() +".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,  REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        int orientation =90;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            Log.d("PrimoLog","getCameraPhotoOrientation: " + e.toString());

        }
        return rotate;
    }

    public void seeText(View view) {
        Intent intent = new Intent(this, DisplayTesto.class);
        intent.putExtra("test", text);
        startActivity(intent);
    }

}
