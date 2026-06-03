INSERT INTO doctor (id, created_at, updated_at, created_by, updated_by, deleted, first_name, last_name, email, specialization, phone_number)
SELECT '423e4567-e89b-12d3-a456-426614174000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE,
       'Aarav', 'Mehta', 'aarav.mehta@hospital.com', 'Cardiology', '+91-9876500001'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE email = 'aarav.mehta@hospital.com');

INSERT INTO doctor (id, created_at, updated_at, created_by, updated_by, deleted, first_name, last_name, email, specialization, phone_number)
SELECT '423e4567-e89b-12d3-a456-426614174001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE,
       'Sara', 'Nair', 'sara.nair@hospital.com', 'Dermatology', '+91-9876500002'
WHERE NOT EXISTS (SELECT 1 FROM doctor WHERE email = 'sara.nair@hospital.com');

INSERT INTO patient (id, created_at, updated_at, created_by, updated_by, deleted, first_name, last_name, email, address, date_of_birth, registration_date)
SELECT '123e4567-e89b-12d3-a456-426614174000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE,
       'John', 'Doe', 'john.doe@example.com', '123 Main St, Springfield', DATE '1985-06-15', DATE '2024-01-10'
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'john.doe@example.com');

INSERT INTO patient (id, created_at, updated_at, created_by, updated_by, deleted, first_name, last_name, email, address, date_of_birth, registration_date)
SELECT '123e4567-e89b-12d3-a456-426614174001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE,
       'Jane', 'Smith', 'jane.smith@example.com', '456 Elm St, Shelbyville', DATE '1990-09-23', DATE '2023-12-01'
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'jane.smith@example.com');

INSERT INTO appointment (id, created_at, updated_at, created_by, updated_by, deleted, appointment_time, end_time, duration_minutes, status, notes, patient_id, doctor_id)
SELECT '523e4567-e89b-12d3-a456-426614174000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE,
       TIMESTAMP '2026-06-10 10:00:00', TIMESTAMP '2026-06-10 10:30:00', 30, 'SCHEDULED', 'Initial cardiology review',
       '123e4567-e89b-12d3-a456-426614174000', '423e4567-e89b-12d3-a456-426614174000'
WHERE NOT EXISTS (SELECT 1 FROM appointment WHERE id = '523e4567-e89b-12d3-a456-426614174000');
