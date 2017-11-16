--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 16394)
-- Name: election; Type: SCHEMA; Schema: -; Owner: postgres
--
CREATE SCHEMA election;
ALTER SCHEMA election OWNER TO postgres;

SET search_path = election, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;
--

CREATE TABLE candidates (
    id SERIAL PRIMARY KEY,
    title TEXT,
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL,
    profession TEXT,
    sex TEXT CHECK (sex IN ('M','W')),
    hometown TEXT,
    birthtown TEXT,
    yearofbirth INTEGER,
    UNIQUE (firstname, lastname, yearofbirth)
);

CREATE TABLE elections (
    year integer NOT NULL PRIMARY KEY,
    day date UNIQUE
);

CREATE TABLE parties (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE states (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE districts (
    id SERIAL PRIMARY KEY,
    number INTEGER NOT NULL CHECK (number BETWEEN 1 AND 299),
    year INTEGER NOT NULL REFERENCES elections(year),
    state INTEGER NOT NULL REFERENCES states(id),
    name TEXT,
    eligibleVoters INTEGER NOT NULL,
    invalidFirstVotes INTEGER NOT NULL, -- aggregated
    invalidSecondVotes INTEGER NOT NULL -- aggregated
);

CREATE TABLE secondvote_aggregates (
    district INTEGER NOT NULL REFERENCES districts(id),
    party INTEGER NOT NULL REFERENCES parties(id),
    votes INTEGER NOT NULL,
    PRIMARY KEY (district,party)
);

CREATE TABLE direct_candidatures (
    id SERIAL PRIMARY KEY,
    district INTEGER NOT NULL REFERENCES districts(id),
    candidate INTEGER NOT NULL REFERENCES candidates(id),
    party INTEGER NOT NULL REFERENCES parties(id),
    votes INTEGER NOT NULL
);

CREATE TABLE statelists (
    id SERIAL PRIMARY KEY,
    party INTEGER NOT NULL REFERENCES parties(id),
    election INTEGER NOT NULL REFERENCES elections(year),
    state INTEGER NOT NULL REFERENCES states(id)
);

CREATE TABLE list_candidatures (
    id SERIAL PRIMARY KEY,
    candidate INTEGER NOT NULL REFERENCES candidates(id),
    statelist INTEGER NOT NULL REFERENCES statelists(id),
    placement INTEGER NOT NULL
);

CREATE TABLE ballots (
    firstvote INTEGER REFERENCES direct_candidatures(id) DEFERRABLE,
    secondvote INTEGER REFERENCES list_candidatures(id) DEFERRABLE,
    district INTEGER NOT NULL REFERENCES districts(id) DEFERRABLE
);

ALTER TABLE ballots OWNER TO postgres;
ALTER TABLE candidates OWNER TO postgres;
ALTER TABLE direct_candidatures OWNER TO postgres;
ALTER TABLE districts OWNER TO postgres;
ALTER TABLE elections OWNER TO postgres;
ALTER TABLE list_candidatures OWNER TO postgres;
ALTER TABLE parties OWNER TO postgres;
ALTER TABLE secondvote_aggregates OWNER TO postgres;
ALTER TABLE states OWNER TO postgres;
ALTER TABLE statelists OWNER TO postgres;

--
-- PostgreSQL database dump complete
--
