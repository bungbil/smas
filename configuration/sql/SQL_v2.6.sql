/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='2.6' WHERE PARAM_NAME = 'APPLICATION_VERSION';

CREATE UNIQUE INDEX ix_search_piutang on piutang (
tgl_jatuh_tempo,kolektor_id
);


/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(274, 1, 274, 0),
(275, 1, 275, 0),
(276, 1, 276, 0),
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
   