package Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownload extends AsyncTask<Void, Integer, Void> {

    private static final String TAG = ImageDownload.class.getSimpleName();

    private String mUrl;
    private Context mContext;
    private Bitmap mBitmap;
    private ImageLoaderListener mListener;
    //TODO: progress bar,etc. hook into activity here by passing as arguments to constructor

    public ImageDownload(String url, Context c, Bitmap bmp, ImageLoaderListener listener) {
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
    protected Void doInBackground(Void... arg0) {
        Log.d(TAG, "ImageDownload: doInBackground task");
        mBitmap = getBitmapFromUrl(mUrl);

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
    public static Bitmap getBitmapFromUrl(String urlString) {
        try {
            //Set up http connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Retrieve and decode the input stream
            InputStream input = connection.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);

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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



}
