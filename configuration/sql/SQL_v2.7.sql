/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.7' WHERE PARAM_NAME = 'APPLICATION_VERSION';

/*==============================================================*/
/* Table: Mandiri                                               */
/*==============================================================*/

DROP TABLE IF EXISTS mandiri cascade;
DROP SEQUENCE IF EXISTS mandiri_seq;

CREATE TABLE mandiri (
   mandiri_id           INT8                not null,
   kode_mandiri         varchar(50)         not null,
   deskripsi_mandiri    varchar(254)        null,   
   last_update       		  timestamp,          
   updated_by       		  varchar(50)         null,
   version                    int4                not null default 0,
   constraint pk_mandiri primary key (mandiri_id)
)
without oids;
ALTER TABLE mandiri owner to smas;

CREATE SEQUENCE mandiri_seq START 100;
ALTER SEQUENCE mandiri_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_mandiri_id on mandiri using btree (
mandiri_id
);
CREATE UNIQUE INDEX ix_kode_mandiri on mandiri (
kode_mandiri
);


/* MANDIRI */
 --> Page Mandiri 
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(274, 2, 'menuItem_Master_Mandiri', 0),
(131, 0, 'windowMandiriList', 0),
(132, 0, 'windowMandiriDetail', 0),*/
/* window_MandiriList Buttons*/
(133, 6, 'button_MandiriList_SearchName', 0),
/* window_MandiriDialog BUTTONS */
(134, 6, 'button_MandiriMain_btnNew', 0),
(135, 6, 'button_MandiriMain_btnEdit', 0),
(136, 6, 'button_MandiriMain_btnDelete', 0),
(137, 6, 'button_MandiriMain_btnSave', 0),
(138, 6, 'button_MandiriMain_btnClose', 0),
/* Mandiri navigation buttons */
(139, 6, 'button_MandiriMain_btnCancel', 0),
(140, 6, 'button_MandiriMain_btnFirst', 0),
(141, 6, 'button_MandiriMain_btnPrevious', 0),
(142, 6, 'button_MandiriMain_btnNext', 0),
(143, 6, 'button_MandiriMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(131, 1, 131, 0),
(132, 1, 132, 0),
(133, 1, 133, 0),
(134, 1, 134, 0),
(135, 1, 135, 0),
(136, 1, 136, 0),
(137, 1, 137, 0),
(138, 1, 138, 0),
(139, 1, 139, 0),
(140, 1, 140, 0),
(141, 1, 141, 0),
(142, 1, 142, 0),
(143, 1, 143, 0),
(274, 1, 274, 0);

alter table penjualan
   add column mandiri_id			INT8                 null,  
   add column alamat2				varchar(255)   		 null,
   add column alamat3				varchar(255)   		 null;

/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
