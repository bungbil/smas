/******************** Penjualan ********************/  
UPDATE PARAMETER SET PARAM_VALUE ='4.1' WHERE PARAM_NAME = 'APPLICATION_VERSION';

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(275, 2, 'menuItem_Report_PiutangSales', 0),
(276, 6, 'button_ReportPiutangSalesMain_btnView', 0);
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(275, 1, 275, 0),
(276, 1, 276, 0);
     

/*BELUM DIPAKE*/
INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(277, 1, 277, 0),
(278, 1, 278, 0),
(279, 1, 279, 0);         
