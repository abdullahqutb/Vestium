package semicolons.vestium;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureImage extends AppCompatActivity {


    // this is the final tag for intent
    static final int REQUEST_TAKE_PHOTO = 1;

    // this is the path of the taken image
    String mCurrentPhotoPath;
    // this is the uri of the taken image
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        mCurrentPhotoPath = "";
        dispatchTakePictureIntent();
    }



    // this one creates a unique name to our newly taken image
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private File createImageFile() throws IOException {
        // Create an image file name
        // String timeStamp = "SemiColons";
        // unique file stamp is needed for pictures not to override each other
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // added the SemiColons stamp to its end
        timeStamp = "SemiColons" + timeStamp;

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // this one uses an intent to take a pic and store it in the private directory in
    // apps path using the unique name made by createImageFile method
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "semicolons.vestium.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivity(takePictureIntent);

    }
}
