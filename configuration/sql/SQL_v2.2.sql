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
   add column diskon			numeric(12,0)		 null default 0;
   
   
   
   