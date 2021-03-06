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
package org.openmrs.module.smartcontainer.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the
 */
public class AdminList extends AdministrationSectionExt {

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
     */
    public String getTitle() {
        return "smartcontainer.title";
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
     */
    public Map<String, String> getLinks() {

        Map<String, String> map = new LinkedHashMap<String, String>();

        map.put("module/smartcontainer/manageSmartApps.form", "smartcontainer.admin.manage");
        map.put("module/smartcontainer/users.list", "smartcontainer.admin.manage.user");
        map.put("module/smartcontainer/problemsetup.form", "smartcontainer.admin.problemsetup");
        map.put("module/smartcontainer/conceptMapping.form", "smartcontainer.admin.conceptMapping");
        return map;
    }

}
