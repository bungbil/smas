/******************** PARAMETER ********************/  
INSERT INTO PARAMETER (PARAM_ID,PARAM_NAME,PARAM_VALUE,DESCRIPTION,VERSION) values
(1,'APPLICATION_VERSION','1.0','Application Version',1);

/******************** COMPANY PROFILE ********************/  
INSERT INTO COMPANY_PROFILE (PROFILE_ID,COMPANY_NAME,ADDRESS,PHONE,EMAIL,VERSION) values
(1,'SUMBER MAS','jl. abc','021-12324342','sumbermas@gmail.com',1);

/******************** WILAYAH ********************/  
INSERT INTO WILAYAH (WILAYAH_ID,KODE_WILAYAH,NAMA_WILAYAH,STATUS,VERSION) values
(1,'W01','JAKARTA','PUSAT',1);

/******************** JOB TYPE ********************/  
INSERT INTO JOB_TYPE (JOB_TYPE_ID,NAMA_JOB_TYPE,VERSION) values
(1,'Supervisor',1),
(2,'Divisi',1),
(3,'Leader',1),
(4,'Sales',1),
(5,'Sopir',1),
(6,'Kolektor',1),
(7,'Staf',1);

/******************** Security: USERS ********************/  
INSERT INTO SEC_USER (USR_ID, USR_LOGINNAME, USR_PASSWORD, USR_LASTNAME, USR_FIRSTNAME, USR_EMAIL, USR_LOCALE, USR_ENABLED, USR_ACCOUNTNONEXPIRED, USR_CREDENTIALSNONEXPIRED, USR_ACCOUNTNONLOCKED, USR_TOKEN,  VERSION) values 
(1, 'admin', 'admin', 'Admin', 'Super', 'admin@super.admin', NULL, true, true, true, true, null, 0);

/******************** Security: ROLES ********************/  
INSERT INTO SEC_ROLE (ROL_ID, ROL_SHORTDESCRIPTION, ROL_LONGDESCRIPTION, VERSION) values
(1,'ROLE SUPER ADMIN','Super Administrator Role', 0);

/* Admin Usr-ID: 1 */
INSERT INTO SEC_USERROLE (URR_ID, USR_ID, ROL_ID, VERSION) values
(1, 1, 1, 0);

/******************** Security: SEC_GROUPS ********************/  
INSERT INTO SEC_GROUP (GRP_ID, GRP_SHORTDESCRIPTION, GRP_LONGDESCRIPTION, VERSION) values
(1, 'Group Super Admin', 'Super Admin Group have all rights', 0);

/******************** Security: SEC_ROLE-GROUPS ********************/  
INSERT INTO SEC_ROLEGROUP (RLG_ID, GRP_ID, ROL_ID, VERSION) values 
(1, 1, 1, 0);

/******************** Security: SEC_RIGHTS ********************/  
/* Pages = Type(0) */
/* Menu Category = Type(1) */
/* Menu Item Category = Type(2) */
/* Method/Event = Type(3) */

/* Tabs = Type(5) */
/* Components = Type(6) */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(1, 1, 'menuCat_Administration', 0),
(2, 2, 'menuItem_Administration_Users', 0),
(3, 2, 'menuItem_Administration_UserRoles', 0),
(4, 2, 'menuItem_Administration_Roles', 0),
(5, 2, 'menuItem_Administration_RoleGroups', 0),
(6, 2, 'menuItem_Administration_Groups', 0),
(7, 2, 'menuItem_Administration_GroupRights', 0),
(8, 2, 'menuItem_Administration_Rights', 0),
(9, 1, 'menuCat_UserRights', 0),
(10, 2, 'menuItem_Administration_LoginsLog', 0),


/* Pages = Type(0) */
/* --> Page Admin - Users */
(11, 0, 'page_Admin_UserList', 0),
(12, 0, 'page_Admin_UserDialog', 0),
/* --> Page Admin - UserRoles */
(13, 0, 'page_Security_UserRolesList', 0),
(14, 0, 'page_Security_RolesList', 0),
/* --> Page Admin - Roles */
(15, 0, 'page_Security_RolesDialog', 0),
/* --> Page Admin - RoleGroups */
(16, 0, 'page_Security_RoleGroupsList', 0),
/* --> Page Admin - Groups */
(17, 0, 'page_Security_GroupsList', 0),
(18, 0, 'page_Security_GroupsDialog', 0),
/* --> Page Admin - GroupRights */
(19, 0, 'page_Security_GroupRightsList', 0),
/* --> Page Admin - Rights */
(20, 0, 'page_Security_RightsList', 0),
(21, 0, 'page_Security_RightsDialog', 0),
/* --> Page Login Log */
(22, 0, 'page_Admin_LoginLogList', 0),

/* USERS */
/* --> userListWindow */
(23, 0, 'userListWindow', 0),
/* --> userListWindow BUTTONS*/
(25, 6, 'button_UserList_NewUser', 0),
(27, 6, 'button_UserList_SearchLoginname', 0),
(28, 6, 'button_UserList_SearchLastname', 0),
(29, 6, 'button_UserList_SearchEmail', 0),
(30, 6, 'checkbox_UserList_ShowAll', 0),
/* --> Mehode onDoubleClick Listbox */
(31, 3, 'UserList_listBoxUser.onDoubleClick', 0),
/* --> userDialogWindow */
(32, 0, 'userDialogWindow', 0),
/* --> userDialogWindow BUTTONS*/
(34, 6, 'button_UserDialog_btnNew', 0),
(35, 6, 'button_UserDialog_btnEdit', 0),
(36, 6, 'button_UserDialog_btnDelete', 0),
(37, 6, 'button_UserDialog_btnSave', 0),
(38, 6, 'button_UserDialog_btnClose', 0),
(39, 6, 'button_UserDialog_btnCancel', 0),
/* --> userDialogWindow Special Admin Panels */
(40, 6, 'panel_UserDialog_Status', 0),

/* --> userListWindow Search panel */
(42, 6, 'hbox_UserList_SearchUsers', 0),
/* Tab Details */
(43, 6, 'tab_UserDialog_Details', 0),
(44, 3, 'data_SeeAllUserData', 0),

/* --> secRoleDialogWindow */
(45, 0, 'secRoleDialogWindow', 0),
/* --> secRoleDialogWindow BUTTONS*/
(47, 6, 'button_SecRoleDialog_btnNew', 0),
(48, 6, 'button_SecRoleDialog_btnEdit', 0),
(49, 6, 'button_SecRoleDialog_btnDelete', 0),
(50, 6, 'button_SecRoleDialog_btnSave', 0),
(51, 6, 'button_SecRoleDialog_btnClose', 0),
(52, 6, 'button_SecRoleDialog_btnCancel', 0),

/* --> secGroupDialogWindow */
(53, 0, 'secGroupDialogWindow', 0),
/* --> secGroupDialogWindow BUTTONS*/
(55, 6, 'button_SecGroupDialog_btnNew', 0),
(56, 6, 'button_SecGroupDialog_btnEdit', 0),
(57, 6, 'button_SecGroupDialog_btnDelete', 0),
(58, 6, 'button_SecGroupDialog_btnSave', 0),
(59, 6, 'button_SecGroupDialog_btnClose', 0),
(60, 6, 'button_SecGroupDialog_btnCancel', 0),

/* --> secRightDialogWindow */
(61, 0, 'secRightDialogWindow', 0),
/* --> secRightDialogWindow BUTTONS*/
(63, 6, 'button_SecRightDialog_btnNew', 0),
(64, 6, 'button_SecRightDialog_btnEdit', 0),
(65, 6, 'button_SecRightDialog_btnDelete', 0),
(66, 6, 'button_SecRightDialog_btnSave', 0),
(67, 6, 'button_SecRightDialog_btnClose', 0),
(68, 6, 'button_SecRightDialog_btnCancel', 0);


INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(1, 1, 1, 0),
(2, 1, 2, 0),
(3, 1, 3, 0),
(4, 1, 4, 0),
(5, 1, 5, 0),
(6, 1, 6, 0),
(7, 1, 7, 0),
(8, 1, 8, 0),
(9, 1, 9, 0),
(10, 1, 10, 0),
(11, 1, 11, 0),
(12, 1, 12, 0),
(13, 1, 13, 0),
(14, 1, 14, 0),
(15, 1, 15, 0),
(16, 1, 16, 0),
(17, 1, 17, 0),
(18, 1, 18, 0),
(19, 1, 19, 0),
(20, 1, 20, 0),
(21, 1, 21, 0),
(22, 1, 22, 0),
(23, 1, 23, 0),
(25, 1, 25, 0),
(27, 1, 27, 0),
(28, 1, 27, 0),
(29, 1, 27, 0),
(30, 1, 30, 0),
(31, 1, 31, 0),
(32, 1, 32, 0),
(34, 1, 34, 0),
(35, 1, 35, 0),
(36, 1, 36, 0),
(37, 1, 37, 0),
(38, 1, 38, 0),
(39, 1, 39, 0),
(41, 1, 41, 0),
(42, 1, 42, 0),
(43, 1, 43, 0),
(44, 1, 44, 0),
(45, 1, 45, 0),
(47, 1, 47, 0),
(48, 1, 48, 0),
(49, 1, 49, 0),
(50, 1, 50, 0),
(51, 1, 51, 0),
(52, 1, 52, 0),
(53, 1, 53, 0),
(55, 1, 55, 0),
(56, 1, 56, 0),
(57, 1, 57, 0),
(58, 1, 58, 0),
(59, 1, 59, 0),
(60, 1, 60, 0),
(61, 1, 61, 0),
(63, 1, 63, 0),
(64, 1, 64, 0),
(65, 1, 65, 0),
(66, 1, 66, 0),
(67, 1, 67, 0),
(68, 1, 68, 0);


/* PARAMETER */
/* --> Page Parameter */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(69, 0, 'windowParameterList', 0),
(70, 0, 'windowParameterDetail', 0),
/* window_ParameterList Buttons*/
(71, 6, 'button_ParameterList_SearchName', 0),
/* window_ParameterDialog BUTTONS */
/* (71, 6, 'button_ParameterMain_btnNew', 0),*/
(72, 6, 'button_ParameterMain_btnEdit', 0),
/* (73, 6, 'button_ParameterMain_btnDelete', 0),*/
(74, 6, 'button_ParameterMain_btnSave', 0),
(75, 6, 'button_ParameterMain_btnClose', 0),
/* Parameter navigation buttons */
(76, 6, 'button_ParameterMain_btnCancel', 0),
(77, 6, 'button_ParameterMain_btnFirst', 0),
(78, 6, 'button_ParameterMain_btnPrevious', 0),
(79, 6, 'button_ParameterMain_btnNext', 0),
(80, 6, 'button_ParameterMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(69, 1, 69, 0),
(70, 1, 70, 0),
(72, 1, 72, 0),
(74, 1, 74, 0),
(75, 1, 75, 0),
(76, 1, 76, 0),
(77, 1, 77, 0),
(78, 1, 78, 0),
(79, 1, 79, 0),
(80, 1, 80, 0);

/* COMPANY PROFILE */
/* --> Page Company Profile */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(81, 0, 'windowCompanyProfileDetail', 0),
/* window_ParameterDialog BUTTONS */
(82, 6, 'button_CompanyProfileMain_btnEdit', 0),
(83, 6, 'button_CompanyProfileMain_btnSave', 0),
(84, 6, 'button_CompanyProfileMain_btnClose', 0),
(85, 6, 'button_CompanyProfileMain_btnCancel', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(81, 1, 81, 0),
(82, 1, 82, 0),
(83, 1, 83, 0),
(84, 1, 84, 0),
(85, 1, 85, 0);

/* WILAYAH */
/* --> Page Wilayah */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(86, 0, 'windowWilayahList', 0),
(87, 0, 'windowWilayahDetail', 0),
/* window_WilayahList Buttons*/
(88, 6, 'button_WilayahList_SearchName', 0),
/* window_WilayahDialog BUTTONS */
(89, 6, 'button_WilayahMain_btnNew', 0),
(90, 6, 'button_WilayahMain_btnEdit', 0),
(91, 6, 'button_WilayahMain_btnDelete', 0),
(92, 6, 'button_WilayahMain_btnSave', 0),
(93, 6, 'button_WilayahMain_btnClose', 0),
/* Wilayah navigation buttons */
(94, 6, 'button_WilayahMain_btnCancel', 0),
(95, 6, 'button_WilayahMain_btnFirst', 0),
(96, 6, 'button_WilayahMain_btnPrevious', 0),
(97, 6, 'button_WilayahMain_btnNext', 0),
(98, 6, 'button_WilayahMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(86, 1, 86, 0),
(87, 1, 87, 0),
(88, 1, 88, 0),
(89, 1, 89, 0),
(90, 1, 90, 0),
(91, 1, 91, 0),
(92, 1, 92, 0),
(93, 1, 93, 0),
(94, 1, 94, 0),
(95, 1, 95, 0),
(96, 1, 96, 0),
(97, 1, 97, 0),
(98, 1, 98, 0);



/* SATUAN_BARANG */
/* --> Page SatuanBarang */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(99, 0, 'windowSatuanBarangList', 0),
(100, 0, 'windowSatuanBarangDetail', 0),
/* window_SatuanBarangList Buttons*/
(101, 6, 'button_SatuanBarangList_SearchName', 0),
/* window_SatuanBarangDialog BUTTONS */
(102, 6, 'button_SatuanBarangMain_btnNew', 0),
(103, 6, 'button_SatuanBarangMain_btnEdit', 0),
(104, 6, 'button_SatuanBarangMain_btnDelete', 0),
(105, 6, 'button_SatuanBarangMain_btnSave', 0),
(106, 6, 'button_SatuanBarangMain_btnClose', 0),
/* SatuanBarang navigation buttons */
(107, 6, 'button_SatuanBarangMain_btnCancel', 0),
(108, 6, 'button_SatuanBarangMain_btnFirst', 0),
(109, 6, 'button_SatuanBarangMain_btnPrevious', 0),
(110, 6, 'button_SatuanBarangMain_btnNext', 0),
(111, 6, 'button_SatuanBarangMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(99, 1, 99, 0),
(100, 1, 100, 0),
(101, 1, 101, 0),
(102, 1, 102, 0),
(103, 1, 103, 0),
(104, 1, 104, 0),
(105, 1, 105, 0),
(106, 1, 106, 0),
(107, 1, 107, 0),
(108, 1, 108, 0),
(109, 1, 109, 0),
(110, 1, 110, 0),
(111, 1, 111, 0);

INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(112, 1, 'menuCat_Utility', 0),
(113, 2, 'menuItem_Utility_CompanyProfile', 0),
(114, 2, 'menuItem_Utility_Parameter', 0),
(115, 1, 'menuCat_Master', 0),
(116, 2, 'menuItem_Master_Wilayah', 0),
(117, 2, 'menuItem_Master_SatuanBarang', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(112, 1, 112, 0),
(113, 1, 113, 0),
(114, 1, 114, 0),
(115, 1, 115, 0),
(116, 1, 116, 0),
(117, 1, 117, 0);



/* STATUS */
/* --> Page Status */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(118, 0, 'windowStatusList', 0),
(119, 0, 'windowStatusDetail', 0),
/* window_StatusList Buttons*/
(120, 6, 'button_StatusList_SearchName', 0),
/* window_StatusDialog BUTTONS */
(121, 6, 'button_StatusMain_btnNew', 0),
(122, 6, 'button_StatusMain_btnEdit', 0),
(123, 6, 'button_StatusMain_btnDelete', 0),
(124, 6, 'button_StatusMain_btnSave', 0),
(125, 6, 'button_StatusMain_btnClose', 0),
/* Status navigation buttons */
(126, 6, 'button_StatusMain_btnCancel', 0),
(127, 6, 'button_StatusMain_btnFirst', 0),
(128, 6, 'button_StatusMain_btnPrevious', 0),
(129, 6, 'button_StatusMain_btnNext', 0),
(130, 6, 'button_StatusMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(118, 1, 118, 0),
(119, 1, 119, 0),
(120, 1, 120, 0),
(121, 1, 121, 0),
(122, 1, 122, 0),
(123, 1, 123, 0),
(124, 1, 124, 0),
(125, 1, 125, 0),
(126, 1, 126, 0),
(127, 1, 127, 0),
(128, 1, 128, 0),
(129, 1, 129, 0),
(130, 1, 130, 0);



/* KATEGORI BARANG */
/* --> Page KategoriBarang */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(131, 0, 'windowKategoriBarangList', 0),
(132, 0, 'windowKategoriBarangDetail', 0),
/* window_KategoriBarangList Buttons*/
(133, 6, 'button_KategoriBarangList_SearchName', 0),
/* window_KategoriBarangDialog BUTTONS */
(134, 6, 'button_KategoriBarangMain_btnNew', 0),
(135, 6, 'button_KategoriBarangMain_btnEdit', 0),
(136, 6, 'button_KategoriBarangMain_btnDelete', 0),
(137, 6, 'button_KategoriBarangMain_btnSave', 0),
(138, 6, 'button_KategoriBarangMain_btnClose', 0),
/* KategoriBarang navigation buttons */
(139, 6, 'button_KategoriBarangMain_btnCancel', 0),
(140, 6, 'button_KategoriBarangMain_btnFirst', 0),
(141, 6, 'button_KategoriBarangMain_btnPrevious', 0),
(142, 6, 'button_KategoriBarangMain_btnNext', 0),
(143, 6, 'button_KategoriBarangMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(131, 1, 131, 0),
(132, 1, 132, 0),
(133, 1, 133, 0),
(134, 1, 134, 0),
(135, 1, 135, 0),
(136, 1, 136, 0),
(137, 1, 137, 0),
(138, 1, 138, 0),
(139, 1, 139, 0),
(140, 1, 140, 0),
(141, 1, 141, 0),
(142, 1, 142, 0),
(143, 1, 143, 0);



/* JOB TYPE */
/* --> Page JobType */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(144, 0, 'windowJobTypeList', 0),
(145, 0, 'windowJobTypeDetail', 0),
/* window_JobTypeList Buttons*/
(146, 6, 'button_JobTypeList_SearchName', 0),
/* window_JobTypeDialog BUTTONS */
(147, 6, 'button_JobTypeMain_btnNew', 0),
(148, 6, 'button_JobTypeMain_btnEdit', 0),
(149, 6, 'button_JobTypeMain_btnDelete', 0),
(150, 6, 'button_JobTypeMain_btnSave', 0),
(151, 6, 'button_JobTypeMain_btnClose', 0),
/* JobType navigation buttons */
(152, 6, 'button_JobTypeMain_btnCancel', 0),
(153, 6, 'button_JobTypeMain_btnFirst', 0),
(154, 6, 'button_JobTypeMain_btnPrevious', 0),
(155, 6, 'button_JobTypeMain_btnNext', 0),
(156, 6, 'button_JobTypeMain_btnLast', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(144, 1, 144, 0),
(145, 1, 145, 0),
(146, 1, 146, 0),
(147, 1, 147, 0),
(148, 1, 148, 0),
(149, 1, 149, 0),
(150, 1, 150, 0),
(151, 1, 151, 0),
(152, 1, 152, 0),
(153, 1, 153, 0),
(154, 1, 154, 0),
(155, 1, 155, 0),
(156, 1, 156, 0);


/* Bonus Transport */
/* --> Page BonusTransport */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(157, 0, 'windowBonusTransportList', 0),
(158, 0, 'windowBonusTransportDetail', 0),
/* window_BonusTransportList Buttons*/
(159, 6, 'button_BonusTransportList_SearchName', 0),
/* window_BonusTransportDialog BUTTONS */
(160, 6, 'button_BonusTransportMain_btnNew', 0),
(161, 6, 'button_BonusTransportMain_btnEdit', 0),
(162, 6, 'button_BonusTransportMain_btnDelete', 0),
(163, 6, 'button_BonusTransportMain_btnSave', 0),
(164, 6, 'button_BonusTransportMain_btnClose', 0),
/* BonusTransport navigation buttons */
(165, 6, 'button_BonusTransportMain_btnCancel', 0),
(166, 6, 'button_BonusTransportMain_btnFirst', 0),
(167, 6, 'button_BonusTransportMain_btnPrevious', 0),
(168, 6, 'button_BonusTransportMain_btnNext', 0),
(169, 6, 'button_BonusTransportMain_btnLast', 0);


INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(157, 1, 157, 0),
(158, 1, 158, 0),
(159, 1, 159, 0),
(160, 1, 160, 0),
(161, 1, 161, 0),
(162, 1, 162, 0),
(163, 1, 163, 0),
(164, 1, 164, 0),
(165, 1, 165, 0),
(166, 1, 166, 0),
(167, 1, 167, 0),
(168, 1, 168, 0),
(169, 1, 169, 0);


INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(170, 2, 'menuItem_Master_Status', 0),
(171, 2, 'menuItem_Master_KategoriBarang', 0),
(172, 2, 'menuItem_Master_JobType', 0),
(173, 2, 'menuItem_Master_BonusTransport', 0);

INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(170, 1, 170, 0),
(171, 1, 171, 0),
(172, 1, 172, 0),
(173, 1, 173, 0);



/* Karyawan */
/* --> Page Karyawan */
INSERT INTO SEC_RIGHT (RIG_ID, RIG_TYPE, RIG_NAME, VERSION) values
(174, 2, 'menuItem_Master_Karyawan', 0),
(175, 0, 'windowKaryawanList', 0),
(176, 0, 'windowKaryawanDetail', 0),
/* window_KaryawanList Buttons*/
(177, 6, 'button_KaryawanList_SearchName', 0),
/* window_KaryawanDialog BUTTONS */
(178, 6, 'button_KaryawanMain_btnNew', 0),
(179, 6, 'button_KaryawanMain_btnEdit', 0),
(180, 6, 'button_KaryawanMain_btnDelete', 0),
(181, 6, 'button_KaryawanMain_btnSave', 0),
(182, 6, 'button_KaryawanMain_btnClose', 0),
/* Karyawan navigation buttons */
(183, 6, 'button_KaryawanMain_btnCancel', 0),
(184, 6, 'button_KaryawanMain_btnFirst', 0),
(185, 6, 'button_KaryawanMain_btnPrevious', 0),
(186, 6, 'button_KaryawanMain_btnNext', 0),
(187, 6, 'button_KaryawanMain_btnLast', 0);


INSERT INTO SEC_GROUPRIGHT (GRI_ID, GRP_ID, RIG_ID, VERSION) values 
(174, 1, 174, 0),
(175, 1, 175, 0),
(176, 1, 176, 0),
(177, 1, 177, 0),
(178, 1, 178, 0),
(179, 1, 179, 0),
(180, 1, 180, 0),
(181, 1, 181, 0),
(182, 1, 182, 0),
(183, 1, 183, 0),
(184, 1, 184, 0),
(185, 1, 185, 0),
(186, 1, 186, 0),
(187, 1, 187, 0);