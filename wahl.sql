--
-- PostgreSQL database dump
--

-- Dumped from database version 10.0
-- Dumped by pg_dump version 10.0

-- Started on 2017-11-11 22:36:38

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
-- TOC entry 200 (class 1259 OID 16410)
-- Name: Bundesland; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Bundesland" (
    "Kuerzel" character(10) NOT NULL,
    "Name" character(100) NOT NULL,
    "SitzZahl" integer NOT NULL
);


ALTER TABLE "Bundesland" OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16506)
-- Name: Direktkandidatur; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Direktkandidatur" (
    "ID" integer NOT NULL,
    "Wahl" integer NOT NULL,
    "Wahhlkreis" character(10) NOT NULL,
    "Kandidat" integer NOT NULL,
    "Partei" character(10),
    "Stimmen" integer
);


ALTER TABLE "Direktkandidatur" OWNER TO postgres;

--
-- TOC entry 199 (class 1259 OID 16405)
-- Name: Kandidat; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Kandidat" (
    "ID" integer NOT NULL,
    "Vorname" character(100) NOT NULL,
    nachname character(100) NOT NULL,
    beruf character(100),
    geburtsdatum date NOT NULL,
    titel character(5),
    geschlecht "char" NOT NULL
);


ALTER TABLE "Kandidat" OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16430)
-- Name: Landesliste; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Landesliste" (
    "ID" integer NOT NULL,
    "Partei" character(1) NOT NULL,
    "Wahl" integer NOT NULL,
    "Bundesland" character(1) NOT NULL,
    "Stimmen" integer NOT NULL
);


ALTER TABLE "Landesliste" OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16468)
-- Name: Landeslistenkandidatur; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Landeslistenkandidatur" (
    "Kandidat" integer NOT NULL,
    "Landesliste" integer NOT NULL,
    "Listenplatz" integer NOT NULL
);


ALTER TABLE "Landeslistenkandidatur" OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 16395)
-- Name: Partei; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Partei" (
    kuerzel character(10) NOT NULL,
    "Name" character(100)
);


ALTER TABLE "Partei" OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16531)
-- Name: Stimme; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Stimme" (
    "ID" integer NOT NULL,
    "Erststimme" integer NOT NULL,
    "Zweitstimme" integer NOT NULL,
    wahlkreis character(10) NOT NULL
);


ALTER TABLE "Stimme" OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 16400)
-- Name: Wahl; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Wahl" (
    "Jahr" integer NOT NULL,
    datum date NOT NULL
);


ALTER TABLE "Wahl" OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 16415)
-- Name: Wahlkreis; Type: TABLE; Schema: election; Owner: postgres
--

CREATE TABLE "Wahlkreis" (
    "Name" character(100) NOT NULL,
    "Wahl" integer NOT NULL,
    "Waehlerzahl" integer NOT NULL,
    ungueltige_1 integer NOT NULL,
    ungueltige_2 integer NOT NULL,
    bundesland character(10) NOT NULL
);


ALTER TABLE "Wahlkreis" OWNER TO postgres;

--
-- TOC entry 2857 (class 0 OID 16410)
-- Dependencies: 200
-- Data for Name: Bundesland; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Bundesland" ("Kuerzel", "Name", "SitzZahl") FROM stdin;
\.


--
-- TOC entry 2861 (class 0 OID 16506)
-- Dependencies: 204
-- Data for Name: Direktkandidatur; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Direktkandidatur" ("ID", "Wahl", "Wahhlkreis", "Kandidat", "Partei", "Stimmen") FROM stdin;
\.


--
-- TOC entry 2856 (class 0 OID 16405)
-- Dependencies: 199
-- Data for Name: Kandidat; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Kandidat" ("ID", "Vorname", nachname, beruf, geburtsdatum, titel, geschlecht) FROM stdin;
\.


--
-- TOC entry 2859 (class 0 OID 16430)
-- Dependencies: 202
-- Data for Name: Landesliste; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Landesliste" ("ID", "Partei", "Wahl", "Bundesland", "Stimmen") FROM stdin;
\.


--
-- TOC entry 2860 (class 0 OID 16468)
-- Dependencies: 203
-- Data for Name: Landeslistenkandidatur; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Landeslistenkandidatur" ("Kandidat", "Landesliste", "Listenplatz") FROM stdin;
\.


--
-- TOC entry 2854 (class 0 OID 16395)
-- Dependencies: 197
-- Data for Name: Partei; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Partei" (kuerzel, "Name") FROM stdin;
\.


--
-- TOC entry 2862 (class 0 OID 16531)
-- Dependencies: 205
-- Data for Name: Stimme; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Stimme" ("ID", "Erststimme", "Zweitstimme", wahlkreis) FROM stdin;
\.


--
-- TOC entry 2855 (class 0 OID 16400)
-- Dependencies: 198
-- Data for Name: Wahl; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Wahl" ("Jahr", datum) FROM stdin;
\.


--
-- TOC entry 2858 (class 0 OID 16415)
-- Dependencies: 201
-- Data for Name: Wahlkreis; Type: TABLE DATA; Schema: election; Owner: postgres
--

COPY "Wahlkreis" ("Name", "Wahl", "Waehlerzahl", ungueltige_1, ungueltige_2, bundesland) FROM stdin;
\.


--
-- TOC entry 2708 (class 2606 OID 16414)
-- Name: Bundesland Bundesland_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Bundesland"
    ADD CONSTRAINT "Bundesland_pkey" PRIMARY KEY ("Kuerzel");


--
-- TOC entry 2716 (class 2606 OID 16510)
-- Name: Direktkandidatur Direktkandidatur_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Direktkandidatur"
    ADD CONSTRAINT "Direktkandidatur_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 2706 (class 2606 OID 16409)
-- Name: Kandidat Kandidat_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Kandidat"
    ADD CONSTRAINT "Kandidat_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 2712 (class 2606 OID 16500)
-- Name: Landesliste Landesliste_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landesliste"
    ADD CONSTRAINT "Landesliste_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 2714 (class 2606 OID 16472)
-- Name: Landeslistenkandidatur Landeslistenkandidatur_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landeslistenkandidatur"
    ADD CONSTRAINT "Landeslistenkandidatur_pkey" PRIMARY KEY ("Kandidat", "Landesliste");


--
-- TOC entry 2702 (class 2606 OID 16399)
-- Name: Partei Partei_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Partei"
    ADD CONSTRAINT "Partei_pkey" PRIMARY KEY (kuerzel);


--
-- TOC entry 2718 (class 2606 OID 16535)
-- Name: Stimme Stimme_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Stimme"
    ADD CONSTRAINT "Stimme_pkey" PRIMARY KEY ("ID");


--
-- TOC entry 2704 (class 2606 OID 16404)
-- Name: Wahl Wahl_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Wahl"
    ADD CONSTRAINT "Wahl_pkey" PRIMARY KEY ("Jahr");


--
-- TOC entry 2710 (class 2606 OID 16419)
-- Name: Wahlkreis Wahlkreis_pkey; Type: CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Wahlkreis"
    ADD CONSTRAINT "Wahlkreis_pkey" PRIMARY KEY ("Name");


--
-- TOC entry 2720 (class 2606 OID 16425)
-- Name: Wahlkreis fk_bundesland; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Wahlkreis"
    ADD CONSTRAINT fk_bundesland FOREIGN KEY (bundesland) REFERENCES "Bundesland"("Kuerzel");


--
-- TOC entry 2731 (class 2606 OID 16541)
-- Name: Stimme fk_direktkandidat; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Stimme"
    ADD CONSTRAINT fk_direktkandidat FOREIGN KEY ("Erststimme") REFERENCES "Direktkandidatur"("ID");


--
-- TOC entry 2724 (class 2606 OID 16473)
-- Name: Landeslistenkandidatur fk_kandidat; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landeslistenkandidatur"
    ADD CONSTRAINT fk_kandidat FOREIGN KEY ("Kandidat") REFERENCES "Kandidat"("ID");


--
-- TOC entry 2728 (class 2606 OID 16521)
-- Name: Direktkandidatur fk_kandidat; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Direktkandidatur"
    ADD CONSTRAINT fk_kandidat FOREIGN KEY ("Kandidat") REFERENCES "Kandidat"("ID");


--
-- TOC entry 2725 (class 2606 OID 16501)
-- Name: Landeslistenkandidatur fk_landesliste; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landeslistenkandidatur"
    ADD CONSTRAINT fk_landesliste FOREIGN KEY ("Landesliste") REFERENCES "Landesliste"("ID");


--
-- TOC entry 2732 (class 2606 OID 16546)
-- Name: Stimme fk_landesliste; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Stimme"
    ADD CONSTRAINT fk_landesliste FOREIGN KEY ("Zweitstimme") REFERENCES "Landesliste"("ID");


--
-- TOC entry 2719 (class 2606 OID 16420)
-- Name: Wahlkreis fk_wahl; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Wahlkreis"
    ADD CONSTRAINT fk_wahl FOREIGN KEY ("Wahl") REFERENCES "Wahl"("Jahr");


--
-- TOC entry 2726 (class 2606 OID 16511)
-- Name: Direktkandidatur fk_wahl; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Direktkandidatur"
    ADD CONSTRAINT fk_wahl FOREIGN KEY ("Wahl") REFERENCES "Wahl"("Jahr");


--
-- TOC entry 2727 (class 2606 OID 16516)
-- Name: Direktkandidatur fk_wahlkreis; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Direktkandidatur"
    ADD CONSTRAINT fk_wahlkreis FOREIGN KEY ("Wahhlkreis") REFERENCES "Wahlkreis"("Name");


--
-- TOC entry 2730 (class 2606 OID 16536)
-- Name: Stimme fk_wahlkreis; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Stimme"
    ADD CONSTRAINT fk_wahlkreis FOREIGN KEY (wahlkreis) REFERENCES "Wahlkreis"("Name");


--
-- TOC entry 2729 (class 2606 OID 16526)
-- Name: Direktkandidatur partei; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Direktkandidatur"
    ADD CONSTRAINT partei FOREIGN KEY ("Partei") REFERENCES "Partei"(kuerzel);


--
-- TOC entry 2723 (class 2606 OID 16491)
-- Name: Landesliste pk_bundesland; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landesliste"
    ADD CONSTRAINT pk_bundesland FOREIGN KEY ("Bundesland") REFERENCES "Bundesland"("Kuerzel");


--
-- TOC entry 2721 (class 2606 OID 16478)
-- Name: Landesliste pk_partei; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landesliste"
    ADD CONSTRAINT pk_partei FOREIGN KEY ("Partei") REFERENCES "Partei"(kuerzel);


--
-- TOC entry 2722 (class 2606 OID 16486)
-- Name: Landesliste pk_wahl; Type: FK CONSTRAINT; Schema: election; Owner: postgres
--

ALTER TABLE ONLY "Landesliste"
    ADD CONSTRAINT pk_wahl FOREIGN KEY ("Wahl") REFERENCES "Wahl"("Jahr");


-- Completed on 2017-11-11 22:36:38

--
-- PostgreSQL database dump complete
--

