/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.1' WHERE PARAM_NAME = 'APPLICATION_VERSION';

alter table penjualan_detail
	add column down_payment			numeric(12,2)		 null default 0,
	add column komisi_sales			numeric(12,2)		 null default 0,
	add column tabungan_sales		numeric(12,2)		 null default 0,
	add column opr_divisi			numeric(12,2)		 null default 0,
	add column or_divisi			numeric(12,2)		 null default 0;

alter table penjualan
	add column kolektor_id			INT8                 null;
	
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(227, 2, 'menuItem_Transaction_CetakPenjualan', 0),
(228, 6, 'button_CetakPenjualanMain_btnCetakFaktur', 0),
(229, 6, 'button_CetakPenjualanMain_btnCetakKuitansiA2', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(227, 1, 227, 0),
(228, 1, 228, 0),
(229, 1, 229, 0);


INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(230, 2, 'menuItem_Report_SummaryPenjualan', 0),
(231, 6, 'button_ReportSummaryPenjualanMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(230, 1, 230, 0),
(231, 1, 231, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(232, 2, 'menuItem_Report_PerhitunganKomisi', 0),
(233, 6, 'button_ReportPerhitunganKomisiMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(232, 1, 232, 0),
(233, 1, 233, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(234, 2, 'menuItem_Report_Komisi', 0),
(235, 6, 'button_ReportKomisiMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(234, 1, 234, 0),
(235, 1, 235, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(236, 2, 'menuItem_Report_Tabungan', 0),
(237, 6, 'button_ReportTabunganMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(236, 1, 236, 0),
(237, 1, 237, 0);

TRUNCATE TABLE STATUS;
INSERT INTO STATUS (STATUS_ID,DESKRIPSI_STATUS,VERSION) values
(1,'BUTUH_APPROVAL',1),
(2,'LUNAS',1),
(3,'KREDIT_BERJALAN',1),
(4,'PIUTANG_BARU',1),
(5,'DISKON',1),
(6,'TARIK_BARANG',1),
(7,'MASALAH',1);





/*==============================================================*/
/* Table: piutang                                             */
/*==============================================================*/
DROP TABLE IF EXISTS piutang cascade;
DROP SEQUENCE IF EXISTS piutang_seq;

CREATE TABLE piutang (
   piutang_id  			INT8                 not null,
   penjualan_id         INT8                 not null,
   no_kuitansi			varchar(50)          not null, 
   pembayaran_ke		INT4                 not null,
   tgl_pembayaran		date				 null,
   status_id			INT8		         not null,
   nilai_tagihan		numeric(12,2)		 not null default 0,
   pembayaran			numeric(12,2)		 null default 0,   
   tgl_jatuh_tempo		date				 not null,
   kolektor_id			INT8                 null,
   keterangan			varchar(300)         null,
   last_update       	timestamp,          
   updated_by       	varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_piutang primary key (piutang_id)
)
without oids;
ALTER TABLE piutang owner to smas;

CREATE SEQUENCE piutang_seq START 100;
ALTER SEQUENCE piutang_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_piutang_id on piutang using btree (
piutang_id
);

CREATE UNIQUE INDEX ix_no_kuitansi on piutang (
no_kuitansi
);

alter table piutang
   add constraint ref_penjualan_to_piutang foreign key (penjualan_id)
      references penjualan (penjualan_id)
      on delete cascade on update cascade;

