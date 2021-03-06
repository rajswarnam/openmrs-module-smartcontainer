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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.module.smartcontainer.RdfSource;
import org.openmrs.module.smartcontainer.smartData.Attribution;
import org.openmrs.module.smartcontainer.smartData.QuantitativeResult;
import org.openmrs.module.smartcontainer.smartData.SmartLabResult;
import org.openmrs.module.smartcontainer.smartData.ValueRange;
import org.openmrs.module.smartcontainer.util.RdfUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Render a RDF/XML for SMART LabResult
 */
public class LabResultRDFSource extends RdfSource {
    Log log = LogFactory.getLog(getClass());

    /**
     * Primary method to render rdf
     *
     * @param labs
     * @return
     * @throws IOException
     */
    public String getRDF(List<SmartLabResult> labs) throws IOException, RDFHandlerException {

        Writer sWriter = new StringWriter();
        RDFXMLWriter graph = new RDFXMLPrettyWriter(sWriter);
        addHeader(graph);

        graph.startRDF();
        for (SmartLabResult l : labs) {
            /*
                * Add parent node <sp:LabResult> ...child nodes </sp:LabResult>
                */
            BNode labResultNode = factory.createBNode();
            URI labResult = factory.createURI(sp, "LabResult");

            graph.handleStatement(factory.createStatement(labResultNode, type, labResult));
            /*
                * Add child node <sp:labName> ...coded value node </sp:labName>
                */
            URI labName = factory.createURI(sp, "labName");
            graph.handleStatement(factory.createStatement(labResultNode, labName,
                    RdfUtil.codedValue(factory, graph, l.getLabName())));
            /*
                * Add child node <sp:quantitativeResult> ...QuantitativeResult node
                * </sp:quantitativeResult>
                */
            URI quantitativeResults = factory.createURI(sp,
                    "quantitativeResult");
            graph.handleStatement(factory.createStatement(
                    labResultNode,
                    quantitativeResults,
                    addQuantityResultNode(factory, graph,
                            l.getQuantitativeResult())));
            /*
                * Add quantitativeResult node if the concept is coded value
                */

            if (l.getQuantitativeResult().getNormalRange() == null
                    ) {

                BNode quantitativeResultNode = factory.createBNode();
                URI quantitativeResult = factory.createURI(sp,
                        "QuantitativeResult");
                graph.handleStatement(factory.createStatement(quantitativeResultNode, type,
                        quantitativeResult));
                URI value = factory.createURI(sp, "value");
                Value valueVal = factory.createLiteral(l
                        .getQuantitativeResult().getValueAndUnit().getValue());
                graph.handleStatement(factory.createStatement(quantitativeResultNode, value, valueVal));
            }
            /*
                * Add child node <sp:specimenCollected> ..Attribution node
                * </sp:specimenCollected>
                */

            URI specimenCollected = factory.createURI(sp, "specimenCollected");
            graph.handleStatement(factory.createStatement(
                    labResultNode,
                    specimenCollected,
                    addattributionNode(factory, graph, l.getSpecimenCollected())));
            /*
                * Add child node <sp:externalID>AC09205823577</sp:externalID>
                */
            if (l.getExternalID() != null) {
                URI externalID = factory.createURI(sp, "externalID");
                Value externalIDVal = factory.createLiteral(l.getExternalID());
                graph.handleStatement(factory.createStatement(labResultNode, externalID, externalIDVal));

            }
            
            // TODO: What about "SmartLabResult.comments" ?
        }
        graph.endRDF();
        return sWriter.toString();
    }

    /**
     * Create following rdf: <sp:Attribution> ..child nodes </sp:Attribution>
     *
     * @param factory
     * @param graph
     * @param specimenCollected
     * @return
     * @throws IOException
     */
    private Value addattributionNode(ValueFactory factory, RDFXMLWriter graph,
                                     Attribution specimenCollected) throws IOException, RDFHandlerException {
        /*
           * Add parent node <sp:Attribution> ..child nodes </sp:Attribution>
           */
        BNode attributionNode = factory.createBNode();
        URI attribution = factory.createURI(sp, "Attribution");
        graph.handleStatement(factory.createStatement(attributionNode, type, attribution));
        /*
           * Add child node <sp:startTime>2010-12-26T16:33:01</sp:startTime>
           */
        URI startTime = factory.createURI(sp, "startTime");
        Value startDateVal = factory.createLiteral(specimenCollected
                .getStartTime());
        graph.handleStatement(factory.createStatement(attributionNode, startTime, startDateVal));
        return attributionNode;
    }

    /**
     * Create following rdf node
     * <p/>
     * <sp:QuantitativeResult> ...child nodes </sp:QuantitativeResult>
     *
     * @param factory
     * @param graph
     * @param q
     * @return
     * @throws IOException
     */
    private Value addQuantityResultNode(ValueFactory factory,
                                        RDFXMLWriter graph, QuantitativeResult q) throws IOException, RDFHandlerException {
        BNode quantitativeResultNode = null;

        if (q.getNormalRange() != null || q.getNonCriticalRange() != null) {
            /*
                * Add parent node<sp:QuantitativeResult> ...child nodes
                * </sp:QuantitativeResult>
                */
            quantitativeResultNode = factory.createBNode();
            URI quantitativeResult = factory
                    .createURI(sp, "QuantitativeResult");
            graph.handleStatement(factory.createStatement(quantitativeResultNode, type,
                    quantitativeResult));
            /*
                * Add child node <sp:valueAndUnit> ..Value and Unit node
                * </sp:valueAndUnit>
                */
            URI valueAndUnit = factory.createURI(sp, "valueAndUnit");
            graph.handleStatement(factory.createStatement(quantitativeResultNode, valueAndUnit,
                    RdfUtil.valueAndUnit(factory, graph, q.getValueAndUnit())));
            /*
                * Add child node <sp:normalRange> ..Value range node
                * </sp:normalRange>
                */
            URI normalRange = factory.createURI(sp, "normalRange");
            graph.handleStatement(factory.createStatement(quantitativeResultNode, normalRange,
                    addResultRangeNode(factory, graph, q.getNormalRange())));
        }
        return quantitativeResultNode;
    }

    /**
     * Create following rdf:
     * <p/>
     * <sp:ValueRange> ...child nodes </sp:ValueRange>
     *
     * @param factory
     * @param graph
     * @param normalRange
     * @return
     * @throws IOException
     */
    private Value addResultRangeNode(ValueFactory factory, RDFXMLWriter graph,
                                     ValueRange normalRange) throws IOException, RDFHandlerException {
        /*
           * Add parent node <sp:ValueRange> ...child nodes </sp:ValueRange>
           */
        BNode resultRangeNode = factory.createBNode();
        URI resultRange = factory.createURI(sp, "ResultRange");
        graph.handleStatement(factory.createStatement(resultRangeNode, type, resultRange));
        /*
           * Add child node <sp:minimum> ..Value and unit node </sp:minimum>
           */
        if (normalRange.getMinimum() != null) {
            URI minimum = factory.createURI(sp, "minimum");
            graph.handleStatement(factory.createStatement(
                    resultRangeNode,
                    minimum,
                    RdfUtil.valueAndUnit(factory, graph,
                            normalRange.getMinimum())));
        }
        /*
           * Add child node <sp:maximum> ..Value and unit node </sp:maximum>
           */
        if (normalRange.getMaximum() != null) {
            URI maximum = factory.createURI(sp, "maximum");
            graph.handleStatement(factory.createStatement(
                    resultRangeNode,
                    maximum,
                    RdfUtil.valueAndUnit(factory, graph,
                            normalRange.getMaximum())));
        }
        return resultRangeNode;
    }

    /**
     * @param patient
     * @return
     * @throws IOException
     */
    public String getRDF(Patient patient) throws IOException {

        return null;
    }

}
