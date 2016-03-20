/******************** PARAMETER ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='1.2' WHERE PARAM_NAME = 'APPLICATION_VERSION';

alter table karyawan
	add column tanggal_mulai_kerja			date null,   	
   	add column tanggal_berhenti_kerja			date null;
   	
INSERT INTO JOB_TYPE (JOB_TYPE_ID,NAMA_JOB_TYPE,VERSION) values
(9,'Kasir',1);


INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(202, 6, 'button_KaryawanDetail_DownloadProfile', 0),
(203, 6, 'button_KaryawanDetail_DownloadKtp', 0);


INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(202, 1, 202, 0),
(203, 1, 203, 0);
