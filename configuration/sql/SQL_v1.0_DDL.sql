
CREATE OR REPLACE FUNCTION public.create_plpgsql_language ()
        RETURNS TEXT
        AS $$
            CREATE LANGUAGE plpgsql;
            SELECT 'language plpgsql created'::TEXT;
        $$
LANGUAGE 'sql';

SELECT CASE WHEN
              (SELECT true::BOOLEAN
                 FROM pg_language
                WHERE lanname='plpgsql')
            THEN
              (SELECT 'language already installed'::TEXT)
            ELSE
              (SELECT public.create_plpgsql_language())
            END;

DROP FUNCTION public.create_plpgsql_language ();

/*==============================================================*/
/* Table: sec_group                                             */
/*==============================================================*/
DROP TABLE IF EXISTS sec_group cascade;
DROP SEQUENCE IF EXISTS sec_group_seq;

CREATE SEQUENCE sec_group_seq START 100;
ALTER SEQUENCE sec_group_seq OWNER TO smas;

create table sec_group (
   grp_id               INT8                 not null,
   grp_shortdescription VARCHAR(30)          not null,
   grp_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUP primary key (grp_id)
)
without oids;

alter table sec_group owner to smas;

create unique index idx_grp_id on sec_group (
grp_id
);

create unique index idx_grp_shortdescription on sec_group (
grp_shortdescription
);

/*==============================================================*/
/* Table: sec_groupright                                        */
/*==============================================================*/
DROP TABLE IF EXISTS sec_groupright cascade;
DROP SEQUENCE IF EXISTS sec_groupright_seq;

CREATE SEQUENCE sec_groupright_seq START 100;
ALTER SEQUENCE sec_groupright_seq OWNER TO smas;

create table sec_groupright (
   gri_id               INT8                 not null,
   grp_id               INT8                 not null,
   rig_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_GROUPRIGHT primary key (gri_id)
)
without oids;

alter table sec_groupright owner to smas;

create unique index idx_gri_id on sec_groupright (
gri_id
);

create unique index idx_gri_grprig on sec_groupright (
grp_id,
rig_id
);

/*==============================================================*/
/* Table: sec_loginlog                                          */
/*==============================================================*/
create table sec_loginlog (
   lgl_id               INT8                 not null,
   i2c_id               INT8                 null,
   lgl_loginname        VARCHAR(50)          not null,
   lgl_logtime          TIMESTAMP            not null,
   lgl_ip               VARCHAR(19)          null,
   lgl_browsertype      VARCHAR(40)          null,
   lgl_status_id        INT4                 not null,
   lgl_sessionid        VARCHAR(50)          null,
   version              INT4                 not null default 0,
   constraint PK_SEC_LOGINLOG primary key (lgl_id)
)
without oids;

alter table sec_loginlog owner to smas;

create unique index idx_lgl_id on sec_loginlog (
lgl_id
);

create  index idx_lgl_logtime on sec_loginlog (
lgl_logtime
);

create  index idx_lgl_i2c_id on sec_loginlog (
i2c_id
);

/*==============================================================*/
/* Table: sec_right                                             */
/*==============================================================*/
DROP TABLE IF EXISTS sec_right cascade;
DROP SEQUENCE IF EXISTS sec_right_seq;

CREATE SEQUENCE sec_right_seq START 100;
ALTER SEQUENCE sec_right_seq OWNER TO smas;

create table sec_right (
   rig_id               INT8                 not null,
   rig_type             INT4                 null default 1,
   rig_name             VARCHAR(50)          not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_RIGHT primary key (rig_id)
)
without oids;

alter table sec_right owner to smas;

create unique index idx_rig_id on sec_right (
rig_id
);

create  index idx_rig_type on sec_right (
rig_type
);

create unique index idx_rig_name on sec_right (
rig_name
);

/*==============================================================*/
/* Table: sec_role                                              */
/*==============================================================*/
DROP TABLE IF EXISTS sec_role cascade;
DROP SEQUENCE IF EXISTS sec_role_seq;

CREATE SEQUENCE sec_role_seq START 100;
ALTER SEQUENCE sec_role_seq OWNER TO smas;

create table sec_role (
   rol_id               INT8                 not null,
   rol_shortdescription VARCHAR(30)          not null,
   rol_longdescription  VARCHAR(1000)        null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLE primary key (rol_id)
)
without oids;

alter table sec_role owner to smas;

create unique index idx_role_id on sec_role (
rol_id
);

create unique index idx_role_shortdescription on sec_role (
rol_shortdescription
);

/*==============================================================*/
/* Table: sec_rolegroup                                         */
/*==============================================================*/
DROP TABLE IF EXISTS sec_rolegroup cascade;
DROP SEQUENCE IF EXISTS sec_rolegroup_seq;

CREATE SEQUENCE sec_rolegroup_seq START 100;
ALTER SEQUENCE sec_rolegroup_seq OWNER TO smas;

create table sec_rolegroup (
   rlg_id               INT8                 not null,
   grp_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_ROLEGROUP primary key (rlg_id)
)
without oids;

alter table sec_rolegroup owner to smas;

create unique index idx_rlg_id on sec_rolegroup (
rlg_id
);

create unique index idx_rlg_grprol on sec_rolegroup (
grp_id,
rol_id
);

/*==============================================================*/
/* Table: sec_user                                              */
/*==============================================================*/
DROP TABLE IF EXISTS sec_user cascade;
DROP SEQUENCE IF EXISTS sec_user_seq;

CREATE SEQUENCE sec_user_seq START 100;
ALTER SEQUENCE sec_user_seq OWNER TO smas;

create table sec_user (
   usr_id               INT8                 not null,
   usr_loginname        VARCHAR(50)          not null,
   usr_password         VARCHAR(50)          not null,
   usr_lastname         VARCHAR(50)          null,
   usr_firstname        VARCHAR(50)          null,
   usr_email            VARCHAR(200)         null,
   usr_locale           VARCHAR(5)           null,
   usr_enabled          BOOL                 not null default FALSE,
   usr_accountNonExpired BOOL                 not null default TRUE,
   usr_credentialsNonExpired BOOL                 not null default TRUE,
   usr_accountNonLocked BOOL                 not null default TRUE,
   usr_token            VARCHAR(20)          null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USER primary key (usr_id)
)
without oids;

alter table sec_user owner to smas;

create unique index idx_usr_id on sec_user (
usr_id
);

create unique index idx_usr_loginname on sec_user (
usr_loginname
);

/*==============================================================*/
/* Table: sec_userrole                                          */
/*==============================================================*/
DROP TABLE IF EXISTS sec_userrole cascade;
DROP SEQUENCE IF EXISTS sec_userrole_seq;

CREATE SEQUENCE sec_userrole_seq START 100;
ALTER SEQUENCE sec_userrole_seq OWNER TO smas;

create table sec_userrole (
   urr_id               INT8                 not null,
   usr_id               INT8                 not null,
   rol_id               INT8                 not null,
   version              INT4                 not null default 0,
   constraint PK_SEC_USERROLE primary key (urr_id)
)
without oids;
alter table sec_userrole owner to smas;

create unique index idx_urr_id on sec_userrole (
urr_id
);

create unique index idx_urr_usrrol on sec_userrole (
usr_id,
rol_id
);


/*==============================================================*/
/* Table: Parameter                                             */
/*==============================================================*/
DROP TABLE IF EXISTS parameter cascade;
DROP SEQUENCE IF EXISTS parameter_seq;

CREATE TABLE parameter (
   param_id             INT8                 not null,
   param_name           varchar(50)          not null,
   param_value       	varchar(254)          null,
   description          varchar(254)          null,
   last_update       	timestamp,          
   updated_by       	varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_parameter primary key (param_id)
)
without oids;
ALTER TABLE parameter owner to smas;

CREATE SEQUENCE parameter_seq START 100;
ALTER SEQUENCE parameter_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_param_id on parameter using btree (
param_id
);
CREATE UNIQUE INDEX ix_param_name on parameter (
param_name
);


/*==============================================================*/
/* Table: Company Profile                                       */
/*==============================================================*/
DROP TABLE IF EXISTS company_profile cascade;
DROP SEQUENCE IF EXISTS company_profile_seq;

CREATE TABLE company_profile (
   profile_id       INT8                 not null,
   company_name     varchar(100)         not null,
   address       	varchar(254)         null,   
   phone            varchar(50)          null,
   email            varchar(50)          null,
   last_update      timestamp,          
   updated_by       varchar(50)          null,
   version          int4             not null default 0,
   constraint pk_company_profile primary key (profile_id)
)
without oids;
ALTER TABLE company_profile owner to smas;

CREATE SEQUENCE company_profile_seq START 100;
ALTER SEQUENCE company_profile_seq OWNER TO smas;


/*==============================================================*/
/* Table: Wilayah                                             */
/*==============================================================*/
DROP TABLE IF EXISTS wilayah cascade;
DROP SEQUENCE IF EXISTS wilayah_seq;

CREATE TABLE wilayah (
   wilayah_id           INT8                 not null,
   kode_wilayah         varchar(50)          not null,
   nama_wilayah       	varchar(50)          null,
   status               varchar(50)          null,
   last_update       	timestamp,          
   updated_by       	varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_wilayah primary key (wilayah_id)
)
without oids;
ALTER TABLE wilayah owner to smas;

CREATE SEQUENCE wilayah_seq START 100;
ALTER SEQUENCE wilayah_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_wilayah_id on wilayah using btree (
wilayah_id
);
CREATE UNIQUE INDEX ix_kode_wilayah on wilayah (
kode_wilayah
);


/*==============================================================*/
/* Table: Satuan Barang                                         */
/*==============================================================*/
DROP TABLE IF EXISTS satuan_barang cascade;
DROP SEQUENCE IF EXISTS satuan_barang_seq;

CREATE TABLE satuan_barang (
   satuan_barang_id           INT8                not null,
   kode_satuan_barang         varchar(50)         not null,
   deskripsi_satuan_barang    varchar(254)        null,
   satuan_standar_barang      BOOL      	      not null default FALSE,
   nilai_standar_satuan       INT8  	          not null default 0,
   nilai_konversi             INT8	 			  not null default 0,
   last_update       		  timestamp,          
   updated_by       		  varchar(50)         null,
   version                    int4                not null default 0,
   constraint pk_satuan_barang primary key (satuan_barang_id)
)
without oids;
ALTER TABLE satuan_barang owner to smas;

CREATE SEQUENCE satuan_barang_seq START 100;
ALTER SEQUENCE satuan_barang_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_satuan_barang_id on satuan_barang using btree (
satuan_barang_id
);
CREATE UNIQUE INDEX ix_kode_satuan_barang on satuan_barang (
kode_satuan_barang
);

