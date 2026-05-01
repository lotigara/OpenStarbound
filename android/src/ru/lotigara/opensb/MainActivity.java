package ru.lotigara.opensb;

import android.content.ContextWrapper;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDL;

public class MainActivity extends SDLActivity {

    String BUILTIN_ASSET = "opensb.pak";

    @Override
    protected String[] getArguments() {
        String[] arguments = {"-bootconfig", getFilesDir().getAbsolutePath() + "/sbinit.config"};
        return arguments;
    }

    @Override
    protected String[] getLibraries() {
        return new String[] {
            "main"
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    private void setFullScreen() {
        try {
            Window window = getWindow();
            WindowInsetsController windowInsetsController = window.getInsetsController();

	    window.setDecorFitsSystemWindows(false);
            windowInsetsController.setSystemBarsBehavior(
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        } catch (Throwable e) {
            Log.w("starbound", "Setting immersive fullscreen mode failed");
	    e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Copy opensb.pak from APK
        File assetFile = new File(getFilesDir().getAbsolutePath() + "/assets/opensb_bundled.pak");
        try {
            long assetModifiedTime = 0;
            if (assetFile.exists())
                assetModifiedTime = Files.getLastModifiedTime(assetFile.toPath()).toMillis();
            else
                assetFile.getParentFile().mkdirs();
            long appInstallTime = getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime;
            if (assetModifiedTime < appInstallTime) {
                InputStream in = getAssets().open(BUILTIN_ASSET);
                FileOutputStream out = new FileOutputStream(assetFile, false);
                byte[] buffer = new byte[64 * 1024];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            }
        } catch (Throwable e) {
            Log.w("starbound", "Built-in asset copy failed");
	    e.printStackTrace();
        }

        setFullScreen();

        super.onCreate(savedInstanceState);
    }
}
