package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.dto.EspecialidadRequest;
import com.ezequiel.gestion_turnos.dto.EspecialidadResponse;
import com.ezequiel.gestion_turnos.model.Especialidad;
import com.ezequiel.gestion_turnos.repository.EspecialidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para EspecialidadService")
class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadService especialidadService;

    private Especialidad especialidad;
    private EspecialidadRequest especialidadRequest;

    @BeforeEach
    void setUp() {
        especialidad = new Especialidad();
        especialidad.setId(1L);
        especialidad.setNombre("Cardiología");
        especialidad.setDescripcion("Especialidad del corazón");
        especialidad.setDuracionMinutos(45);
        especialidad.setPrecio(500.0);
        especialidad.setActiva(true);

        especialidadRequest = new EspecialidadRequest(
                "Cardiología",
                "Especialidad del corazón",
                45,
                500.0
        );
    }

    @Test
    @DisplayName("Listar especialidades activas")
    void listarActivas_Exitoso() {
        when(especialidadRepository.findByActivaTrue()).thenReturn(List.of(especialidad));

        List<EspecialidadResponse> result = especialidadService.listarActivas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Cardiología");
        assertThat(result.get(0).activa()).isTrue();
    }

    @Test
    @DisplayName("Crear especialidad exitosamente")
    void crear_DatosValidos_Exitoso() {
        when(especialidadRepository.existsByNombre("Cardiología")).thenReturn(false);
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);

        EspecialidadResponse result = especialidadService.crear(especialidadRequest);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Cardiología");
        verify(especialidadRepository).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("Error al crear especialidad con nombre duplicado")
    void crear_NombreDuplicado_Error() {
        when(especialidadRepository.existsByNombre("Cardiología")).thenReturn(true);

        assertThatThrownBy(() -> especialidadService.crear(especialidadRequest))
                .isInstanceOf(DatosDuplicadosException.class)
                .hasMessageContaining("Cardiología");
    }

    @Test
    @DisplayName("Obtener especialidad por ID exitosamente")
    void obtenerPorId_Existente_Exitoso() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));

        EspecialidadResponse result = especialidadService.obtenerPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Error al obtener especialidad inexistente")
    void obtenerPorId_Inexistente_Error() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Eliminar especialidad (soft delete)")
    void eliminar_Existente_Exitoso() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);

        especialidadService.eliminar(1L);

        verify(especialidadRepository).save(argThat(e -> !e.isActiva()));
    }

    @Test
    @DisplayName("Error al eliminar especialidad inexistente")
    void eliminar_Inexistente_Error() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Actualizar especialidad exitosamente")
    void actualizar_DatosValidos_Exitoso() {
        EspecialidadRequest updateRequest = new EspecialidadRequest(
                "Cardiología",
                "Descripción actualizada",
                60,
                600.0
        );

        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidad));
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);

        EspecialidadResponse result = especialidadService.actualizar(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(especialidadRepository).save(any(Especialidad.class));
    }
}
