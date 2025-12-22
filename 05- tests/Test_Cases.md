## Test Case: New Patient Registration

**Preconditions:**  
The patient must not have an existing account.

**Steps:**
1. Access the registration form.
2. Complete the required fields (name, ID number, email, password).
3. Click on "Register".

**Expected Result:**  
A successful registration message is displayed. The system redirects the user to the login screen.

---

## Test Case: Appointment Request

**Preconditions:**  
The patient must be authenticated. The doctor must have available time slots.

**Steps:**
1. Access the “Request Appointment” option.
2. Select specialty, doctor, date, and time.
3. Confirm the request.

**Expected Result:**  
An appointment confirmation message is displayed. An email with the appointment details is sent to the patient.
