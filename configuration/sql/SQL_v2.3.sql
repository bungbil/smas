/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.3' WHERE PARAM_NAME = 'APPLICATION_VERSION';


/*==============================================================*/
/* Table: Karyawan_images                                       */
/*==============================================================*/
DROP TABLE IF EXISTS karyawan_images cascade;
DROP SEQUENCE IF EXISTS karyawan_images_seq;

CREATE TABLE karyawan_images (
   karyawan_images_id   INT8                not null,   
   karyawan_id		    INT8                not null, 
   profile_image      	bytea        		null,
   ktp_image       		bytea        		null,
   version              int4                not null default 0,
   constraint pk_karyawan_images primary key (karyawan_images_id)
)
without oids;
ALTER TABLE karyawan_images owner to smas;

--CREATE SEQUENCE karyawan_images_seq START 100; --value sama seperti karyawan_seq saat sekarang
ALTER SEQUENCE karyawan_images_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_karyawan_images_id on karyawan_images using btree (
karyawan_images_id
);

--script migrasi images ke table karyawan_images
INSERT INTO karyawan_images (karyawan_images_id,karyawan_id,profile_image,ktp_image,version)
SELECT karyawan_id,karyawan_id,profile_image,ktp_image,'0' FROM karyawan;

--backup table karyawan
CREATE TABLE karyawan_backup AS
  TABLE karyawan;

--delete column image di karyawan
alter table karyawan   
   drop column profile_image,
   drop column ktp_image;

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(272, 2, 'menuItem_Transaction_CetakPembayaranDiskon', 0),
(273, 6, 'button_CetakPembayaranDiskonMain_btnCetak', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(272, 1, 272, 0),
(273, 1, 273, 0);



/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(274, 1, 274, 0),
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
   