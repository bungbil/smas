/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.5' WHERE PARAM_NAME = 'APPLICATION_VERSION';


alter table barang      
   add column bonus		boolean				 default false;
   
alter table penjualan_detail
   add column bonus		boolean				 default false;

   