insert into krms_ctgry_s values(NULL);
insert into krms_ctgry_t (ctgry_id, nmspc_cd, nm) values (LAST_INSERT_ID(), 'OLE', 'Patron');

insert into krms_ctgry_s values(NULL);
insert into krms_ctgry_t (ctgry_id, nmspc_cd, nm) values (LAST_INSERT_ID(), 'OLE', 'Instance');

insert into krms_ctgry_s values(NULL);
insert into krms_ctgry_t (ctgry_id, nmspc_cd, nm) values (LAST_INSERT_ID(), 'OLE', 'Time of Year');
