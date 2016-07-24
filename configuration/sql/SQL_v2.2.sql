/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.2' WHERE PARAM_NAME = 'APPLICATION_VERSION';
/* manual ubah di database langsung
alter table penjualan_detail
	add column down_payment			numeric(12,0)		 null default 0,
	add column komisi_sales			numeric(12,0)		 null default 0,
	add column tabungan_sales		numeric(12,0)		 null default 0,
	add column opr_divisi			numeric(12,0)		 null default 0,
	add column or_divisi			numeric(12,0)		 null default 0,
	add column harga				numeric(12,0)		 not null default 0,
    add column total				numeric(12,0)		 not null default 0;
    
alter table piutang
   add column nilai_tagihan			numeric(12,0)		 not null default 0,
   add column pembayaran			numeric(12,0)		 null default 0;
   
alter table penjualan
   add column down_payment			numeric(12,0)		 null default 0,  
   add column diskon				numeric(12,0)		 null default 0,  
   add column total					numeric(12,0)		 not null default 0,  
   add column grand_total			numeric(12,0)		 not null default 0,  
   add column kredit_per_bulan		numeric(12,0)		 null default 0,
   add column piutang				numeric(12,0)		 not null default 0;
   
  */ 

alter table piutang   
   add column full_payment		boolean				 default false,
   add column aktif				boolean				 default false,
   add column tgl_bawa     		date				 null,   
   add column diskon			numeric(12,0)		 null default 0,
   add column status_final_id			INT8,
   add column kekurangan_bayar			numeric(12,0)		 null default 0;
   
alter table karyawan   
   add column jenis_kelamin			varchar(20)		 null ;
   



INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(266, 2, 'menuItem_Transaction_CetakTandaTerimaKwitansi', 0),
(267, 6, 'button_CetakTandaTerimaKwitansiMain_btnCetak', 0),
(268, 2, 'menuItem_Transaction_CetakPembayaranKwitansi', 0),
(269, 6, 'button_CetakPembayaranKwitansiMain_btnCetak', 0),
(270, 2, 'menuItem_Transaction_CetakSisaKwitansi', 0),
(271, 6, 'button_CetakSisaKwitansiMain_btnCetak', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(266, 1, 266, 0),
(267, 1, 267, 0),
(268, 1, 268, 0),
(269, 1, 269, 0),
(270, 1, 270, 0),
(271, 1, 271, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(280, 2, 'menuItem_Transaction_InputTglBawa', 0),
(281, 6, 'button_InputTglBawaMain_btnSave', 0),
(282, 6, 'button_InputTglBawaMain_btnSearch', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(280, 1, 280, 0),
(281, 1, 281, 0),
(282, 1, 282, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(283, 2, 'menuItem_Transaction_PenerimaanPembayaran', 0),
(284, 6, 'button_PenerimaanPembayaranMain_btnSave', 0),
(285, 6, 'button_PenerimaanPembayaranMain_btnSearch', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(283, 1, 283, 0),
(284, 1, 284, 0),
(285, 1, 285, 0);

INSERT INTO STATUS (STATUS_ID,DESKRIPSI_STATUS,VERSION) values
(8,'FINAL',1);

/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(272, 1, 272, 0),
(273, 1, 273, 0),
(274, 1, 274, 0),
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
   