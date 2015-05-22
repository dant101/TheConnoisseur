package com.theconnoisseur;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import Util.ImageDownload;

public class ImageDownloadTest extends AndroidTestCase {

    /**
     * Test assumes we have an internet connection
     */
    public void testSingleImageDownload() {
        String magic = "http://www.see-and-do-france.com/images/French_flag_design.jpg";
        Bitmap mBitmap = null;

        ImageDownload mDownload = new ImageDownload(magic, null, mBitmap, null);;

        //Download Image and test returned bitmap is not null
        mBitmap = ImageDownload.getBitmapFromUrl(magic);

        assertNotNull(mBitmap);
    }

}
