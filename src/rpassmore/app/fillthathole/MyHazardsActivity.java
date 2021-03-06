/**
 * File: MyHazardsActivity.java Project: FillThatHole
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

import java.sql.SQLException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

public class MyHazardsActivity extends ListActivity {

  private static final String VIEW_SUBMITTED_HAZARD_WEB_ADR =  "http://www.fillthathole.org.uk/hazard/";
  private Cursor cursor = null;
  private DBAdapter dbAdapter = null;

  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);
    setContentView(R.layout.my_hazards_activity);
    registerForContextMenu(getListView());

    dbAdapter = new DBAdapter(this);  
    dbAdapter.open();
    cursor = dbAdapter.getAll();
    cursor.moveToFirst();
    startManagingCursor(cursor);
  

    // The columns to bind
    String[] columns = new String[] { DBAdapter.KEY_PHOTO, DBAdapter.KEY_ADDRESS, DBAdapter.KEY_CREATED_DATE,
        DBAdapter.KEY_HAZARD_TYPE, DBAdapter.KEY_STATE };

    // The XML defined views which the data will be bound to
    int[] to = new int[] { R.id.photo, R.id.address, R.id.date, R.id.type, R.id.state };

    // Create the adapter using the cursor pointing to the desired data as well
    // as the layout information
    ImageCursorAdapter mAdapter = new ImageCursorAdapter(this, R.layout.my_hazards_list_entry, cursor, columns, to);

    this.setListAdapter(mAdapter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    dbAdapter.close();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.my_hazards_option_menu, menu);
    return true;
  }

  @Override
  public void onListItemClick(ListView l, View view, int position, long id) {
    super.onListItemClick(l, view, position, id);
    //display the fillThatHole hazard web page at   
    //http://www.fillthathole.org.uk/hazard/<hazard_id>?key=<reporter_key>
    try {
      Hazard hazard = dbAdapter.load(id);          

      //hazard has been submitted so display on website
      if (hazard.getState() == Hazard.State.SUBMITTED) {
        String url = VIEW_SUBMITTED_HAZARD_WEB_ADR + hazard.getHazardId() + "?key=" + hazard.getreporterKey();  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        Uri u = Uri.parse(url);  
        intent.setData(u);  
        try {  
          startActivity(intent);  
        } catch (ActivityNotFoundException e) {          
          Log.e(getPackageName(), "Browser not found.", e);        
        }

      } else {
        //hazard not submitted so open for editing
        displayHazard(id);
      }
    } catch (SQLException e) {      
      Log.e(getPackageName(), "Error fetching hazard for viewing", e);
    }  
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.newHazard: {
        displayHazard(-1);
        break;
      }
      case R.id.quit: {
        finish();
        break;
      }   
      case R.id.settings:
        startActivity(new Intent(this, HazardViewPreferencesActivity.class));
        return true;
    }
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.my_hazards_context_menu, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    switch (item.getItemId()) {
      case R.id.edit: {
        displayHazard(info.id);
        return true;
      }
      case R.id.delete: {
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this hazard?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 @Override
                   public void onClick(DialogInterface dialog, int id) {                                              
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                 @Override
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        AlertDialog alert = builder.create();        
        */
        dbAdapter.delete(info.id);
        cursor.requery();
        
        return true;
      }
      case R.id.markFixed: {
        try {
          Hazard hazard = dbAdapter.load(info.id);
          
          try {
            WebSiteComms.updateState(hazard);
            hazard.setState(Hazard.State.FIXED);
            dbAdapter.save(hazard);
            cursor.requery();
            
            //inform user submission complete
            Toast toast = Toast.makeText(getApplicationContext(), "Hazard updated successfully", Toast.LENGTH_SHORT);
            toast.show();        
            
          } catch (WebSiteCommsException e) {
            Log.e(getPackageName(), "Error updating hazard", e);
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
          }                    
        } catch (SQLException e) {
          Log.e(getPackageName(), e.toString());
        }
        return true;
      }
      default: {
        return super.onContextItemSelected(item);
      }
    }
  }

  private void displayHazard(long id) {
    Intent myIntent = new Intent(this, ViewHazardActivity.class);
    Bundle bundle = new Bundle();
    bundle.putLong("hazardId", id);
    myIntent.putExtras(bundle);
    startActivity(myIntent);
  }
}
