package com.example.nzaidi.captureimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SaveFile {

    public static File saveFile(Activity myActivity, Bitmap bitmap) throws IOException
    {
        String externalStorageState = Environment.getExternalStorageState();
        File myFile = null;
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED))
        {
            File picturesDirectory = myActivity.getExternalFilesDir("ColorAppPictures");
            Date currentDate = new Date();
            long elapseTime = SystemClock.elapsedRealtime();
            String uniqueImageName = "/" + currentDate + "_" + elapseTime + ".png";

            myFile = new File(picturesDirectory + uniqueImageName);
            long remainingSpace = picturesDirectory.getFreeSpace();
            long requiredSpace = bitmap.getByteCount();

            if (requiredSpace * 1.8 < remainingSpace)
            {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(myFile);
                    boolean isImageSaveSuccessfully = bitmap.compress
                            (Bitmap.CompressFormat.PNG,100 , fileOutputStream);
                    if (isImageSaveSuccessfully)
                    {
                        return myFile;
                    }else{

                        throw new IOException("The image is not save successfully to the external storage");
                    }

                }catch (Exception e)
                {
                    throw new IOException("The operation of saving the image to external storage went wrong");

                }
            }
            else {
                throw new IOException("There is no enough space in order to save the image to external storage");
            }
        }
        else
        {
            throw new IOException("This device does not have an external storage");
        }
    }
}
