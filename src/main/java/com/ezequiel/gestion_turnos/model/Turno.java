package com.ezequiel.gestion_turnos.model;

import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "turnos", indexes = {
    @Index(name = "idx_turno_fecha", columnList = "fechaHora"),
    @Index(name = "idx_turno_medico_fecha", columnList = "medicoUsername, fechaHora"),
    @Index(name = "idx_turno_paciente", columnList = "pacienteUsername")
})
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El paciente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private User paciente;

    @NotNull(message = "El médico es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private User medico;

    @NotNull(message = "La especialidad es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @NotNull(message = "La fecha y hora del turno es obligatoria")
    @Future(message = "La fecha del turno debe ser futura")
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTurno estado = EstadoTurno.PENDIENTE;

    @Column(name = "notas_paciente", length = 500)
    private String notasPaciente;

    @Column(name = "notas_medico", length = 500)
    private String notasMedico;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPaciente() {
        return paciente;
    }

    public void setPaciente(User paciente) {
        this.paciente = paciente;
    }

    public User getMedico() {
        return medico;
    }

    public void setMedico(User medico) {
        this.medico = medico;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public String getNotasPaciente() {
        return notasPaciente;
    }

    public void setNotasPaciente(String notasPaciente) {
        this.notasPaciente = notasPaciente;
    }

    public String getNotasMedico() {
        return notasMedico;
    }

    public void setNotasMedico(String notasMedico) {
        this.notasMedico = notasMedico;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean isConfirmado() {
        return estado == EstadoTurno.CONFIRMADO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turno turno = (Turno) o;
        return Objects.equals(id, turno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Turno{id=%d, estado=%s, fecha=%s, paciente=%s, medico=%s}",
                id, estado, fechaHora, 
                paciente != null ? paciente.getUsername() : null,
                medico != null ? medico.getUsername() : null);
    }
}
