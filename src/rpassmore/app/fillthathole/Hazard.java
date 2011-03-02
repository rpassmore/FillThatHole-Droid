/**
 * File: Hazard.java Project: FillThatHole
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

public class Hazard {

	enum State {
		UNSUBMITTED,
		SUMITTED,
		FIXED,
		ERROR
	}
	
	private long id = -1;
	private long createdDate = new Date().getTime();
	private long updatedDate = createdDate; 
	private Double lattitude = null;
	private Double longitude = null;
	private String locationDesc = null;
	private String address = null;
	
	private int hazardType = -1;
	private String hazardDesc = null;
	
	private boolean hasPhoto = false;
	private byte[] photo = null;
	private String photoUrl = null;
	
	private State state = State.UNSUBMITTED;
	
	private boolean hasAdditionalInfo = false;
	private double depth = 0.0;
	private double size = 0.0;
	private double distFromKerb = 0.0;
	private boolean onRedRoute = false;
	private boolean onLevelCrossing = false;
	private boolean onTowPath = false;
	
	//holds the hazard id returned from the web site
	private String hazardId = null;
	
	public long getId() {
		return id;
	}		

	public void setId(long id) {
		this.id = id;
	}


	public Double getLattitude() {
		return lattitude;
	}


	public void setLattitude(Double lattitude) {
		this.lattitude = lattitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public String getLocationDesc() {
		return locationDesc;
	}


	public void setLocationDesc(String locationDesc) {
		this.locationDesc = locationDesc;
	}

	public String getAddress() {
    return address;	  
	}

	public void setAddress(String address) {
	  this.address = address;
	}
	
	public int getHazardType() {
		return hazardType;
	}


	public void setHazardType(int hazardType) {
		this.hazardType = hazardType;
	}


	public String getHazardDesc() {
		return hazardDesc;
	}


	public void setHazardDesc(String hazardDesc) {
		this.hazardDesc = hazardDesc;
	}


	public boolean isHasPhoto() {
		return hasPhoto;
	}


	public void setHasPhoto(boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}


	public byte[] getPhoto() {
		return photo;
	}


	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}


	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	

	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}


	public boolean isHasAdditionalInfo() {
		return hasAdditionalInfo;
	}


	public void setHasAdditionalInfo(boolean hasAdditionalInfo) {
		this.hasAdditionalInfo = hasAdditionalInfo;
	}


	public double getDepth() {
		return depth;
	}


	public void setDepth(double d) {
		this.depth = d;
	}


	public double getSize() {
		return size;
	}


	public void setSize(double size) {
		this.size = size;
	}


	public double getDistFromKerb() {
		return distFromKerb;
	}


	public void setDistFromKerb(double distFromKerb) {
		this.distFromKerb = distFromKerb;
	}


	public boolean isOnRedRoute() {
		return onRedRoute;
	}


	public void setOnRedRoute(boolean onRedRoute) {
		this.onRedRoute = onRedRoute;
	}


	public boolean isOnLevelCrossing() {
		return onLevelCrossing;
	}


	public void setOnLevelCrossing(boolean onLevelCrossing) {
		this.onLevelCrossing = onLevelCrossing;
	}


	public boolean isOnTowPath() {
		return onTowPath;
	}


	public void setOnTowPath(boolean onSomethingelse) {
		this.onTowPath = onSomethingelse;
	}


	public long getUpdatedDate() {
		return updatedDate;
	}


	public void setUpdatedDate(long updatedDate) {
		this.updatedDate = updatedDate;
	}


	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long date) {
		this.createdDate = date;
	}
	
	/**
   * @param hazardId the hazardId to set
   */
  public void setHazardId(String hazardId) {
    this.hazardId = hazardId;
  }

  /**
   * @return the hazardId
   */
  public String getHazardId() {
    return hazardId;
  }

  public String createSubmitStr() {
		String str = "{ + " + locationDesc + "}";
		
		return str;
	}
	
	public String createAddImageStr() {
		return "";
	}
}
