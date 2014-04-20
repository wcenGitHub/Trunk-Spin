package com.xiaomin.spin.views.module.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class CameraActivity extends Activity
{
    private Preview mPreview;
    private Camera mCamera;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = new FrameLayout(this);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        Resources r = this.getResources();
        int rightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,
                r.getDisplayMetrics());
        btnLayoutParams.setMargins(0,0,rightPx,0);

        startButton = new Button(this);
        startButton.setText("Start");
        startButton.setId(1);
        startButton.setLayoutParams(btnLayoutParams);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.addView(startButton);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreview = new Preview(this);

        frameLayout.addView(mPreview);
        frameLayout.addView(relativeLayout);
        setContentView(frameLayout);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        openCamera();
    }

    private boolean openCamera()
    {
        try
        {
            releaseCameraAndPreview();
            mCamera = Camera.open();
            mPreview.setCamera(mCamera);
            return true;
        } catch (Exception e)
        {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
            return false;
        }
    }

    private void releaseCameraAndPreview()
    {
        mPreview.setCamera(null);
        if (mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mPreview.recordEnd();
        if (mCamera != null)
        {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, 0, "开始");
        menu.add(Menu.NONE, 1, 1, "结束");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId())// 寰楀埌琚偣鍑荤殑item鐨刬temId
        {
            case 0: //开始录像

                if (mCamera != null)
                {
                    mPreview.setCamera(null);
                    mCamera.release();
                    mCamera = null;
                }

                String filePath = Environment.getExternalStorageDirectory()
                        + "/ABCD/";
                String fileName = System.currentTimeMillis() + ".3gp";
                mPreview.recordBegin(filePath, fileName);
                break;
            case 1:// 停止录像
                mPreview.recordEnd();
                openCamera();
                mPreview.restartGame();
                break;
        }
        return true;
    }
}