CREATE SEQUENCE IF NOT EXISTS public.user_info_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;


CREATE TABLE IF NOT EXISTS public.user_info
(
    active_status boolean,
    create_date timestamp(6) without time zone,
    created_by bigint,
    id bigint NOT NULL DEFAULT nextval('user_info_id_seq'::regclass),
    update_date timestamp(6) without time zone,
    updated_by bigint,
    code character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    full_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    roles character varying(255) COLLATE pg_catalog."default",
    user_name character varying(255) COLLATE pg_catalog."default",
    user_state character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT user_info_pkey PRIMARY KEY (id),
    CONSTRAINT user_info_code_key UNIQUE (code),
    CONSTRAINT user_info_email_key UNIQUE (email),
    CONSTRAINT user_info_user_name_key UNIQUE (user_name),
    CONSTRAINT user_info_user_state_check CHECK (user_state::text = ANY (ARRAY['PENDING'::character varying, 'ACTIVE'::character varying, 'INACTIVE'::character varying, 'BLOCKED'::character varying, 'ENABLE'::character varying, 'DISABLE'::character varying]::text[]))
);