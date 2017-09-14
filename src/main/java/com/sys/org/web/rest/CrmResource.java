package com.sys.org.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sys.org.domain.*;
import com.sys.org.service.CrmService;
import com.sys.org.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/crm")
public class CrmResource {

    private final Logger log = LoggerFactory.getLogger(CrmResource.class);

    @Autowired
    private CrmService crmService;

    @PostMapping("/registration-information")
    @Timed
    public ResponseEntity<RegistrationInformation> createRegistrationInformation(@RequestBody RegistrationInformation registrationInformation) throws URISyntaxException {
        log.debug("REST request to save RegistrationInformation : {}", registrationInformation);
        if (registrationInformation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("registrationInformation", "idexists", "A new registrationInformation cannot already have an ID")).body(null);
        }
        RegistrationInformation result = crmService.createRegInfo(registrationInformation);
        return ResponseEntity.created(new URI("/api/registration-information/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("registrationInformation", result.getId().toString()))
            .body(result);
    }

    @GetMapping("/center-locations")
    @Timed
    public List<CenterLocation> getAllCenterLocation() {
        log.debug("REST request to get all Center Locations");
        return crmService.getAllCenterLocation();
    }

    @GetMapping("/registration-type")
    @Timed
    public List<RegistrationType> getAllRegistrationType() {
        log.debug("REST request to get all Registration Type");
        return crmService.getAllRegistrationType();
    }

    @GetMapping("/employers/occupation/{id}")
    @Timed
    public List<Employers> getAllEmployersByOccupationId(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all employers by occupation");
        return crmService.getEmployersByOccupation(id);
    }

    @GetMapping("/district/state/{id}")
    @Timed
    public List<CityDistrictTown> getDistrictByState(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all CityDistrictTown by state");
        return crmService.getDistrictByState(id);
    }

    @GetMapping("/area-name/district/area-type/{distId}/{areaTypeId}")
    @Timed
    public List<AreaName> getAreaNameByDistrictAreaType(@PathVariable(name = "distId") long distId, @PathVariable(name = "areaTypeId") long areaTypeId) {
        log.debug("REST request to get all AreaName by District and AreaType");
        return crmService.getAreaNameByDistrictAreaType(distId, areaTypeId);
    }

    @GetMapping("/police-station/area/{id}")
    @Timed
    public List<PoliceStation> getPoliceStationByAreaName(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all police station by area");
        return crmService.getPoliceStationByAreaName(id);
    }

    @GetMapping("/post-office/police-station/{id}")
    @Timed
    public List<PostOffice> getPostByPoliceStation(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all PostOffice by police station");
        return crmService.getPostByPoliceStation(id);
    }

    @GetMapping("/village/post-office/{id}")
    @Timed
    public List<PremisesBuildingVillage> getVillagesByPostOffice(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all village by post-office");
        return crmService.getVillagesByPostOffice(id);
    }

    @GetMapping("/pincode/village/{id}")
    @Timed
    public List<Pincode> getPincodeByVillage(@PathVariable(name = "id") long id) {
        log.debug("REST request to get all pincode by villagee");
        return crmService.getPincodeByVillage(id);
    }

    @GetMapping("/address/reg-inf/{id}")
    @Timed
    public List<AddressInformation> getAllAddressesByRegInfoId(@PathVariable(name = "id") long regId) {
        log.debug("REST request to get all addresses of Registration information");
        return crmService.getAllAddressesByRegInfoId(regId);
    }

    @PostMapping("twilio")
    public void responseOfCall(@RequestParam(name = "from") String from){

    }
}