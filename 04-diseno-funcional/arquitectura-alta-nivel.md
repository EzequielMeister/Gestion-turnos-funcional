# Arquitectura funcional de alto nivel

El sistema se divide en los siguientes módulos:

- **Frontend Web/App**: Interfaz de usuario para pacientes, médicos y recepcionistas.
- **Backend/API**: Servicio que procesa las solicitudes, gestiona la base de datos y expone endpoints.
- **Base de Datos**: Almacena pacientes, médicos, turnos, agendas, etc.
- **Notificaciones**: Módulo encargado de enviar correos electrónicos de confirmación o recordatorio.
- **Autenticación**: Sistema de login/registro con validación de identidad de usuarios.

Se considera que el otorgamiento de turnos puede ser gestionado por una app o sistema externo que actúa como actor del sistema.
