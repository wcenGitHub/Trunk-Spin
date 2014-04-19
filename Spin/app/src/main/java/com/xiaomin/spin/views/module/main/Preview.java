package com.xiaomin.spin.views.module.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.hardware.Camera.Size;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressLint("InlinedApi")
public class Preview extends ViewGroup implements SurfaceHolder.Callback
{
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    // 褰曞儚
    MediaRecorder mRecord;

    private String file_path;
    private String file_name;

    @SuppressWarnings("deprecation")
    Preview(Context context)
    {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera)
    {
        mCamera = camera;
        if (mCamera != null)
        {
            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void recordBegin(String file_path, String file_name)
    {
        if (mRecord == null)
        {
            this.file_path = file_path;
            this.file_name = file_name;
            boolean result = startRecord();
            if (!result)
            {
                stopRecord();
            }
        }
    }

    public void recordEnd()
    {
        stopRecord();
    }

    // 鍋滄褰曞儚
    private void stopRecord()
    {
        if (mRecord != null)
        {
            mRecord.stop();
            mRecord.release();
            mRecord = null;
        }
    }

    // 鍚姩褰曞儚
    private boolean startRecord()
    {
        mRecord = new MediaRecorder();
        mRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecord.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecord.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mRecord.setVideoSize(mPreviewSize.width, mPreviewSize.height);

        File file = new File(file_path);
        if (!file.exists())
            file.mkdirs();
        file = new File(file_path + file_name);
        if (file.exists())
            file.delete();

        mRecord.setOutputFile(file.getAbsolutePath());
        mRecord.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        try
        {
            mRecord.prepare();
        } catch (IllegalStateException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        mRecord.start();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (changed && getChildCount() > 0)
        {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null)
            {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            if (width * previewHeight > height * previewWidth)
            {
                final int scaledChildWidth = previewWidth * height
                        / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else
            {
                final int scaledChildHeight = previewHeight * width
                        / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width,
                        (height + scaledChildHeight) / 2);
            }
        }
    }

    public void restartGame()
    {
        try
        {
            if (mCamera != null)
            {
                mCamera.setPreviewDisplay(mHolder);
            }
        } catch (IOException exception)
        {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            if (mCamera != null)
            {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception)
        {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h)
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}