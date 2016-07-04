/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.1' WHERE PARAM_NAME = 'APPLICATION_VERSION';

alter table penjualan_detail
	add column down_payment			numeric(12,2)		 null default 0;
	




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
(232, 2, 'menuItem_Report_KomisiPenjualan', 0),
(233, 6, 'button_ReportKomisiPenjualanMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(232, 1, 232, 0),
(233, 1, 233, 0);

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
   tgl_pembayaran		date				 not null,
   status				varchar(50)          not null,
   nilai_tagihan		numeric(12,2)		 not null default 0,
   pembayaran			numeric(12,2)		 not null default 0,
   piutang				numeric(12,2)		 not null default 0,
   tgl_jatuh_tempo		date				 not null,
   kolektor_id			INT8                 not null,
   keterangan			varchar(300)          null,
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

