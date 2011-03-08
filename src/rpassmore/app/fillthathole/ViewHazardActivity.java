/**
 * File: FillThatHole.java Project: FillThatHole
 *
 * Created: 20 Feb 2011
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
package rpassmore.app.fillthathole;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;

public class ViewHazardActivity extends Activity {

  private static final int PICK_PHOTO_ACTIVITY = 1;
  private static final int PICTURE_ACTIVITY = 10;
  private static final int LOCATION_MAP_ACTIVITY = 20;

  private static final int ADD_PHOTO_DIALOG_ID = 100;
  private static final String SAVED_HAZARD_ID = "hazardId";

  private DBAdapter dbAdapter;

  private Button locateOnMap;
  private EditText locationDesc;
  private ImageView image;
  private Button addImage;
  private Spinner hazardType;
  private EditText hazardDesc;
  private CheckBox additionalInfo;
  private EditText hazardDepth;
  private EditText hazardSize;
  private EditText hazardDistFromKerb;
  private CheckBox onRedRoute;
  private CheckBox onLevelCrossing;
  private CheckBox onTowPath;
  private Button submitBtn;
  private RelativeLayout additionalInfoLayout;
  private TextView depthUnits;
  private TextView sizeUnits;
  private TextView distUnits;

  private Hazard hazard;
  private String measurementUnits;
  private Uri capturedImageURI;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_hazard_activity);

    locateOnMap = (Button) findViewById(R.id.locateOnMap);
    locationDesc = (EditText) findViewById(R.id.locationDesc);
    image = (ImageView) findViewById(R.id.image);
    addImage = (Button) findViewById(R.id.addImage);
    hazardType = (Spinner) findViewById(R.id.hazardType);
    hazardDesc = (EditText) findViewById(R.id.hazardDesc);
    additionalInfo = (CheckBox) findViewById(R.id.additionalInfo);
    hazardDepth = (EditText) findViewById(R.id.hazardDepth);
    hazardSize = (EditText) findViewById(R.id.hazardSize);
    hazardDistFromKerb = (EditText) findViewById(R.id.hazardDistFromKerb);
    onRedRoute = (CheckBox) findViewById(R.id.onRedRoute);
    onLevelCrossing = (CheckBox) findViewById(R.id.onLevelCrossing);
    onTowPath = (CheckBox) findViewById(R.id.onTowPath);
    submitBtn = (Button) findViewById(R.id.submit);
    additionalInfoLayout = (RelativeLayout) findViewById(R.id.additionalInfoLayout);
    depthUnits = (TextView) findViewById(R.id.hazardDepthUnits);
    sizeUnits = (TextView) findViewById(R.id.hazardSizeUnits);
    distUnits = (TextView) findViewById(R.id.hazardDistKerbUnits);

    // read in preferences for measurement units
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    measurementUnits = prefs.getString(getResources().getString(R.string.measurementUnits), "cm");
    depthUnits.setText(measurementUnits);
    sizeUnits.setText(measurementUnits);
    distUnits.setText(measurementUnits);

    dbAdapter = new DBAdapter(this.getApplicationContext());

    //try and load a hazard if onCreate called from resuming an activity
    if(!createFromBundle(savedInstanceState)) {
      //try and load a hazard if onCreate called from startActivity()
      Bundle b = this.getIntent().getExtras();    
      if(!createFromBundle(b)) {
        //no hazard to load so create one
        hazard = new Hazard();
      }        
    }
  }    
  
  
  /**
   * loads the hazard and populates the ui from the id supplied in the bundle
   * @param b
   * @return bool - true if a hazard was loaded
   */
  private boolean createFromBundle(Bundle b) {
    if (b != null) {
      long id = b.getLong(SAVED_HAZARD_ID);
      if (id == -1) {
        hazard = new Hazard();
      } else {
        try {
          dbAdapter.open();
          hazard = dbAdapter.load(id);
          populateFromHazard();
          makeActivityReadOnly();
        } catch (SQLException e) {
          Log.e(getPackageName(), e.toString());
        } finally {
          dbAdapter.close();
        }
      }
      return true;
    } 
    return false;
  }

  @Override
  protected void onSaveInstanceState (Bundle outState) {
    outState.putLong(SAVED_HAZARD_ID, hazard.getId());
  }

  @Override
  public void onPause() {
    super.onPause();
    persistHazard();
  }

  /**
   * 
   */
  private void makeActivityReadOnly() {   
    if (hazard.getState() != Hazard.State.UNSUBMITTED) {
      locationDesc.setEnabled(false);
      hazardType.setEnabled(false);
      hazardDesc.setEnabled(false);
      submitBtn.setEnabled(false);
      if (hazard.isHasAdditionalInfo()) {
        additionalInfo.setEnabled(false);
        hazardDepth.setEnabled(false);
        hazardSize.setEnabled(false);
        hazardDistFromKerb.setEnabled(false);
        onRedRoute.setEnabled(false);
        onLevelCrossing.setEnabled(false);
        onTowPath.setEnabled(false);
        additionalInfoLayout.setVisibility(View.VISIBLE);
      }
    }
    if (hazard.getState() == Hazard.State.FIXED || hazard.isHasPhoto()) {
      image.setEnabled(false);
      addImage.setEnabled(false);
    }
  }

  private void populateFromHazard() {
    locationDesc.setText(hazard.getLocationDesc());
    // Photo set from photo handlers
    hazardType.setSelection(hazard.getHazardType());
    locationDesc.setText(hazard.getHazardDesc());
    // display the thumbnail
    if(hazard.getPhoto() != null) {
      image.setImageBitmap(BitmapFactory.decodeByteArray(hazard.getPhoto(), 0, hazard.getPhoto().length));
    }

    additionalInfo.setChecked(hazard.isHasAdditionalInfo());
    
    hazardDepth.setText(Double.toString(hazard.getDepth()));
    hazardSize.setText(Double.toString(hazard.getSize()));
    hazardDistFromKerb.setText(Double.toString(hazard.getDistFromKerb()));
    onRedRoute.setChecked(hazard.isOnRedRoute());
    onLevelCrossing.setChecked(hazard.isOnLevelCrossing());
    onTowPath.setChecked(hazard.isOnTowPath());    
  }

  public void submitClickHandler(View view) {
    switch (view.getId()) {
      case R.id.submit:
        persistHazard();

        // TODO
        // if the hazard has been submitted successfully set the state to
        // submitted
        // if photo has been submitted successfully set hasPhoto true
       
        
        
        
        try {                            
          HttpClient client = new DefaultHttpClient();  
          String postURL = "http://www.fillthathole.org.uk/services/submit_hazard";
          HttpPost post = new HttpPost(postURL); 
         //hopefully dont need to set user agent
          //post.setHeader("User-Agent", "Fill%20That%20Hole/1.11 CFNetwork/485.12.7 Darwin/10.4.0");      
          ByteArrayEntity ent = new ByteArrayEntity(hazard.createSubmitStr().getBytes("UTF8"));          
          ent.setContentType("application/x-www-form-urlencoded");       
          post.setEntity(ent);          
          HttpResponse responsePOST = client.execute(post);  
          HttpEntity resEntity = responsePOST.getEntity();  
          if (resEntity != null) {    
            String s = EntityUtils.toString(resEntity);
            Log.i("RESPONSE", EntityUtils.toString(resEntity));
          }
        } catch (Exception e) {
          e.printStackTrace();
          Log.e(getPackageName(), "Error submitting hazard", e);
        }            

        hazard.setState(Hazard.State.SUMITTED);
        dbAdapter.open();
        dbAdapter.save(hazard);
        dbAdapter.close();
        break;
    }
    finish();
  }

  /**
   * 
   */
  private void persistHazard() {
    // populate the hazard and persist it
    hazard.setLocationDesc(locationDesc.getText().toString());
    // Photo set from photo handlers
    hazard.setHazardType(hazardType.getSelectedItemPosition());
    hazard.setHazardDesc(hazardDesc.getText().toString());
    hazard.setHasAdditionalInfo(additionalInfo.isChecked());

    if (hazardDepth.getText() != null && !hazardDepth.getText().toString().equals("")) {
      hazard.setDepth(Double.parseDouble(hazardDepth.getText().toString()));
    }
    if (hazardSize.getText() != null && !hazardSize.getText().toString().equals("")) {
      hazard.setSize(Double.parseDouble(hazardSize.getText().toString()));
    }
    if (hazardDistFromKerb.getText() != null && !hazardDistFromKerb.getText().toString().equals("")) {
      hazard.setDistFromKerb(Double.parseDouble(hazardDistFromKerb.getText().toString()));
    }
    hazard.setOnRedRoute(onRedRoute.isChecked());
    hazard.setOnLevelCrossing(onLevelCrossing.isChecked());
    hazard.setOnTowPath(onTowPath.isChecked());
    
    dbAdapter.open();
    long id = dbAdapter.save(hazard);
    hazard.setId(id); 
    dbAdapter.close();
  }

  public void additionalInfoClickHandler(View view) {
    if (additionalInfo.isChecked()) {
      additionalInfoLayout.setVisibility(View.VISIBLE);
    } else {
      additionalInfoLayout.setVisibility(View.GONE);
    }
  }

  public void addPhotoClickHandler(View view) {
    // take photo/select pic dialog
    showDialog(ADD_PHOTO_DIALOG_ID);
  }

  private void storeNewPhoto(Bitmap bitmap, String photoUrl) {
    // shrink bitmap for thumb nail image
    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

    // save scaled image to jpg byte array
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
    // store thumb nail from photo
    hazard.setPhoto(out.toByteArray());
    hazard.setPhotoUrl(photoUrl);
    try {
      out.close();
    } catch (IOException e) {
      Log.e(getPackageName(), "Error scaling and storing thumbnail", e);
    }

    // display the thumbnail
    image.setImageBitmap(scaledBitmap); 

    dbAdapter.open();
    long id = dbAdapter.save(hazard);
    hazard.setId(id);  
    dbAdapter.close();    
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case PICK_PHOTO_ACTIVITY: {
        if (resultCode == RESULT_OK) {
          Bitmap bitmap;
          try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
            storeNewPhoto(bitmap, data.getData().toString());
          } catch (FileNotFoundException ex) {
            Log.e(getPackageName(), "Error loading image file", ex);
          }
        }
        break;
      }
      case PICTURE_ACTIVITY: {
        if (resultCode == RESULT_OK) {
                              
          String[] projection = {MediaStore.Images.Media.DATA}; 
          Cursor cursor = managedQuery(capturedImageURI, projection, null, null, null); 
          int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
          cursor.moveToFirst(); 
          String capturedImageFilePath = cursor.getString(column_index_data);
                   
          Bitmap bitmap = BitmapFactory.decodeFile(capturedImageFilePath);    
          
          storeNewPhoto(bitmap, capturedImageFilePath);        
        }
        break;
      }
      case LOCATION_MAP_ACTIVITY: {
        if (resultCode == RESULT_OK) {
          Bundle extras = data.getExtras();
          if (extras != null) {
            hazard.setLattitude(extras.getLong(LocationActivity.LOCATION_LAT) / 1.0E6);
            hazard.setLongitude(extras.getLong(LocationActivity.LOCATION_LONG) / 1.0E6);
            if (extras.getString("Address") != null) {
              hazard.setAddress(extras.getString(LocationActivity.LOCATION_ADDRESS));
            }
          }
        }
        break;
      }
    }
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case ADD_PHOTO_DIALOG_ID:
        // Create our AlertDialog
        Builder builder = new AlertDialog.Builder(this);
        builder
            .setMessage(
                "You can attach a photo of the hazard to your report. Would you like to take a new photo or use an existing one?")
            .setCancelable(true).setPositiveButton("New photo", new DialogInterface.OnClickListener() {             
              @Override
              public void onClick(DialogInterface dialog, int which) {              
                String filename = System.currentTimeMillis() + ".jpg";                
                File imageDirectory = new File(Environment.getExternalStorageDirectory() + "/FillThatHole", filename);                           
                ContentValues values = new ContentValues();                 
                values.put(Images.Media.MIME_TYPE, "image/jpeg");
                values.put(Media.DESCRIPTION, "FillThatHole Image");
                values.put(Media.DATA, imageDirectory.getPath() );
                values.put(MediaStore.Images.Media.TITLE, filename);  
                
                // start the camera and take a new photo
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                                                                             
                capturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);               
                startActivityForResult(cameraIntent, PICTURE_ACTIVITY);
              }
            }).setNegativeButton("Existing photo", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // select photo from gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_PHOTO_ACTIVITY);
              }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;

    }
    return super.onCreateDialog(id);
  }

  public void locateClickHandler(View view) {
    // display location map activity
    Intent myIntent = new Intent(view.getContext(), LocationActivity.class);

    Bundle bundle = new Bundle();
    if (hazard.getState() != Hazard.State.UNSUBMITTED) {
      bundle.putBoolean(LocationActivity.LOCATION_CAN_DRAG, false);
    } else {
      bundle.putBoolean(LocationActivity.LOCATION_CAN_DRAG, true);
    }

    if (hazard.getLattitude() != null && hazard.getLongitude() != null) {
      bundle.putLong(LocationActivity.LOCATION_LAT, (long) (hazard.getLattitude() * 1E6));
      bundle.putLong(LocationActivity.LOCATION_LONG, (long) (hazard.getLongitude() * 1E6));
      bundle.putBoolean(LocationActivity.LOCATION_CAN_CREATE, false);
    } else {
      bundle.putBoolean(LocationActivity.LOCATION_CAN_CREATE, true);
    }

    myIntent.putExtras(bundle);
    startActivityForResult(myIntent, LOCATION_MAP_ACTIVITY);
  }
}