## Caso: Registro de nuevo paciente

**Precondiciones:**  
El paciente no debe tener cuenta registrada.

**Pasos:**
1. Ingresar al formulario de registro.
2. Completar los datos obligatorios (nombre, DNI, correo, contraseña).
3. Hacer clic en "Registrarse".

**Resultado esperado:**  
Se muestra mensaje de registro exitoso. El sistema redirige a la pantalla de login.

---

## Caso: Solicitud de turno

**Precondiciones:**  
El paciente debe estar autenticado. El médico debe tener disponibilidad.

**Pasos:**
1. Acceder a la opción “Solicitar turno”.
2. Seleccionar especialidad, médico, fecha y horario.
3. Confirmar solicitud.

**Resultado esperado:**  
Se muestra mensaje de turno confirmado. Se envía mail con los datos del turno.
