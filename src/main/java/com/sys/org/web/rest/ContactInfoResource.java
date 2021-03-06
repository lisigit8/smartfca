package com.sys.org.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sys.org.domain.ContactInfo;

import com.sys.org.repository.ContactInfoRepository;
import com.sys.org.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ContactInfo.
 */
@RestController
@RequestMapping("/api")
public class ContactInfoResource {

    private final Logger log = LoggerFactory.getLogger(ContactInfoResource.class);

    private static final String ENTITY_NAME = "contactInfo";

    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoResource(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    /**
     * POST  /contact-infos : Create a new contactInfo.
     *
     * @param contactInfo the contactInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new contactInfo, or with status 400 (Bad Request) if the contactInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/contact-infos")
    @Timed
    public ResponseEntity<ContactInfo> createContactInfo(@RequestBody ContactInfo contactInfo) throws URISyntaxException {
        log.debug("REST request to save ContactInfo : {}", contactInfo);
        if (contactInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new contactInfo cannot already have an ID")).body(null);
        }
        ContactInfo result = contactInfoRepository.save(contactInfo);
        return ResponseEntity.created(new URI("/api/contact-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contact-infos : Updates an existing contactInfo.
     *
     * @param contactInfo the contactInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated contactInfo,
     * or with status 400 (Bad Request) if the contactInfo is not valid,
     * or with status 500 (Internal Server Error) if the contactInfo couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/contact-infos")
    @Timed
    public ResponseEntity<ContactInfo> updateContactInfo(@RequestBody ContactInfo contactInfo) throws URISyntaxException {
        log.debug("REST request to update ContactInfo : {}", contactInfo);
        if (contactInfo.getId() == null) {
            return createContactInfo(contactInfo);
        }
        ContactInfo result = contactInfoRepository.save(contactInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, contactInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contact-infos : get all the contactInfos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of contactInfos in body
     */
    @GetMapping("/contact-infos")
    @Timed
    public List<ContactInfo> getAllContactInfos() {
        log.debug("REST request to get all ContactInfos");
        return contactInfoRepository.findAll();
    }

    /**
     * GET  /contact-infos/:id : get the "id" contactInfo.
     *
     * @param id the id of the contactInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the contactInfo, or with status 404 (Not Found)
     */
    @GetMapping("/contact-infos/{id}")
    @Timed
    public ResponseEntity<ContactInfo> getContactInfo(@PathVariable Long id) {
        log.debug("REST request to get ContactInfo : {}", id);
        ContactInfo contactInfo = contactInfoRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(contactInfo));
    }

    /**
     * DELETE  /contact-infos/:id : delete the "id" contactInfo.
     *
     * @param id the id of the contactInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contact-infos/{id}")
    @Timed
    public ResponseEntity<Void> deleteContactInfo(@PathVariable Long id) {
        log.debug("REST request to delete ContactInfo : {}", id);
        contactInfoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
