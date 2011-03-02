/**
 * File: HazardType.java Project: FillThatHole
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

import android.content.Context;
import android.content.res.Resources;

public class HazardType {

  private int type = 0;
  private Context context = null;
  
  public HazardType(Context context, int type) {
    this.type = type;
    this.context = context;
  }
  
  @Override
  public String toString() {
    Resources res = context.getResources(); 
    String[] hazardTypes = res.getStringArray(R.array.hazardTypesArray);   
    
    if(hazardTypes.length > type) {
      return hazardTypes[type];
    }
    
    return null;
  }
}
