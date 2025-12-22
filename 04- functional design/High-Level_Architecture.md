# High-Level Functional Architecture

The system is divided into the following modules:

- **Web/App Frontend**: User interface for patients, doctors, and reception staff.
- **Backend / API**: Service responsible for processing requests, managing the database, and exposing endpoints.
- **Database**: Stores information related to patients, doctors, appointments, schedules, and related data.
- **Notifications**: Module responsible for sending confirmation and reminder emails.
- **Authentication**: Login and registration system with user identity validation.

It is considered that appointment scheduling may also be managed by an external application or system acting as a system actor.
