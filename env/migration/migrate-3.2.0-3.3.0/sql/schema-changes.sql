--
--  Copyright 2009 Denys Pavlov, Igor Azarnyi
--
--     Licensed under the Apache License, Version 2.0 (the "License");
--     you may not use this file except in compliance with the License.
--     You may obtain a copy of the License at
--
--         http://www.apache.org/licenses/LICENSE-2.0
--
--     Unless required by applicable law or agreed to in writing, software
--     distributed under the License is distributed on an "AS IS" BASIS,
--     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--     See the License for the specific language governing permissions and
--     limitations under the License.
--

--
-- This script is for MySQL only with some Derby hints inline with comments
-- We highly recommend you seek YC's support help when upgrading your system
-- for detailed analysis of your code.
--
-- Upgrades organised in blocks representing JIRA tasks for which they are
-- necessary - potentially you may hand pick the upgrades you required but
-- to keep upgrade process as easy as possible for future we recommend full
-- upgrades
--

--
-- YC-719 Customer object should support pricing policy attribute
--

alter table TCUSTOMER add column PRICINGPOLICY varchar(255);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11063,  'pricingpolicy', 'pricingpolicy',  1,  NULL,  'Customer Pricing Policy',  'Customer Pricing Policy', 1000,  1006, 0, 0, 0, 0);

--
-- YC-720 SkuPrice object should support pricing policy attribute
--

alter table TSKUPRICE add column PRICINGPOLICY varchar(255);

create index SKUPRICE_PRICINGPOLICY on TSKUPRICE (PRICINGPOLICY);

--
-- YC-724 Search products and sku by manufacturing code optimizations
--

alter table TPRODUCT add column PIM_CODE varchar(255);

create index PRODUCT_MCODE on TPRODUCT (MANUFACTURER_CODE);
create index PRODUCT_PCODE on TPRODUCT (PIM_CODE);
create index SKU_MCODE on TSKU (MANUFACTURER_CODE);
create index SKU_BCODE on TSKU (BARCODE);

--
-- YC-727 Disable shops
--

alter table TSHOP add column DISABLED bit not null default 0;
-- alter table TSHOP add column DISABLED smallint not null default 0;


--
-- YC-694 Change SLA pricing to use price lists
--

alter table TCARRIERSLA drop column PRICE;
alter table TCARRIERSLA drop column PER_CENT;
alter table TCARRIERSLA drop column PRICE_NOTLESS;
alter table TCARRIERSLA drop column PERCENT_NOTLESS;
alter table TCARRIERSLA drop column COST_NOTLESS;
alter table TCARRIERSLA drop column COST_NOTLESS;
alter table TCARRIERSLA drop column CURRENCY;

UPDATE TDATAGROUP SET DESCRIPTORS = '
shopcurrencies-demo.xml,
countrynames-demo.xml,
statenames-demo.xml,
carriernames-demo.xml,
carriershopnames-demo.xml,
carrierslanames-demo.xml,
promotionnames-demo.xml,
promotioncouponnames-demo.xml,
skuprices-demo.xml,
taxnames-demo.xml,
taxconfignames-demo.xml' WHERE NAME = 'YC DEMO: Initial Data';

--
-- YC-730 Shipping cost strategy based on weight and/or volume
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11024,  'PRODUCT_WEIGHT_KG', 'PRODUCT_WEIGHT_KG',  0,  NULL,  'Product weight KG',  'Product weight KG',  1005, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11025,  'PRODUCT_VOLUME_M3', 'PRODUCT_VOLUME_M3',  0,  NULL,  'Product volume m3',  'Product volume m3',  1005, 1003, 0, 0, 0, 0);

--
-- YC-734 Improve access to carriers for shop owners
--

alter table TCARRIERSHOP add column DISABLED bit default 0;
-- alter table TCARRIERSHOP add column DISABLED smallint default 0;

create index I_CRS_SHOP_DISABLED on TCARRIERSHOP (DISABLED);


--
-- Tidy up attribute types
--

update TATTRIBUTE set ETYPE_ID = 1001 where CODE = 'SYSTEM_IMAGE_VAULT';

--
-- YC-739 Improve access to warehouse for shop owners
--

alter table TSHOPWAREHOUSE add column DISABLED bit default 0;
-- alter table TSHOPWAREHOUSE add column DISABLED smallint default 0;

create index I_SWE_SHOP_DISABLED on TSHOPWAREHOUSE (DISABLED);

--
-- YC-743 Improve access to customers for shop owners
--

alter table TCUSTOMERSHOP add column DISABLED bit default 0;
-- alter table TCUSTOMERSHOP add column DISABLED smallint default 0;

create index I_CS_SHOP_DISABLED on TCUSTOMERSHOP (DISABLED);

--
-- YC-748 Improve preview capabilities for CMS in JAM
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11017,  'SYSTEM_PREVIEW_URL_TEMPLATE', 'SYSTEM_PREVIEW_URL_TEMPLATE',  1,  NULL,  'Admin\\CMS preview URL template',
  'This template is used to adjust URLs in content (<img src=""/> and <a href=""/>). For example: http://{primaryShopURL}:8080/ where {primaryShopURL} is a placeholder', 1000,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11018,  'SYSTEM_PREVIEW_URI_CSS', 'SYSTEM_PREVIEW_URI_CSS',  1,  NULL,  'Admin\\CMS preview CSS URI',
  'This URI points to preview CSS. For example on SFW: "yes-shop/wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css"', 1000,  1000, 0, 0, 0, 0);


--
-- YC-749 Perform order splitting by fulfilment centre
--

alter table TCUSTOMERORDERDELIVERYDET add column SUPPLIER_CODE varchar(255);
alter table TCUSTOMERORDERDET add column SUPPLIER_CODE varchar(255);

-- Template for updating order details for specific shop to specific supplier code - this has to be warehouse.CODE
-- assigned to the shop:
-- update TCUSTOMERORDERDET od set od.SUPPLIER_CODE = 'XXX' where od.CUSTOMERORDER_ID in (select o.CUSTOMERORDER_ID from TCUSTOMERORDER o where o.SHOP_ID = xxx);
update TCUSTOMERORDERDET od set od.SUPPLIER_CODE = 'Main' where od.CUSTOMERORDER_ID in (select o.CUSTOMERORDER_ID from TCUSTOMERORDER o where o.SHOP_ID = 10);
-- update TCUSTOMERORDERDELIVERYDET od set od.SUPPLIER_CODE = 'XXX' where od.CUSTOMERORDERDELIVERY_ID in (select d.CUSTOMERORDERDELIVERY_ID from TCUSTOMERORDER o, TCUSTOMERORDERDELIVERY d where o.CUSTOMERORDER_ID = d.CUSTOMERORDER_ID and o.SHOP_ID = xxx);
update TCUSTOMERORDERDELIVERYDET od set od.SUPPLIER_CODE = 'Main' where od.CUSTOMERORDERDELIVERY_ID in (select d.CUSTOMERORDERDELIVERY_ID from TCUSTOMERORDER o, TCUSTOMERORDERDELIVERY d where o.CUSTOMERORDER_ID = d.CUSTOMERORDER_ID and o.SHOP_ID = 10);


alter table TWAREHOUSE add column DISPLAYNAME longtext;
-- alter table TWAREHOUSE add column DISPLAYNAME varchar(4000);

alter table TCARRIERSLA add column SUPPORTED_FCS varchar(1024);

--
-- YC-737 Split ImpexFederationFilter into two separate filters
--

INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2006, 'Export Attribute Definitions', 'EXPORT', 'attributes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2007, 'Export Brands', 'EXPORT', 'brands.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2008, 'Export Product Types (Definitions)', 'EXPORT', 'producttypes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2009, 'Export Product Types (Attributes)', 'EXPORT', 'producttypeattributes.xml');

INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2006, 'attributes.xml', 'WEBINF_XML', 'attributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2007, 'brands.xml', 'WEBINF_XML', 'brands.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2008, 'producttypes.xml', 'WEBINF_XML', 'producttypes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2009, 'producttypeattributes.xml', 'WEBINF_XML', 'producttypeattributes.xml');

--
-- YC-707 Shopping cart object should have setting to include or exclude taxes
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10965,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_T', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES',  0,  NULL,  'Product: Limit price tax information to customer types',
  'Limits price tax information on search results and product pages for given customer types. Tax info must be enabled. Blank value is treated as no restriction. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10964,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_C', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE',  0,  NULL,  'Product: Allow changing price tax information view for customer types',
  'Allow changing price tax information view for customer types. Blank value is treated as no customer can change tax information view. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

--
-- YC-751 Global product reindex must prevent inventory changed processor from running
--


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11116,  'JOB_REINDEX_PRODUCT_BATCH_SIZE', 'JOB_REINDEX_PRODUCT_BATCH_SIZE',  0,  NULL,  'Job\\Product re-index: batch size',
    'Number of products to reindex in single batch.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11117,  'JOB_PRODINVUP_DELTA', 'JOB_PRODINVUP_DELTA',  0,  NULL,  'Job\\Inventory Change Detection: max delta after delay',
    'Number of inventory records that had changed after a second the job started (default is 100). If changes exceed this number then re-indexing is postponed.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11118,  'JOB_PRODINVUP_FULL', 'JOB_PRODINVUP_FULL',  0,  NULL,  'Job\\Inventory Change Detection: changes for full reindex',
    'Number of inventory records that should trigger full re-index rather than partial. Default is 1000',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11119,  'JOB_PRODINVUP_DELTA_S', 'JOB_PRODINVUP_DELTA_S',  0,  NULL,  'Job\\Inventory Change Detection: delta delay in seconds',
    'Delay for delta check. Default 15s.',  1006, 1000, 0, 0, 0, 0);

--
-- YC-754 Closed access shops
--

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8004,  'SHOP_SF_REQUIRE_LOGIN', 'SHOP_SF_REQUIRE_LOGIN',  1,  NULL,  'Customer: login required',  'Anonymous browsing for this shop is prohibited',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8005,  'SHOP_SF_REQUIRE_REG_AT', 'SHOP_SF_REQUIRE_REG_APPROVE_TYPES',  0,  NULL,  'Customer: registration types that require approval email (CSV)',  'CSV of customer types that must be approved by shop admin
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8006,  'SHOP_SF_REQUIRE_REG_NT', 'SHOP_SF_REQUIRE_REG_NOTIFY_TYPES',  0,  NULL,  'Customer: registration types that require notification email (CSV)',  'CSV of customer types for which shop admin is notified
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

--
-- YC-000 Organise Shop Preferences naming
--

update TATTRIBUTE set name = 'Tax: Enable price tax information' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO';
update TATTRIBUTE set name = 'Tax: Enable price tax information to show net prices' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET';
update TATTRIBUTE set name = 'Tax: Enable price tax information to show tax amount' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT';
update TATTRIBUTE set name = 'Tax: Display price tax information for customer types only' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_TYPES';
update TATTRIBUTE set name = 'Customer: Allow changing price tax information view for customer types' where CODE = 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE';
update TATTRIBUTE set name = 'Shop: primary shop admin email' where CODE = 'SHOP_ADMIN_EMAIL';
update TATTRIBUTE set name = 'Checkout: enable guest checkout' where CODE = 'SHOP_CHECKOUT_ENABLE_GUEST';
update TATTRIBUTE set name = 'Customer: supported customer types' where CODE = 'SHOP_CUSTOMER_TYPES';
update TATTRIBUTE set name = 'Customer: session expiry in seconds' where CODE = 'CART_SESSION_EXPIRY_SECONDS';
update TATTRIBUTE set name = 'Checkout: enable quantity picker for products' where CODE = 'CART_ADD_ENABLE_QTY_PICKER';
