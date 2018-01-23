/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='3.6' WHERE PARAM_NAME = 'APPLICATION_VERSION';

DROP INDEX ix_search_piutang;
CREATE INDEX ix_search_piutang on piutang (
tgl_jatuh_tempo,kolektor_id
);

INSERT INTO STATUS (STATUS_ID,DESKRIPSI_STATUS,VERSION) values
(9,'UNDUR',1);

alter table piutang
   add column masalah 				boolean				 default false,
   add column nama_pelanggan  	  	varchar(200)         null,
   add column telepon				varchar(100)   		 null,
   add column alamat				varchar(500)   		 null,
   add column alamat2				varchar(255)   		 null,
   add column alamat3				varchar(255)   		 null;

   
   
/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
