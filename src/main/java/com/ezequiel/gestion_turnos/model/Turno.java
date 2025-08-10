package com.ezequiel.gestion_turnos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;


// Clase entidad de turnos
@Entity // --> le dice a Spring que esta clase es una tabla en la base de datos
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // el ID se autogenera al guardar el turno.
    private Long id;

    @NotBlank(message = "El paciente es obligatorio")
    private String paciente;

    @NotBlank(message = "El medico es obligatorio")
    private String medico;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotBlank(message = "La fecha es obligatoria")
    private LocalDateTime fechaHora;


    private boolean confirmado;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }
}

