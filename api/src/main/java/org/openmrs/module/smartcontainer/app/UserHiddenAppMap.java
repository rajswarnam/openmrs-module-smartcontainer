/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.smartcontainer.app;

import org.openmrs.User;

/**
 * Represents a mapping between a user and an App that is hidden
 */
public class UserHiddenAppMap {
	
	private Integer userHiddenAppMapId;
	
	//the user hiding the app
	private User user;
	
	//the hidden app
	private App app;
	
	/**
	 * Default constraint
	 */
	public UserHiddenAppMap() {
	}
	
	/**
	 * Convenience constructor that takes in the user and the app to hide
	 */
	public UserHiddenAppMap(User user, App app) {
		this.user = user;
		this.app = app;
	}
	
	/**
	 * @return the userHiddenAppMapId
	 */
	public Integer getUserHiddenAppMapId() {
		return userHiddenAppMapId;
	}
	
	/**
	 * @param userHiddenAppMapId the userHiddenAppMapId to set
	 */
	public void setUserHiddenAppMapId(Integer userHiddenAppMapId) {
		this.userHiddenAppMapId = userHiddenAppMapId;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return the app
	 */
	public App getApp() {
		return app;
	}
	
	/**
	 * @param app the app to set
	 */
	public void setApp(App app) {
		this.app = app;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserHiddenAppMap)) {
			return false;
		}
		UserHiddenAppMap other = (UserHiddenAppMap) obj;
		if (getUserHiddenAppMapId() != null && other.getUserHiddenAppMapId() != null)
			return (getUserHiddenAppMapId().equals(other.getUserHiddenAppMapId()));
		
		return this == obj;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getUserHiddenAppMapId() == null)
			return super.hashCode();
		return getUserHiddenAppMapId().hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (getUserHiddenAppMapId() == null)
			return "";
		return "#" + getUserHiddenAppMapId();
	}
}