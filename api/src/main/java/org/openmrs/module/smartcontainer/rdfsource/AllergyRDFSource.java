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
package org.openmrs.module.smartcontainer.rdfsource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.smartcontainer.RdfSource;
import org.openmrs.module.smartcontainer.smartData.CodedValue;
import org.openmrs.module.smartcontainer.smartData.SmartAllergy;
import org.openmrs.module.smartcontainer.util.RdfUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.springframework.util.StringUtils;

/**
 * Render a RDF/XML for @link {@link SmartAllergy}
 */
public class AllergyRDFSource extends RdfSource {

    /**
     * Primary method to render rdf
     *
     * @param allergies
     * @return
     * @throws IOException
     */
    public String getRDF(List<SmartAllergy> allergies) throws IOException, RDFHandlerException {

        Writer sWriter = new StringWriter();
        RDFXMLWriter graph = new RDFXMLPrettyWriter(sWriter);
        addHeader(graph);
        graph.startRDF();
        for (SmartAllergy smartallergy : allergies) {
            /*
             * Add parent node <sp:Allergy> ..child nodes </sp:Allergy>
             */
            BNode allergyNode = factory.createBNode();
            URI allergy = factory.createURI(sp, "Allergy");
            graph.handleStatement(factory.createStatement(allergyNode, type, allergy));
            
            
            // should be only one of "class" OR "substance"
            if (smartallergy.getClassOfAllergen() != null)
            	addChildNode("class", allergyNode, graph, smartallergy.getClassOfAllergen());
            if (smartallergy.getSubstance() != null)
            	addChildNode("substance", allergyNode, graph, smartallergy.getSubstance());
            
            // these are all required
            addChildNode("severity", allergyNode, graph, smartallergy.getSeverity());
            addChildNode("reaction", allergyNode, graph, smartallergy.getReaction());
            addChildNode("category", allergyNode, graph, smartallergy.getCategory());
            
            // notes is optional
            if (StringUtils.hasLength(smartallergy.getNotes()))
            	addChildNode("notes", allergyNode, graph, smartallergy.getNotes());
        }

        graph.endRDF();

        return sWriter.toString();
    }

    private URI addChildNode(String nodename, BNode parentNode,
			RDFXMLWriter graph, CodedValue codedValue) throws RDFHandlerException, IOException {
    	URI childnode = factory.createURI(sp, nodename);
        Value value = RdfUtil.codedValue(factory, graph, codedValue);
		graph.handleStatement(factory.createStatement(parentNode, childnode, value));
        return childnode;
	}
    
    private URI addChildNode(String nodename, BNode parentNode,
			RDFXMLWriter graph, String textContent) throws RDFHandlerException, IOException {
    	URI childnode = factory.createURI(sp, nodename);
    	Value value = factory.createLiteral(textContent);
		graph.handleStatement(factory.createStatement(parentNode, childnode, value));
        return childnode;
	}

	public String getRDF(Patient patient) throws IOException {
        return null;
    }

}
