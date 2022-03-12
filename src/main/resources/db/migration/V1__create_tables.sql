create table account
(id bigint not null auto_increment, account_number varchar(255), bank_name varchar(255), bank_number integer not null,
cor_account_number varchar(255), company_id bigint, primary key (id));

create table company
(id bigint not null auto_increment, inn bigint not null, is_mine bit not null, kpp integer not null, name varchar(255),
primary key (id));

create table dinners
(item_id bigint not null, in_dinners_id bigint not null);

create table document
(doc_type varchar(31) not null, id bigint not null auto_increment, date_time datetime(6), is_hold bit not null,
is_payed bit not null, number bigint not null, amount double precision, tax double precision, payment_type varchar(255),
author_id bigint not null, individual_id bigint, project_id integer not null, recipient_id bigint,
storage_to_id bigint, storage_from_id bigint, supplier_id bigint, base_document_id bigint, is_delivery bit not null,
dtype varchar(255), primary key (id));

create table document_item
(id bigint not null auto_increment, price double precision not null, quantity double precision not null, item_id bigint,
document_id bigint, primary key (id));

create table check_KKM_info
(id bigint not null auto_increment, check_number bigint, cash_register_number varchar(255), amount_received double precision not null,
guest_number integer, table_number integer, waiter varchar(255), time datetime, is_return bit not null, is_KKM_checked bit not null,
is_payed bit not null, is_payed_by_card bit not null, check_id bigint,  primary key (id));

create table item
(id bigint not null auto_increment, is_alcohol bit not null, is_garnish bit not null, is_in_employee_menu bit not null,
is_include_garnish bit not null, is_include_sauce bit not null, is_sauce bit not null, is_weight bit not null,
name varchar(255), print_name varchar(255), reg_time datetime(6) not null, parent_id bigint, unit varchar(255),
workshop varchar(255), primary key (id));

create table price
(id bigint not null auto_increment, date date, value double precision not null, item_id bigint, primary key (id));

create table project
(id integer not null auto_increment, name varchar(255), primary key (id));

create table storage
(id bigint not null auto_increment, name varchar(255), type varchar(255), primary key (id));

create table users
(id bigint not null auto_increment, birth_date date not null, email varchar(255), first_name varchar(255),
last_name varchar(255), password varchar(255), phone varchar(255), reg_time datetime(6) not null, role varchar(255),
primary key (id));