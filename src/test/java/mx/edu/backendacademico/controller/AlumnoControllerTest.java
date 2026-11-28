package mx.edu.backendacademico.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import mx.edu.backendacademico.domain.*;
import mx.edu.backendacademico.service.AlumnoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlumnoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlumnoControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean AlumnoService service;

    @Test
    void listarDevuelveJson() throws Exception {
        when(service.listar()).thenReturn(List.of(new Alumno(1L, "A1", "Ada", "ada@u.mx", EstatusAlumno.ACTIVO)));
        mvc.perform(get("/api/v1/alumnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("A1"));
    }

    @Test
    void crearDevuelve201YHeaderLocation() throws Exception {
        when(service.crear(any(), any(), any()))
                .thenReturn(new Alumno(42L, "A100", "Ada", "ada@u.mx", EstatusAlumno.ACTIVO));

        mvc.perform(post("/api/v1/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"A100\",\"nombre\":\"Ada\",\"correo\":\"ada@u.mx\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/api/v1/alumnos/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.matricula").value("A100"));
    }

    @Test
    void rechazaUnContratoInvalido() throws Exception {
        mvc.perform(post("/api/v1/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"\",\"nombre\":\"A\",\"correo\":\"incorrecto\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
        org.mockito.Mockito.verifyNoInteractions(service);
    }
}