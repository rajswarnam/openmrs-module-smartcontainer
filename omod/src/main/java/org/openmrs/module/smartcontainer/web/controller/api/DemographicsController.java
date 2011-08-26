package org.openmrs.module.smartcontainer.web.controller.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.smartcontainer.SmartDataService;
import org.openmrs.module.smartcontainer.rdfsource.DemographicsRDFSource;
import org.openmrs.module.smartcontainer.smartData.SmartDemographics;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@Controller
@RequestMapping(value = "/smartcontainer/api/")
public class DemographicsController {

    Log log = LogFactory.getLog(getClass());
    private DemographicsRDFSource resource;


    public DemographicsRDFSource getResource() {
        return resource;
    }

    public void setResource(DemographicsRDFSource resource) {
        this.resource = resource;
    }

    @RequestMapping(method = RequestMethod.GET, value = "records/{pid}/demographics")
    public ModelAndView handle(@PathVariable("pid") Patient patient,
                               HttpServletResponse resp) {
        log.info("In the Demographics Controller");
        resp.setContentType("text/xml"); // should use a constant
        Writer writer;

        try {
            writer = resp.getWriter();
            SmartDemographics d = Context.getService(SmartDataService.class)
                    .getForPatient(patient, SmartDemographics.class);
            writer.write(resource.getRDF(d));

            writer.close();
        } catch (IOException e) {
            log.error("Unable to write out demographics for pid: " + patient.getId(), e);
        } catch (RDFHandlerException e) {
            log.error("Unable to write out demographics for pid: " + patient.getId(), e);
        }

        return null; // indicates this controller did all necessary processing

    }
}