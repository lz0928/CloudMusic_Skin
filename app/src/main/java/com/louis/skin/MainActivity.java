package com.louis.skin;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.louis.cloudmusic.skin.library.base.SkinActivity;

import java.io.File;

public class MainActivity extends SkinActivity {

    private String skinPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skinPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "net163.skin";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms,200);
            }
        }
    }

    @Override
    protected boolean openChangeSkin() {
        return true;
    }

    public void skinDynamic(View view) {
        skinDynamic(skinPath, R.color.skin_item_color);
    }

    public void skinDefault(View view) {
        defaultSkin(R.color.colorPrimary);
    }
}
