CREATE TABLE customer.person (         -- ó CREATE TABLE person ( en Flyway)
                                 id             BIGSERIAL    PRIMARY KEY,
                                 identification VARCHAR(20)  NOT NULL UNIQUE,
                                 first_name     VARCHAR(80)  NOT NULL,
                                 last_name      VARCHAR(80)  NOT NULL,
                                 gender         CHAR(1)      NOT NULL,   --  <--  añade esta línea
                                 age            INT          NOT NULL CHECK (age >= 0),
                                 address        VARCHAR(120) NOT NULL,
                                 phone          VARCHAR(30)  NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
                                        id            BIGSERIAL    PRIMARY KEY,
                                        person_id     BIGINT       NOT NULL REFERENCES person(id),
    password_hash VARCHAR(120) NOT NULL,
    status        VARCHAR(10)  NOT NULL
    );
