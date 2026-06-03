CREATE TABLE medical_record
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMP,
    visit_date        DATE        NOT NULL,
    diagnosis         VARCHAR(500) NOT NULL,
    allergies         VARCHAR(1000),
    notes             VARCHAR(2000),
    patient_id        UUID        NOT NULL,
    doctor_id         UUID        NOT NULL,
    CONSTRAINT fk_medical_record_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_medical_record_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id)
);

CREATE TABLE prescription
(
    id                 UUID PRIMARY KEY,
    created_at         TIMESTAMP   NOT NULL,
    updated_at         TIMESTAMP   NOT NULL,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    deleted            BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at         TIMESTAMP,
    medication_name    VARCHAR(255) NOT NULL,
    dosage             VARCHAR(255) NOT NULL,
    frequency          VARCHAR(255) NOT NULL,
    duration_in_days   INTEGER      NOT NULL,
    instructions       VARCHAR(2000),
    status             VARCHAR(30)  NOT NULL,
    patient_id         UUID         NOT NULL,
    doctor_id          UUID         NOT NULL,
    medical_record_id  UUID,
    CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id),
    CONSTRAINT fk_prescription_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_record (id)
);

CREATE TABLE billing_record
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMP,
    invoice_number    VARCHAR(100) NOT NULL UNIQUE,
    amount            DECIMAL(12,2) NOT NULL,
    status            VARCHAR(30) NOT NULL,
    due_date          DATE        NOT NULL,
    paid_date         DATE,
    description       VARCHAR(1000),
    patient_id        UUID        NOT NULL,
    appointment_id    UUID,
    CONSTRAINT fk_billing_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_billing_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id)
);

CREATE INDEX idx_medical_record_patient_visit ON medical_record (patient_id, visit_date);
CREATE INDEX idx_prescription_patient_status ON prescription (patient_id, status);
CREATE INDEX idx_billing_patient_status ON billing_record (patient_id, status);
