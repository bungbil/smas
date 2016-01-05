/******************** PARAMETER ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='1.1' WHERE PARAM_NAME = 'APPLICATION_VERSION';

/******************** JOB TYPE ********************/  
INSERT INTO JOB_TYPE (JOB_TYPE_ID,NAMA_JOB_TYPE,VERSION) values
(8,'Auditor',1);


