package practice.aaklii.memeapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

    TextView topText;
    TextView bottomText;
    EditText editTop ;
    EditText editBottom;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topText = (TextView) findViewById(R.id.memeTopText);
        bottomText = (TextView) findViewById(R.id.memeBottomText);
        editBottom = (EditText) findViewById(R.id.editBottom);
        editTop = (EditText) findViewById(R.id.editTop);
        imageView = (ImageView) findViewById(R.id.memeImage);

    }


 // function to add image.
    public void addImage(View view) {
        //Intent to Load Image from Gallery
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    // Issue Solved worked after giving permission manually
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();

            //Here we get the column index of image we have selected
           //Then we use this data so as to use image
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            //Cursor will give position of image
            Cursor cursor = getContentResolver()
                    .query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            //From cursor we get columnIndex
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            ImageView imageView = (ImageView) findViewById(R.id.memeImage);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }


//Adding text to image
    public void tryMeme(View view) {

        topText.setText(editTop.getText().toString());
        bottomText.setText(editBottom.getText().toString());

        //To minimize the keyboard
        hideKeyboard(view);
}

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    // function to save image
    // Issue Solved worked after giving permission manually.
    public void saveImage(View view){
        View content = findViewById(R.id.lay);
        Bitmap bitmap = getScreenshot(content);
        String currentImage = "meme"+ System.currentTimeMillis()+".png";
        store(bitmap,currentImage);
    }


    public static Bitmap getScreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
    public void store(Bitmap bm, String filename){
        String dirpath = Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File dir = new File(dirpath);
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirpath,filename);
        try {
            FileOutputStream fos = null;
            fos= new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }




}
