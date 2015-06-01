package com.theconnoisseur;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import Util.ImageDownloadHelper;

public class ImageDownloadTest extends AndroidTestCase {

    /**
     * Test assumes we have an internet connection
     */
    public void testSingleImageDownload() {
        String magic = "http://www.see-and-do-france.com/images/French_flag_design.jpg";
        String magic2 = "http://www.doc.ic.ac.uk/project/2014/271/g1427115/images/flags/4-russian.png";
        Bitmap mBitmap = null;

        ImageDownloadHelper mDownload = new ImageDownloadHelper(magic, null, mBitmap, null);
        //mDownload.getLanguages(false);

        //Download Image and test returned bitmap is not null
        mBitmap = ImageDownloadHelper.getBitmapFromUrl(magic, getContext(), false);

        assertNotNull(mBitmap);
    }

}
