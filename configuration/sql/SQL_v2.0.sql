/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.0' WHERE PARAM_NAME = 'APPLICATION_VERSION';


/*==============================================================*/
/* Table: Penjualan                                              */
/*==============================================================*/
DROP TABLE IF EXISTS penjualan cascade;
DROP SEQUENCE IF EXISTS penjualan_seq;

CREATE TABLE penjualan (
   penjualan_id         INT8                 not null,   
   no_faktur       		varchar(50)          not null,      
   tgl_penjualan      	date				 not null,   
   metode_pembayaran	varchar(100)         not null,
   status_id			INT8                 not null,
   sales1_id			INT8                 not null,
   sales2_id			INT8                 null,
   wilayah_id			INT8                 not null,
   divisi_id			INT8                 null,
   nama_pelanggan  	  	varchar(200)         not null,
   telepon				varchar(100)   		 not null,
   alamat				varchar(500)   		 not null,
   rencana_kirim      	date				 not null,
   pengirim_id			INT8                 not null,   
   down_payment			numeric(12,2)		 not null default 0,  
   interval_kredit		INT4                 not null,
   diskon				numeric(12,2)		 not null default 0,  
   tgl_angsuran2		date				 null,
   total				numeric(12,2)		 not null default 0,  
   grand_total			numeric(12,2)		 not null default 0,  
   kredit_per_bulan		numeric(12,2)		 not null default 0,
   piutang				numeric(12,2)		 not null default 0,
   remark				varchar(500)   		 null,
   mandiri				varchar(20)          null,
   no_order_sheet		varchar(50)          not null,
   need_approval		boolean				 not null,
   reason_approval		varchar(254)   		 null,
   approved_remark		varchar(254)   		 null,
   approved_by			varchar(50)	 		 null,
   last_update       	timestamp,          
   updated_by       	varchar(50)          null,
   version              int4                 not null default 0,
   constraint pk_penjualan primary key (penjualan_id)
)
without oids;
ALTER TABLE penjualan owner to smas;

CREATE SEQUENCE penjualan_seq START 100;
ALTER SEQUENCE penjualan_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_penjualan_id on penjualan using btree (
penjualan_id
);

CREATE UNIQUE INDEX ix_no_faktur on penjualan (
no_faktur
);


/*==============================================================*/
/* Table: Penjualan Detail                                             */
/*==============================================================*/
DROP TABLE IF EXISTS penjualan_detail cascade;
DROP SEQUENCE IF EXISTS penjualan_detail_seq;

CREATE TABLE penjualan_detail (
   penjualan_detail_id  INT8                 not null,
   penjualan_id         INT8                 not null,   
   barang_id         	INT8                 not null,
   qty					INT4                 not null default 0,
   harga				numeric(12,2)		 not null default 0,
   total				numeric(12,2)		 not null default 0,
   version              int4                 not null default 0,
   constraint pk_penjualan_detail primary key (penjualan_detail_id)
)
without oids;
ALTER TABLE penjualan_detail owner to smas;

CREATE SEQUENCE penjualan_detail_seq START 100;
ALTER SEQUENCE penjualan_detail_seq OWNER TO smas;

CREATE UNIQUE INDEX ix_penjualan_detail_id on penjualan_detail using btree (
penjualan_detail_id
);

alter table penjualan_detail
   add constraint ref_penjualan_to_penjualandetail foreign key (penjualan_id)
      references penjualan (penjualan_id)
      on delete cascade on update cascade;


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

/* Penjualan */
/* --> Page Penjualan */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(204, 1, 'menuCat_Transaction', 0),
(205, 2, 'menuItem_Transaction_Penjualan', 0),
(206, 0, 'windowPenjualanList', 0),
(207, 0, 'windowPenjualanDetail', 0),
/* window_PenjualanList Buttons*/
(208, 6, 'button_PenjualanList_Search', 0),
/* window_PenjualanDialog BUTTONS */
(209, 6, 'button_PenjualanMain_btnNew', 0),
(210, 6, 'button_PenjualanMain_btnEdit', 0),
(211, 6, 'button_PenjualanMain_btnDelete', 0),
(212, 6, 'button_PenjualanMain_btnSave', 0),
(213, 6, 'button_PenjualanMain_btnClose', 0),
/* Penjualan navigation buttons */
(214, 6, 'button_PenjualanMain_btnCancel', 0),
(215, 6, 'button_PenjualanMain_btnFirst', 0),
(216, 6, 'button_PenjualanMain_btnPrevious', 0),
(217, 6, 'button_PenjualanMain_btnNext', 0),
(218, 6, 'button_PenjualanMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(204, 1, 204, 0),
(205, 1, 205, 0),
(206, 1, 206, 0),
(207, 1, 207, 0),
(208, 1, 208, 0),
(209, 1, 209, 0),
(210, 1, 210, 0),
(211, 1, 211, 0),
(212, 1, 212, 0),
(213, 1, 213, 0),
(214, 1, 214, 0),
(215, 1, 215, 0),
(216, 1, 216, 0),
(217, 1, 217, 0),
(218, 1, 218, 0);
