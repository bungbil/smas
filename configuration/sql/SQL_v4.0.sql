/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='4.0' WHERE PARAM_NAME = 'APPLICATION_VERSION';

alter table piutang
   add column tukar_old_barang_id 	INT8,   
   add column tukar_old_harga		numeric(12,0);   
   
alter table penjualan
   add column tukar_barang 	boolean default false;
     

/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
