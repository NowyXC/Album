package com.nowy.library.mediascanner;

import android.net.Uri;

public interface ScannerListener {
    void oneComplete(String var1, Uri var2);

    void allComplete(String[] var1);
}
