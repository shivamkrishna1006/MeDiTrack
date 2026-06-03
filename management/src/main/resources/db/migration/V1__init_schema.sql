CREATE TABLE app_user
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    deleted      BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at   TIMESTAMP,
    username     VARCHAR(100) NOT NULL UNIQUE,
    full_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(30)  NOT NULL
);

CREATE TABLE doctor
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    deleted        BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMP,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    specialization VARCHAR(150) NOT NULL,
    phone_number   VARCHAR(30)  NOT NULL
);

CREATE TABLE patient
(
    id                 UUID PRIMARY KEY,
    created_at         TIMESTAMP   NOT NULL,
    updated_at         TIMESTAMP   NOT NULL,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    deleted            BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at         TIMESTAMP,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100) NOT NULL,
    email              VARCHAR(255) NOT NULL UNIQUE,
    address            VARCHAR(255) NOT NULL,
    date_of_birth      DATE         NOT NULL,
    registration_date  DATE         NOT NULL
);

CREATE TABLE appointment
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP   NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMP,
    appointment_time TIMESTAMP   NOT NULL,
    end_time         TIMESTAMP   NOT NULL,
    duration_minutes INTEGER     NOT NULL,
    status           VARCHAR(30) NOT NULL,
    notes            VARCHAR(1000),
    patient_id       UUID        NOT NULL,
    doctor_id        UUID        NOT NULL,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id)
);

CREATE INDEX idx_patient_email ON patient (email);
CREATE INDEX idx_doctor_email ON doctor (email);
CREATE INDEX idx_appointment_time ON appointment (appointment_time);
CREATE INDEX idx_appointment_doctor_time ON appointment (doctor_id, appointment_time);
