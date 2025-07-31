---------------------------------------------------------------------
-- DATABASE & SCHEMAS   (one physical DB, two logical schemas)
---------------------------------------------------------------------
CREATE DATABASE banking;
\c banking;

-- el rol bankuser ya existe; nos aseguramos de que sea superuser
ALTER ROLE bankuser WITH SUPERUSER;

-- dos esquemas lógicos, uno para cada micro-servicio
CREATE SCHEMA IF NOT EXISTS customer AUTHORIZATION bankuser;
CREATE SCHEMA IF NOT EXISTS account  AUTHORIZATION bankuser;

---------------------------------------------------------------------
-- TABLES  ──────────────────────────────────────────────────────────
---------------------------------------------------------------------
-- 1️⃣  CUSTOMER-SERVICE
CREATE SCHEMA IF NOT EXISTS customer;

CREATE TABLE IF NOT EXISTS customer.person (
                                               id             BIGSERIAL    PRIMARY KEY,
                                               identification VARCHAR(20)  NOT NULL UNIQUE,
    first_name     VARCHAR(80)  NOT NULL,
    last_name      VARCHAR(80)  NOT NULL,
    gender         VARCHAR(1)   NOT NULL CHECK (gender IN ('M','F')),  -- ← CAMBIO
    age            INT          NOT NULL CHECK (age >= 0),
    address        VARCHAR(120) NOT NULL,
    phone          VARCHAR(30)  NOT NULL
    );

CREATE TABLE customer.customer (
                                   id            BIGSERIAL    PRIMARY KEY,
                                   person_id     BIGINT       NOT NULL REFERENCES customer.person(id) ON DELETE CASCADE,
                                   password_hash VARCHAR(120) NOT NULL,              -- ⬅️  ahora coincide con passwordHash
                                   status        VARCHAR(10)  NOT NULL
);

-- 2️⃣  ACCOUNT-SERVICE
CREATE TABLE account.account (
                                 id              BIGSERIAL      PRIMARY KEY,
                                 number          VARCHAR(20)    NOT NULL UNIQUE,   -- ⬅️  ahora coincide con number
                                 type            VARCHAR(10)    NOT NULL,          -- SAVINGS | CHECKING
                                 initial_balance NUMERIC(18,2)  NOT NULL,
                                 current_balance NUMERIC(18,2)  NOT NULL,
                                 status          VARCHAR(10)    NOT NULL,          -- ACTIVE | INACTIVE
                                 customer_id     BIGINT         NOT NULL           -- FK → customer.customer
);

CREATE INDEX idx_account_customer ON account.account(customer_id);

CREATE TABLE account.movement (
                                  id            BIGSERIAL      PRIMARY KEY,
                                  account_id    BIGINT         NOT NULL REFERENCES account.account(id) ON DELETE CASCADE,
                                  type          VARCHAR(6)     NOT NULL,            -- DEBIT | CREDIT
                                  amount        NUMERIC(18,2)  NOT NULL,
                                  balance       NUMERIC(18,2)  NOT NULL,
                                  date          TIMESTAMP      NOT NULL DEFAULT now()
);

---------------------------------------------------------------------
-- SAMPLE DATA  (idéntico al enunciado, solo cambian los nombres)
---------------------------------------------------------------------
-- Personas
INSERT INTO customer.person (identification, first_name, last_name, gender, age, address, phone)
VALUES  ('1100110011', 'Jose',      'Lema',      'M', 31, 'Otavalo sn y principal', '098254785'),
        ('2200220022', 'Marianela', 'Montalvo',  'F', 28, 'Amazonas y NNUU',        '097548965'),
        ('3300330033', 'Juan',      'Osorio',    'M', 38, '13 junio y Equinoccial', '098874587');

-- Clientes
INSERT INTO customer.customer (password_hash, status, person_id)
SELECT '1234', 'ACTIVE', p.id FROM customer.person p WHERE p.identification = '1100110011'
UNION ALL
SELECT '5678', 'ACTIVE', p.id FROM customer.person p WHERE p.identification = '2200220022'
UNION ALL
SELECT '1245', 'ACTIVE', p.id FROM customer.person p WHERE p.identification = '3300330033';

-- Cuentas
-- José Lema
INSERT INTO account.account (number, type, initial_balance, current_balance, status, customer_id)
SELECT '478758', 'SAVINGS', 2000, 2000, 'ACTIVE', c.id
FROM customer.customer c JOIN customer.person p ON c.person_id = p.id
WHERE p.identification = '1100110011';

-- Marianela Montalvo
INSERT INTO account.account (number, type, initial_balance, current_balance, status, customer_id)
SELECT '225487', 'CHECKING', 100, 100, 'ACTIVE', c.id
FROM customer.customer c JOIN customer.person p ON c.person_id = p.id
WHERE p.identification = '2200220022';

INSERT INTO account.account (number, type, initial_balance, current_balance, status, customer_id)
SELECT '496825', 'SAVINGS', 540, 540, 'ACTIVE', c.id
FROM customer.customer c JOIN customer.person p ON c.person_id = p.id
WHERE p.identification = '2200220022';

-- Juan Osorio
INSERT INTO account.account (number, type, initial_balance, current_balance, status, customer_id)
SELECT '495878', 'SAVINGS', 0, 0, 'ACTIVE', c.id
FROM customer.customer c JOIN customer.person p ON c.person_id = p.id
WHERE p.identification = '3300330033';

-- Cuenta extra para José Lema
INSERT INTO account.account (number, type, initial_balance, current_balance, status, customer_id)
SELECT '585545', 'CHECKING', 1000, 1000, 'ACTIVE', c.id
FROM customer.customer c JOIN customer.person p ON c.person_id = p.id
WHERE p.identification = '1100110011';

-- Movimientos de ejemplo
INSERT INTO account.movement (account_id, type, amount, balance)
SELECT a.id, 'DEBIT',  -575, a.current_balance - 575 FROM account.account a WHERE a.number = '478758';
UPDATE account.account SET current_balance = current_balance - 575 WHERE number = '478758';

INSERT INTO account.movement (account_id, type, amount, balance)
SELECT a.id, 'CREDIT', 600, a.current_balance + 600 FROM account.account a WHERE a.number = '225487';
UPDATE account.account SET current_balance = current_balance + 600 WHERE number = '225487';

INSERT INTO account.movement (account_id, type, amount, balance)
SELECT a.id, 'CREDIT', 150, a.current_balance + 150 FROM account.account a WHERE a.number = '495878';
UPDATE account.account SET current_balance = current_balance + 150 WHERE number = '495878';

INSERT INTO account.movement (account_id, type, amount, balance)
SELECT a.id, 'DEBIT',  -540, a.current_balance - 540 FROM account.account a WHERE a.number = '496825';
UPDATE account.account SET current_balance = current_balance - 540 WHERE number = '496825';
