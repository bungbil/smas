/******************** PARAMETER ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='1.2' WHERE PARAM_NAME = 'APPLICATION_VERSION';

alter table karyawan
	add column tanggal_mulai_kerja			date null,   	
   	add column tanggal_berhenti_kerja			date null;