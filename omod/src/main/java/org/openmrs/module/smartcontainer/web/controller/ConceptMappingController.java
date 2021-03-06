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
package org.openmrs.module.smartcontainer.web.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.smartcontainer.SmartDataService;
import org.openmrs.module.smartcontainer.TransientSmartConceptMap;
import org.openmrs.module.smartcontainer.smartData.SmartData;
import org.openmrs.module.smartcontainer.smartData.handler.SmartDataHandler;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for Manage Concept mapping page of the smartcontainer module User: aja Date: 8/20/11
 * Time: 7:11 AM To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "module/smartcontainer/conceptMapping.form")
public class ConceptMappingController {
	
	private Log log = LogFactory.getLog(ConceptMappingController.class);
	
	private String SUCCESS_VIEW = "module/smartcontainer/conceptMapping";
	
	@Autowired
	private SmartDataService smartService;
	
	@Autowired
	private ConceptService conceptService;
	
	/**
	 * Called when the request to page load come
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model, WebRequest request) {
		//TODO Support versions 1.9+, if we can allow creation of
		// reference terms from here before using them in mappings
		if (isValidOpenmrsVersion())
			model.put("canManageMappings", true);
		
		//Map to group  required mapping for each smart data handler
		Map<String, List<TransientSmartConceptMap>> handlerListOfMappingsMap = new HashMap<String, List<TransientSmartConceptMap>>();
		
		Map<String, SmartDataHandler<? extends SmartData>> handlersMap = smartService.getHandlers();
		for (Map.Entry<String, SmartDataHandler<? extends SmartData>> handlerEntry : handlersMap.entrySet()) {
			List<TransientSmartConceptMap> listOfMappings = handlerEntry.getValue().getRequiredConceptMappings();
			if (CollectionUtils.isNotEmpty(listOfMappings)) {
				//Add a spacing between the handler names for better display in the form
				handlerListOfMappingsMap.put(StringUtils.join(handlerEntry.getKey().split("(?=\\p{Lu})"), " "),
				    listOfMappings);
			}
		}
		
		model.put("handlerListOfMappingsMap", handlerListOfMappingsMap);
		
		return SUCCESS_VIEW;
	}
	
	/**
	 * Processes requests to add new concept mappings
	 * 
	 * @param sourceNames an array of concept source names to map to
	 * @param sourceCodes an array of source codes to use for the mappings
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String saveMappings(@RequestParam(value = "sourceNames", required = false) String[] sourceNames,
	                           @RequestParam(value = "sourceCodes", required = false) String[] sourceCodes,
	                           WebRequest webRequest) {
		try {
			//The concept field tag doesn't support multiple values for the same request 
			//parameter so we can't get them as an array using the @RequestParam annotation
			for (int i = 0; i < sourceCodes.length; i++) {
				String conceptId = webRequest.getParameter("conceptIds[" + i + "]");
				//if the user didn't specify the concept to map to skip the mapping
				if (StringUtils.isNotBlank(conceptId))
					saveMapping(conceptId, sourceNames[i], sourceCodes[i]);
				
			}
			
			webRequest.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
			    Context.getMessageSourceService().getMessage("smartcontainer.changes.saved"), WebRequest.SCOPE_SESSION);
		}
		catch (Exception e) {
			webRequest.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
			    Context.getMessageSourceService().getMessage("smartcontainer.changes.save.fail"), WebRequest.SCOPE_SESSION);
		}
		
		return "redirect:conceptMapping.form";
	}
	
	private void saveMapping(String conceptId, String conceptSourceName, String sourceCode) {
		Concept mappedConcept = conceptService.getConceptByMapping(sourceCode, conceptSourceName);
		//The form is strictly for mapping unmapped concepts
		if (mappedConcept != null)
			return;
		
		Concept concept = conceptService.getConcept(conceptId);
		if (concept != null) {
			ConceptMap map = new ConceptMap();
			map.setConcept(concept);
			
			ConceptSource source = conceptService.getConceptSourceByName(conceptSourceName);
			if (source == null) {
				source = new ConceptSource();
				source.setName(conceptSourceName);
				source.setDescription(conceptSourceName + " terms");
				//TODO prompt the user for the hl7 code from the form 
				conceptService.saveConceptSource(source);
			}
			
			map.setSource(source);
			map.setSourceCode(sourceCode);
			concept.addConceptMapping(map);
			conceptService.saveConcept(concept);
		}
	}
	
	/**
	 * Hack to check if the user is running version 1.8 or earlier to be able to manage concept
	 * mappings through the smart container
	 */
	private boolean isValidOpenmrsVersion() {
		Field referenceTermField = null;
		try {
			referenceTermField = ConceptMap.class.getDeclaredField("conceptReferenceTerm");
		}
		catch (SecurityException e) {}
		catch (NoSuchFieldException e) {}
		return referenceTermField == null;
	}
}
