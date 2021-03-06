package pe.joedayz.agenda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.joedayz.agenda.domain.Departamento;
import pe.joedayz.agenda.domain.Reunion;
import pe.joedayz.agenda.service.ReunionService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for ReunionController RESTful endpoints.
 * 
 * @author joe
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReunionControllerTest {

	private MockMvc mockMvc;
	
    @Mock
    private ReunionService reunionService;
    
    @InjectMocks
    private ReunionController reunionController;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(reunionController)
                .build();
    }
    
    @Test
    public void test_get_all_success() throws Exception {
        List<Departamento> departamentos = Arrays.asList(
        		new Departamento(1, "Ingenieria", "Departamento de Ingenieria"),
        		new Departamento(2, "Diseno", "Departamento de Diseno"));
    	
        List<Reunion> reuniones = Arrays.asList(
        		new Reunion(1, "Revision", "Revision Semanal", departamentos),
        		new Reunion(2, "Scrum", "Reunion Scrum", departamentos));
        
        when(reunionService.list()).thenReturn(reuniones);
        
        mockMvc.perform(get("/reuniones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Revision")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Scrum")));
        
        verify(reunionService, times(1)).list();
        verifyNoMoreInteractions(reunionService);
    }
    
    @Test
    public void test_get_by_id_success() throws Exception {
        List<Departamento> departamentos = Arrays.asList(
        		new Departamento(1, "Ingenieria", "Departamento de Ingenieria"),
        		new Departamento(2, "Diseno", "Departamento de Diseno"));
    	Reunion reunion = new Reunion(1, "Revision", "Revision Semanal", departamentos);
    	
        when(reunionService.findById(1)).thenReturn(reunion);
        
        mockMvc.perform(get("/reuniones/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Revision")));
        
        verify(reunionService, times(1)).findById(1);
        verifyNoMoreInteractions(reunionService);
    }
    
    @Test
    public void test_get_by_id_fail_404_not_found() throws Exception {
        when(reunionService.findById(1)).thenReturn(null);
        
        mockMvc.perform(get("/reuniones/{id}", 1))
                .andExpect(status().isNotFound());
        
        verify(reunionService, times(1)).findById(1);
        verifyNoMoreInteractions(reunionService);
    }
    
    @Test
    public void test_create_success() throws Exception {
        List<Departamento> departments = Arrays.asList(
        		new Departamento(1, "Ingenieria", "Departamento de Ingenieria"),
        		new Departamento(2, "Diseno", "Departamento de Diseno"));
    	Reunion reunion = new Reunion(1, "Revision", "Revision Semanal", departments);
    	
        when(reunionService.saveReunion(reunion)).thenReturn(Boolean.TRUE);
        
        mockMvc.perform(post("/reuniones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(reunion)))
            		.andExpect(status().isCreated());
        
        verify(reunionService, times(1)).saveReunion(reunion);
        verifyNoMoreInteractions(reunionService);
    }
    
    @Test
    public void test_update_success() throws Exception {
        List<Departamento> departamentos = Arrays.asList(
        		new Departamento(1, "Ingenieria", "Departamento de Ingenieria"),
        		new Departamento(2, "Diseno", "Departamento de Diseno"));
    	Reunion reunion = new Reunion(1, "Revision", "Revision Semanal", departamentos);
    	
        when(reunionService.updateReunion(reunion)).thenReturn(Boolean.TRUE);
        
        mockMvc.perform(
                put("/reuniones/{id}", reunion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(reunion)))
                	.andExpect(status().isOk());
        
        verify(reunionService, times(1)).updateReunion(reunion);
        verifyNoMoreInteractions(reunionService);
    }
    
    @Test
    public void test_delete_success() throws Exception {
        List<Departamento> departamentos = Arrays.asList(
        		new Departamento(1, "Ingenieria", "Departamento de Ingenieria"),
        		new Departamento(2, "Diseno", "Departamento de Diseno"));
    	Reunion reunion = new Reunion(1, "Revision", "Revision Semanal", departamentos);
    	
        when(reunionService.deleteById(reunion.getId())).thenReturn(Boolean.TRUE);
        
        mockMvc.perform(
                delete("/reuniones/{id}", reunion.getId()))
                .andExpect(status().isNoContent());
        
        verify(reunionService, times(1)).deleteById(reunion.getId());
        verifyNoMoreInteractions(reunionService);
    }    
    
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
}
