package rpassmore.app.fillthathole;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * File: TakePicActivity.java	Project: FillThatHole-Android
 *
 * Created: 1 Mar 2011
 *
 * Copyright (C) 2011 Robert Passmore
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
public class CameraActivity {
  
}
/*
public class CameraActivity extends Activity implements SurfaceHolder.Callback,
    Camera.AutoFocusCallback, Camera.PictureCallback
{
  Camera mCamera;
  boolean mPreviewRunning = false;
  ImageButton takePictureButton;
  View waitView;

  public void onCreate(Bundle icicle)
  {
    super.onCreate(icicle);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    getWindow().setFormat(PixelFormat.TRANSLUCENT);

    setContentView(R.layout.camera);

    mSurfaceView = (SurfaceView) findViewById(R.id.surface);

    waitView = findViewById(R.id.wait_icon);

    mSurfaceHolder = mSurfaceView.getHolder();
    mSurfaceHolder.addCallback(this);
    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void surfaceCreated(SurfaceHolder holder)
  {
    mCamera = Camera.open();
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
  {
    if (mPreviewRunning)
    {
      mCamera.stopPreview();
    }

    Camera.Parameters p = mCamera.getParameters();
    p.setPreviewSize(w, h);
    p.setPictureFormat(PixelFormat.JPEG);

    mCamera.setParameters(p);
    try
    {
      mCamera.setPreviewDisplay(holder);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    mCamera.startPreview();
    mPreviewRunning = true;
  }

  public void surfaceDestroyed(SurfaceHolder holder)
  {
    mCamera.stopPreview();
    mPreviewRunning = false;
    mCamera.release();
  }

  private SurfaceView mSurfaceView;
  private SurfaceHolder mSurfaceHolder;

  public void onAutoFocus(boolean success, Camera camera)
  {
    camera.takePicture(null, null, this);
  }

  private static String file_name = "temp_img";

  public void onPictureTaken(byte[] data, Camera camera)
  {

    // Matrix m = new Matrix();
    // m.postRotate(90);
    // Bitmap nb = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
    // m, true);

    try
    {
      FileOutputStream fos = openFileOutput(file_name, MODE_PRIVATE);
      fos.write(data);
      fos.close();
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    }

   // Intent i = new Intent(this, PhotoEditActivity.class);
   // i.putExtra("data", file_name);
   // this.finish();
   // startActivity(i);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_CAMERA)
    {
      mCamera.autoFocus(this);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

}*/