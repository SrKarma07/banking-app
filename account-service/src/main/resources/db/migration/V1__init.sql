CREATE TABLE account (
                         id               BIGSERIAL PRIMARY KEY,
                         number           VARCHAR(20) NOT NULL UNIQUE,
                         type             VARCHAR(10) NOT NULL,
                         initial_balance  NUMERIC(18,2) NOT NULL,
                         current_balance  NUMERIC(18,2) NOT NULL,
                         status           VARCHAR(10) NOT NULL
);

CREATE TABLE movement (
                          id         BIGSERIAL PRIMARY KEY,
                          account_id BIGINT      NOT NULL REFERENCES account(id),
                          type       VARCHAR(6)  NOT NULL,
                          amount     NUMERIC(18,2) NOT NULL,
                          balance    NUMERIC(18,2) NOT NULL,
                          date       TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
