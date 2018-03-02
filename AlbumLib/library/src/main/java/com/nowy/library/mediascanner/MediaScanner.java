//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nowy.library.mediascanner;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.util.LinkedList;
import java.util.List;

public class MediaScanner implements MediaScannerConnectionClient {
    private MediaScannerConnection mMediaScanConn;
    private ScannerListener mScannerListener;
    private LinkedList<String[]> mLinkedList;
    private String[] mCurrentScanPaths;
    private int mScanCount;

    public MediaScanner(Context context) {
        this.mLinkedList = new LinkedList();
        this.mScanCount = 0;
        this.mMediaScanConn = new MediaScannerConnection(context.getApplicationContext(), this);
    }

    /** @deprecated */
    @Deprecated
    public MediaScanner(Context context, ScannerListener scannerListener) {
        this(context);
        this.mScannerListener = scannerListener;
    }

    public boolean isRunning() {
        return this.mMediaScanConn.isConnected();
    }

    public void scan(String filePath) {
        this.scan(new String[]{filePath});
    }

    public void scan(List<String> filePaths) {
        this.scan((String[])filePaths.toArray(new String[filePaths.size()]));
    }

    public void scan(String[] filePaths) {
        if(filePaths != null && filePaths.length > 0) {
            this.mLinkedList.add(filePaths);
            this.executeOnce();
        }

    }

    private void executeOnce() {
        if(!this.isRunning() && this.mLinkedList.size() > 0) {
            this.mCurrentScanPaths = (String[])this.mLinkedList.remove(0);
            this.mMediaScanConn.connect();
        }

    }

    public void onMediaScannerConnected() {
        String[] var1 = this.mCurrentScanPaths;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String filePath = var1[var3];
            String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            this.mMediaScanConn.scanFile(filePath, mimeType);
        }

    }

    public void onScanCompleted(String path, Uri uri) {
        if(this.mScannerListener != null) {
            this.mScannerListener.oneComplete(path, uri);
        }

        ++this.mScanCount;
        if(this.mScanCount == this.mCurrentScanPaths.length) {
            this.mMediaScanConn.disconnect();
            if(this.mScannerListener != null) {
                this.mScannerListener.allComplete(this.mCurrentScanPaths);
            }

            this.mScanCount = 0;
            this.mCurrentScanPaths = null;
            this.executeOnce();
        }

    }
}
