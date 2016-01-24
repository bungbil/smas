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
   sales2_id			INT8                 not null,
   wilayah_id			INT8                 not null,
   divisi_id			INT8                 not null,
   nama_pelanggan  	  	varchar(200)         not null,
   telepon				varchar(100)   		 not null,
   alamat				varchar(500)   		 not null,
   rencana_kirim      	date				 not null,
   pengirim_id			INT8                 not null,   
   down_payment			numeric(12,2)		 not null default 0,  
   interval_kredit		INT4                 not null,
   diskon				numeric(12,2)		 not null default 0,  
   tgl_angsuran1		date				 not null,
   total				numeric(12,2)		 not null default 0,  
   grand_total			numeric(12,2)		 not null default 0,  
   kredit_per_bulan		numeric(12,2)		 not null default 0,
   piutang				numeric(12,2)		 not null default 0,
   remark				varchar(500)   		 null,
   bulan_penjualan		varchar(15)          not null,
   tahun_penjualan		varchar(10)          not null,
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
   last_update       	timestamp,          
   updated_by       	varchar(50)          null,
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

/* Barang */
/* --> Page Barang */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(202, 1, 'menuCat_Transaction', 0),
(203, 2, 'menuItem_Transaction_Penjualan', 0),
(204, 0, 'penjualanListWindow', 0),
(205, 6, 'button_PenjualanList_NewPenjualan', 0),



/* window_BarangDialog BUTTONS */
(192, 6, 'button_BarangMain_btnNew', 0),
(193, 6, 'button_BarangMain_btnEdit', 0),
(194, 6, 'button_BarangMain_btnDelete', 0),
(195, 6, 'button_BarangMain_btnSave', 0),
(196, 6, 'button_BarangMain_btnClose', 0),
/* Barang navigation buttons */
(197, 6, 'button_BarangMain_btnCancel', 0),
(198, 6, 'button_BarangMain_btnFirst', 0),
(199, 6, 'button_BarangMain_btnPrevious', 0),
(200, 6, 'button_BarangMain_btnNext', 0),
(201, 6, 'button_BarangMain_btnLast', 0);


INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(188, 1, 188, 0),
(189, 1, 189, 0),
(190, 1, 190, 0),
(191, 1, 191, 0),
(192, 1, 192, 0),
(193, 1, 193, 0),
(194, 1, 194, 0),
(195, 1, 195, 0),
(196, 1, 196, 0),
(197, 1, 197, 0),
(198, 1, 198, 0),
(199, 1, 199, 0),
(200, 1, 200, 0),
(201, 1, 201, 0);
