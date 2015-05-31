package Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This helper class can be used twofold.
 * 1: Download an image on the calling thread with getBitmapFromUrl(), returns a bitmap
 * 2: Create an instance of this class and call 'getLanguages()' to initiate the async task which
 * calls the onImageDownloaded(Bitmap b) method which the calling Activity must implement
 */

/**
 * To decode an image stored at 'path':
 *
 //                FileInputStream f = context.openFileInput(urlString);
 //                mBitmap = BitmapFactory.decodeStream(f);
 //                f.close();
 */

public class ImageDownloadHelper extends AsyncTask<Boolean, Integer, Void> {

    private static final String TAG = ImageDownloadHelper.class.getSimpleName();

    private String mUrl;
    private Context mContext;
    private Bitmap mBitmap;
    private ImageLoaderListener mListener;
    //TODO: progress bar,etc. hook into activity here by passing as arguments to constructor

    public ImageDownloadHelper(String url, Context c, Bitmap bmp, ImageLoaderListener listener) {
        this.mUrl = url;
        this.mContext = c;
        this.mBitmap = bmp;
        this.mListener = listener;
    }

    /**
     * Interface that activity should implement to react to download of image
     */
    public interface ImageLoaderListener {
        void onImageDownloaded(Bitmap bmp);
    }

    @Override
    protected  void onPreExecute() {
        //TODO: prepare download screen? loading bars,etc.
    }

    @Override
    protected Void doInBackground(Boolean... save) {
        Log.d(TAG, "ImageDownload: doInBackground task");

        boolean shouldSave = save.length > 0 ? save[0] : false;

        mBitmap = getBitmapFromUrl(mUrl, mContext, shouldSave);

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //TODO: show download progress on main UI thread
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "ImageDownload: onPostExecute");

        if (mListener != null) {
            mListener.onImageDownloaded(mBitmap);
        }

        super.onPostExecute(result);
    }

    /**
     * Retrieves the image from given url and returns its bitmap
     * @param urlString
     * @return
     */
    public static Bitmap getBitmapFromUrl(String urlString, Context context, boolean shouldSave) {
        try {
            //Set up http connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Retrieve and decode the input stream
            InputStream input = connection.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);

            FileOutputStream fos;

            if(shouldSave) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                urlString = urlString.replace(File.separator, "");

                try {
                    fos = context.openFileOutput(urlString, Context.MODE_PRIVATE);

                    fos.write(bytes.toByteArray());
                    fos.close();

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "Unable to find required output file");
                    e.printStackTrace();

                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to output stream");
                    e.printStackTrace();
                }
            }
            return mBitmap;

        } catch (MalformedURLException e) {
            Log.d(TAG, "Malformed URL Exception thrown, provided URL for image download is invalid:" +
                    urlString);
            e.printStackTrace();
        } catch (IOException i) {
            Log.d(TAG, "Unable to open URL connection when downloading image");
            i.printStackTrace();
        }

        return null;
    }

    public static void loadImage(Context context, ImageView view, String path_in) {
        try {
            String path = path_in.replace(File.separator, "");

            FileInputStream f = context.openFileInput(path);

            Bitmap b = BitmapFactory.decodeStream(f);
            f.close();

            view.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "FileNotFoundException when decoding saved image");
            e.printStackTrace();
        } catch (IOException f) {
            Log.d(TAG, "Unable to close fileinputstream when decoding image");
        } catch (NullPointerException g) {
            Log.d(TAG, "String path given to find image was null - unable to resolve");
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



}
