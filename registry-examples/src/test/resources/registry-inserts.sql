/*
* Inserts below are only needed when setting up a new registry-sandbox, depended on by:
*  - ipt.gbif.org
*  - ipt-uat.gbif.org
*  - registry-examples integration tests
*  - training-ipt-a, training-ipt-b, training-ipt-c.gbif.org
*/

-- Add organization named "Test Organization #1"
INSERT INTO organization(key,endorsing_node_key,endorsement_approved,password,title,description,language,email,phone,homepage,logo_url,address,city,country,postal_code,latitude,longitude,created,modified,created_by,modified_by,endorsed,endorsement_status)
VALUES('0a16da09-7719-40de-8d4f-56a15ed52fb6'::uuid,'02c40d2a-1cba-4633-90b7-e36e5e97aba8'::uuid,true::boolean,'password','Test Organization #1','Test Organization for IPT','EN','{helpdesk@gbif.org}','{+45 35 32 14 70}','{https://www.gbif.org/}','https://rs.gbif.org/style/logo.svg','{Universitetsparken 15}','Copenhagen','DK','2100','55.6761','55.6761',now(),now(),'Script', 'Script',now(),'ENDORSED');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21000,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO organization_contact(organization_key,contact_key,type,is_primary)
VALUES('0a16da09-7719-40de-8d4f-56a15ed52fb6'::uuid,21000,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Give ws_client_demo permission to edit Test Organization #1
INSERT INTO editor_rights (username, key) values ('ws_client_demo', '0a16da09-7719-40de-8d4f-56a15ed52fb6');

-- Add installation to host ipt.gbif.org
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('63e64bc2-d3de-43ee-bb52-cd65c759dd99'::uuid,'0a16da09-7719-40de-8d4f-56a15ed52fb6'::uuid,'password','Test IPT','Test IPT for IPT.GBIF.ORG','IPT_INSTALLATION',now(),now(),'Script', 'Script');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21001,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('63e64bc2-d3de-43ee-bb52-cd65c759dd99'::uuid,21001,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Add installation to host ipt-uat.gbif.org
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('8949a635-0eb0-4bf1-aaf9-9af97a0ea949'::uuid,'0a16da09-7719-40de-8d4f-56a15ed52fb6'::uuid,'password','Test IPT','Test IPT for IPT-UAT.GBIF.ORG','IPT_INSTALLATION',now(),now(),'Script', 'Script');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21002,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('8949a635-0eb0-4bf1-aaf9-9af97a0ea949'::uuid,21002,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('2cfd84f9-e9bd-4c90-8cf5-c1e582cf347b'::uuid,'cace8d10-2646-11d8-a2da-b8a03c50a862'::uuid,'password','NLBIF IPT tryout','Test IPT for NLBIF','IPT_INSTALLATION',now(),now(),'Script', 'Script');

-- Add organization named "Training Organization"
INSERT INTO organization(key,endorsing_node_key,endorsement_approved,password,title,description,language,email,phone,homepage,logo_url,address,city,country,postal_code,latitude,longitude,created,modified,created_by,modified_by,endorsed,endorsement_status)
VALUES('7b720883-a229-45b8-8792-1e610be5ce78'::uuid,'02c40d2a-1cba-4633-90b7-e36e5e97aba8'::uuid,true::boolean,'6A2g9KUUMjau','Training Organization','This organization is used during IPT training workshops.','EN','{helpdesk@gbif.org}','{+45 35 32 14 70}','{https://www.gbif.org/}','https://rs.gbif.org/style/logo.svg','{Universitetsparken 15}','Copenhagen','DK','2100','55.6761','55.6761',now(),now(),'Script', 'Script',now(),'ENDORSED');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21006,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO organization_contact(organization_key,contact_key,type,is_primary)
VALUES('7b720883-a229-45b8-8792-1e610be5ce78'::uuid,21006,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Add installation to host training-ipt-a.gbif.org, training-ipt-b.gbif.org, training-ipt-c.gbif.org
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('9b6c3ca9-73dd-4246-b198-16cf8bc93575'::uuid,'7b720883-a229-45b8-8792-1e610be5ce78'::uuid,'6A2g9KUUMjau','Training IPT A','Training IPT A, for use during training workshops.','IPT_INSTALLATION',now(),now(),'Script', 'Script');
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('961dfb09-de6a-47a9-8f5c-b45a7ed921ab'::uuid,'7b720883-a229-45b8-8792-1e610be5ce78'::uuid,'6A2g9KUUMjau','Training IPT B','Training IPT B, for use during training workshops.','IPT_INSTALLATION',now(),now(),'Script', 'Script');
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('2730d3c4-62d3-4615-aef8-4b16c161c6cd'::uuid,'7b720883-a229-45b8-8792-1e610be5ce78'::uuid,'6A2g9KUUMjau','Training IPT C','Training IPT C, for use during training workshops.','IPT_INSTALLATION',now(),now(),'Script', 'Script');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21007,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());
INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21008,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());
INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21009,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('9b6c3ca9-73dd-4246-b198-16cf8bc93575'::uuid,21007,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);
INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('961dfb09-de6a-47a9-8f5c-b45a7ed921ab'::uuid,21008,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);
INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('2730d3c4-62d3-4615-aef8-4b16c161c6cd'::uuid,21009,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Add organization used exclusively for Jenkins
INSERT INTO organization(key,endorsing_node_key,endorsement_approved,password,title,description,language,email,phone,homepage,logo_url,address,city,country,postal_code,latitude,longitude,created,modified,created_by,modified_by,endorsed,endorsement_status)
VALUES('62922b92-69d1-4c4b-831c-b23d5412a124'::uuid,'02c40d2a-1cba-4633-90b7-e36e5e97aba8'::uuid,true::boolean,'password','Test Organization Jenkins','Test Organization for IPT built by Jenkins','EN','{helpdesk@gbif.org}','{+45 35 32 14 70}','{https://builds.gbif.org/}','https://rs.gbif.org/style/logo.svg','{Universitetsparken 15}','Copenhagen','DK','2100','55.6761','55.6761',now(),now(),'Script', 'Script',now(),'ENDORSED');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21003,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO organization_contact(organization_key,contact_key,type,is_primary)
VALUES('62922b92-69d1-4c4b-831c-b23d5412a124'::uuid,21003,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Add installation to host https://ipt.gbif-dev.org/ipt/
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('107acb59-89f2-40ea-84e1-69a5ec1f08c0'::uuid,'62922b92-69d1-4c4b-831c-b23d5412a124'::uuid,'password','Dev (Jenkins) IPT','Test IPT instance deployed by Jenkins communicating with the Dev Registry (not the registry-sandbox)','IPT_INSTALLATION',now(),now(),'Script', 'Script');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21004,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('107acb59-89f2-40ea-84e1-69a5ec1f08c0'::uuid,21004,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);

-- Add installation to demonstrate registering by API calls
INSERT INTO installation(key,organization_key,password,title,description,type,created,modified,created_by,modified_by)
VALUES('92d76df5-3de1-4c89-be03-7a17abad962a'::uuid,'0a16da09-7719-40de-8d4f-56a15ed52fb6'::uuid,'password','Test HTTP installation','Test HTTP installation for use with https://github.com/gbif/registry/tree/master/registry-examples','HTTP_INSTALLATION',now(),now(),'Script', 'Script');

INSERT INTO contact(key,first_name,last_name,position,email,phone,organization,address,city,country,postal_code,created_by,modified_by,created,modified)
VALUES(21005,'GBIF','Helpdesk','{Programmer}','{helpdesk@gbif.org}','{+45 35 32 14 70}','GBIF','{Universitetsparken 15}','Copenhagen','DK','2100','Script','Script',now(),now());

INSERT INTO installation_contact(installation_key,contact_key,type,is_primary)
VALUES('92d76df5-3de1-4c89-be03-7a17abad962a'::uuid,21005,'ADMINISTRATIVE_POINT_OF_CONTACT'::enum_contact_type,true);
