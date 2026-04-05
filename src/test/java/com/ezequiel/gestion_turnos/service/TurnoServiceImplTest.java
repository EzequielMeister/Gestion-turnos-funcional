package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.dto.TurnoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoResponse;
import com.ezequiel.gestion_turnos.model.Especialidad;
import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.model.User;
import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;
import com.ezequiel.gestion_turnos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TurnoService")
class TurnoServiceImplTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EspecialidadService especialidadService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    private User paciente;
    private User medico;
    private Especialidad especialidad;
    private Turno turno;
    private TurnoRequest turnoRequest;

    @BeforeEach
    void setUp() {
        paciente = new User();
        paciente.setId(1L);
        paciente.setUsername("paciente");
        paciente.setPassword("pass");
        paciente.setEnabled(true);

        medico = new User();
        medico.setId(2L);
        medico.setUsername("medico");
        medico.setPassword("pass");
        medico.setEnabled(true);

        especialidad = new Especialidad();
        especialidad.setId(1L);
        especialidad.setNombre("Clínica General");
        especialidad.setDuracionMinutos(30);

        LocalDateTime manana = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        turno = new Turno();
        turno.setId(1L);
        turno.setPaciente(paciente);
        turno.setMedico(medico);
        turno.setEspecialidad(especialidad);
        turno.setFechaHora(manana);
        turno.setEstado(EstadoTurno.PENDIENTE);

        turnoRequest = new TurnoRequest(1L, 2L, manana, "Primera consulta");
    }

    @Test
    @DisplayName("BR-02: Turno creado exitosamente dentro del horario de atención")
    void crearTurno_DentroHorarioAtencion_Exitoso() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("paciente");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("paciente")).thenReturn(Optional.of(paciente));
        when(userRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(especialidadService.obtenerEntidadPorId(1L)).thenReturn(especialidad);
        when(turnoRepository.save(any(Turno.class))).thenReturn(turno);

        TurnoResponse response = turnoService.crearTurno(turnoRequest);

        assertThat(response).isNotNull();
        assertThat(response.estado()).isEqualTo(EstadoTurno.PENDIENTE);
        verify(turnoRepository).save(any(Turno.class));
    }

    @Test
    @DisplayName("BR-02: Error al crear turno fuera del horario de atención (8:00-18:00)")
    void crearTurno_FueraHorarioAtencion_Error() {
        TurnoRequest requestFueraHorario = new TurnoRequest(
                1L, 2L, 
                LocalDateTime.now().plusDays(1).withHour(20).withMinute(0), 
                "Consulta");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("paciente");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("paciente")).thenReturn(Optional.of(paciente));
        when(userRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(especialidadService.obtenerEntidadPorId(1L)).thenReturn(especialidad);

        assertThatThrownBy(() -> turnoService.crearTurno(requestFueraHorario))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("BR-02");
    }

    @Test
    @DisplayName("Error al crear turno en fecha pasada")
    void crearTurno_FechaPasada_Error() {
        TurnoRequest requestPasado = new TurnoRequest(
                1L, 2L, 
                LocalDateTime.now().minusDays(1), 
                "Consulta");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("paciente");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("paciente")).thenReturn(Optional.of(paciente));
        when(userRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(especialidadService.obtenerEntidadPorId(1L)).thenReturn(especialidad);

        assertThatThrownBy(() -> turnoService.crearTurno(requestPasado))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("pasadas");
    }

    @Test
    @DisplayName("Obtener turno por ID exitosamente")
    void obtenerTurno_Existente_Exitoso() {
        when(turnoRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(turno));

        TurnoResponse response = turnoService.obtenerTurno(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.estado()).isEqualTo(EstadoTurno.PENDIENTE);
    }

    @Test
    @DisplayName("Error al obtener turno inexistente")
    void obtenerTurno_Inexistente_Error() {
        when(turnoRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turnoService.obtenerTurno(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Verificar disponibilidad de médico")
    void tieneDisponibilidad_MedicoDisponible_True() {
        when(turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNot(
                eq(2L), any(LocalDateTime.class), eq(EstadoTurno.CANCELADO)))
                .thenReturn(false);

        boolean disponible = turnoService.tieneDisponibilidad(
                2L, LocalDateTime.now().plusDays(1).withHour(10));

        assertThat(disponible).isTrue();
    }

    @Test
    @DisplayName("Verificar disponibilidad - médico ocupado")
    void tieneDisponibilidad_MedicoOcupado_False() {
        when(turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNot(
                eq(2L), any(LocalDateTime.class), eq(EstadoTurno.CANCELADO)))
                .thenReturn(true);

        boolean disponible = turnoService.tieneDisponibilidad(
                2L, LocalDateTime.now().plusDays(1).withHour(10));

        assertThat(disponible).isFalse();
    }

    @Test
    @DisplayName("Cancelar turno por el paciente")
    void cancelarTurno_PorPaciente_Exitoso() {
        when(turnoRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(turno));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("paciente");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("paciente")).thenReturn(Optional.of(paciente));
        when(turnoRepository.save(any(Turno.class))).thenReturn(turno);

        turnoService.cancelarTurno(1L);

        verify(turnoRepository).save(argThat(t -> t.getEstado() == EstadoTurno.CANCELADO));
    }

    @Test
    @DisplayName("Error al cancelar turno ya completado")
    void cancelarTurno_YaCompletado_Error() {
        turno.setEstado(EstadoTurno.COMPLETADO);
        when(turnoRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(turno));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("paciente");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("paciente")).thenReturn(Optional.of(paciente));

        assertThatThrownBy(() -> turnoService.cancelarTurno(1L))
                .isInstanceOf(ReglaDeNegocioException.class)
                .hasMessageContaining("COMPLETADO");
    }

    @Test
    @DisplayName("Admin puede listar todos los turnos")
    void listarTurnos_Admin_ListaTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Turno> paginaTurnos = new PageImpl<>(List.of(turno));

        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        when(authentication.getName()).thenReturn("admin");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(paciente));
        when(turnoRepository.findAllWithDetails(pageable)).thenReturn(paginaTurnos);

        Page<TurnoResponse> result = turnoService.listarTurnos(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(turnoRepository).findAllWithDetails(pageable);
    }
}
