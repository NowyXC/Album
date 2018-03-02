package com.nowy.albumlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nowy.albumlib.album.AlbumAty;
import com.nowy.baselib.utils.T;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQ_CODE_PHOTO = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_Tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumAty.startSingle(MainActivity.this,true,null,true,REQ_CODE_PHOTO);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_PHOTO:
                if (resultCode==Activity.RESULT_OK && data != null){
                    ArrayList<String> paths = data.getStringArrayListExtra(AlbumAty.BUNDLE_CHECKED_PATH);
                    if(paths != null && paths.size() > 0 ){
                        T.s(paths.get(0));

                        //提交后清除缓存目录下的图片
                        // DataCleanManager.cleanCustomCache(FileManager.getCacheImageCutPath());
                    }
                }
                break;
        }
    }
}
