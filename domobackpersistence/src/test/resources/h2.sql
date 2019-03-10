CREATE SEQUENCE com_chauff_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;



CREATE TABLE com_chauff (
    id integer DEFAULT com_chauff_seq.nextval NOT NULL primary key,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    onoff boolean NOT NULL,
    done boolean DEFAULT false NOT NULL
);


CREATE SEQUENCE edfindex_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;



CREATE TABLE edfindex (
    id integer DEFAULT edfindex_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value integer NOT NULL,
    type integer NOT NULL
);


CREATE SEQUENCE hygro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;




CREATE TABLE hygro (
    id integer DEFAULT hygro_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value real NOT NULL
);


CREATE SEQUENCE intensity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;


CREATE TABLE intensity (
    id integer DEFAULT intensity_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value integer NOT NULL
);



CREATE SEQUENCE pression_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;


CREATE TABLE pression (
    id integer DEFAULT pression_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    valueabs real,
    valuerel real
);



CREATE SEQUENCE temp_chauff_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;


CREATE TABLE temp_chauff (
    id integer DEFAULT temp_chauff_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    start_hour time without time zone,
    end_hour time without time zone,
    temp smallint NOT NULL
);



CREATE SEQUENCE temperature_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 2147483648
    CACHE 1;


CREATE TABLE temperature (
    id integer DEFAULT temperature_seq.nextval NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value real NOT NULL,
    type integer DEFAULT 1 NOT NULL
);

CREATE SEQUENCE mode_chauff_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483648
  START 1
  CACHE 1;

CREATE TABLE mode_chauff
(
  id integer NOT NULL DEFAULT mode_chauff_seq.nextval NOT NULL,
  date timestamp without time zone NOT NULL DEFAULT now(),
  hourmode boolean NOT NULL
);

CREATE SEQUENCE error_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 2147483648
  START 1
  CACHE 1;

CREATE TABLE serial_event
(
    id integer NOT NULL DEFAULT error_seq.nextval NOT NULL,
    date timestamp without time zone NOT NULL DEFAULT now(),
    error_type varchar NOT NULL DEFAULT '0',
    success boolean NOT NULL DEFAULT false
);
