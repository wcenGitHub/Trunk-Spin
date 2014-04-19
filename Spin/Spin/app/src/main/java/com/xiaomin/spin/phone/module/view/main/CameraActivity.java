package com.xiaomin.spin.phone.module.view.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class CameraActivity extends Activity
{
    private Preview mPreview;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreview = new Preview(this);
        setContentView(mPreview);
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
        }
        catch (Exception e)
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