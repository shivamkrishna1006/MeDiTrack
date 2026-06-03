CREATE TABLE document
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMP,
    document_type     VARCHAR(50)  NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL,
    content_type      VARCHAR(150) NOT NULL,
    size_bytes        BIGINT       NOT NULL,
    patient_id        UUID         NOT NULL,
    medical_record_id UUID,
    CONSTRAINT fk_document_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_document_medical_record FOREIGN KEY (medical_record_id) REFERENCES medical_record (id)
);

CREATE TABLE notification
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMP,
    channel           VARCHAR(30)  NOT NULL,
    type              VARCHAR(50)  NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    recipient_email   VARCHAR(255) NOT NULL,
    subject           VARCHAR(255) NOT NULL,
    body              VARCHAR(2000) NOT NULL,
    scheduled_at      TIMESTAMP,
    sent_at           TIMESTAMP,
    failure_reason    VARCHAR(1000),
    related_entity_id UUID,
    related_entity_type VARCHAR(100)
);

CREATE TABLE audit_log
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMP,
    actor             VARCHAR(255),
    action            VARCHAR(100) NOT NULL,
    entity_type       VARCHAR(100) NOT NULL,
    entity_id         UUID,
    message           VARCHAR(1000)
);

CREATE TABLE refresh_token
(
    id         UUID PRIMARY KEY,
    token      VARCHAR(255) NOT NULL UNIQUE,
    username   VARCHAR(100) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL
);

CREATE INDEX idx_document_patient ON document (patient_id);
CREATE INDEX idx_notification_status ON notification (status);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);
