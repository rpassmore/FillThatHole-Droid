/**
 * File: ImageCusorAdapter.java Project: FillThatHole
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

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Custom adapter to work around a bug in {@link SimpleCursorAdapter} 
 * where exceptions are thrown when setting a ImageView from a DB BLOB 
 * @author chuzzley
 *
 */
public class ImageCursorAdapter extends SimpleCursorAdapter {

  private Cursor c;
  private Context context;
  
  protected String[] from = null;
  protected int[] to = null;
  protected int layout = -1;

  public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
    super(context, layout, c, from, to);
    this.c = c;
    this.context = context;           
    this.to = to;
    this.from = from;
    this.layout = layout;
  }

  
  public View getView(int pos, View inView, ViewGroup parent) {
    View v = inView;
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = inflater.inflate(layout, null);
    }
    
    this.c.moveToPosition(pos);
    String address = this.c.getString(this.c.getColumnIndex(DBAdapter.KEY_ADDRESS));
    Date date = new Date( this.c.getLong(this.c.getColumnIndex(DBAdapter.KEY_CREATED_DATE)) );                 
    HazardType hazardType = new HazardType(context, this.c.getInt(this.c.getColumnIndex(DBAdapter.KEY_HAZARD_TYPE)) );
    String state = this.c.getString(this.c.getColumnIndex(DBAdapter.KEY_STATE));
    
    byte[] photo = this.c.getBlob(this.c.getColumnIndex(DBAdapter.KEY_PHOTO));
    if (photo != null) {
      ImageView iv = (ImageView) v.findViewById(R.id.photo);
      iv.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
    }
    
    TextView addressTextView = (TextView) v.findViewById(R.id.address);
    addressTextView.setText(address);
    
    TextView dateTextView = (TextView) v.findViewById(R.id.date);
    dateTextView.setText(date.toLocaleString());    
    
    TextView typeTextView = (TextView) v.findViewById(R.id.type);
    typeTextView.setText(hazardType.toString());
    
    TextView stateTextView = (TextView) v.findViewById(R.id.state);
    stateTextView.setText(state);
    return v;
  }

}
