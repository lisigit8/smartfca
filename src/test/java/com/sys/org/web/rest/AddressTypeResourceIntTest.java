package com.sys.org.web.rest;

import com.sys.org.Smartfca001App;

import com.sys.org.domain.AddressType;
import com.sys.org.repository.AddressTypeRepository;
import com.sys.org.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AddressTypeResource REST controller.
 *
 * @see AddressTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Smartfca001App.class)
public class AddressTypeResourceIntTest {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAddressTypeMockMvc;

    private AddressType addressType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AddressTypeResource addressTypeResource = new AddressTypeResource(addressTypeRepository);
        this.restAddressTypeMockMvc = MockMvcBuilders.standaloneSetup(addressTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AddressType createEntity(EntityManager em) {
        AddressType addressType = new AddressType()
            .type(DEFAULT_TYPE);
        return addressType;
    }

    @Before
    public void initTest() {
        addressType = createEntity(em);
    }

    @Test
    @Transactional
    public void createAddressType() throws Exception {
        int databaseSizeBeforeCreate = addressTypeRepository.findAll().size();

        // Create the AddressType
        restAddressTypeMockMvc.perform(post("/api/address-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addressType)))
            .andExpect(status().isCreated());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeCreate + 1);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createAddressTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = addressTypeRepository.findAll().size();

        // Create the AddressType with an existing ID
        addressType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressTypeMockMvc.perform(post("/api/address-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addressType)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAddressTypes() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList
        restAddressTypeMockMvc.perform(get("/api/address-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addressType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get the addressType
        restAddressTypeMockMvc.perform(get("/api/address-types/{id}", addressType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(addressType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAddressType() throws Exception {
        // Get the addressType
        restAddressTypeMockMvc.perform(get("/api/address-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();

        // Update the addressType
        AddressType updatedAddressType = addressTypeRepository.findOne(addressType.getId());
        updatedAddressType
            .type(UPDATED_TYPE);

        restAddressTypeMockMvc.perform(put("/api/address-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAddressType)))
            .andExpect(status().isOk());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();

        // Create the AddressType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAddressTypeMockMvc.perform(put("/api/address-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(addressType)))
            .andExpect(status().isCreated());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);
        int databaseSizeBeforeDelete = addressTypeRepository.findAll().size();

        // Get the addressType
        restAddressTypeMockMvc.perform(delete("/api/address-types/{id}", addressType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AddressType.class);
        AddressType addressType1 = new AddressType();
        addressType1.setId(1L);
        AddressType addressType2 = new AddressType();
        addressType2.setId(addressType1.getId());
        assertThat(addressType1).isEqualTo(addressType2);
        addressType2.setId(2L);
        assertThat(addressType1).isNotEqualTo(addressType2);
        addressType1.setId(null);
        assertThat(addressType1).isNotEqualTo(addressType2);
    }
}
