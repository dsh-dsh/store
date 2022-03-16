create table account
(id integer not null auto_increment, account_number varchar(255), bank_name varchar(255),
bank_number integer not null, cor_account_number varchar(255), company_id integer, primary key (id));

create table company
(id integer not null auto_increment, inn bigint not null, is_mine bit not null,
kpp integer not null, name varchar(255), primary key (id));

create table dinners
(item_id integer not null, in_dinners_id integer not null);

create table document
(d_type integer not null, id integer not null auto_increment, date_time datetime(6), doc_type varchar(255),
is_hold bit not null, is_payed bit not null, number integer not null, amount float,
payment_type varchar(255), tax float, author_id integer not null, base_document_id integer,
individual_id integer, project_id integer not null, supplier_id integer, recipient_id integer,
storage_from_id integer, storage_to_id integer, primary key (id));

create table document_item
(id integer not null auto_increment, discount float not null, price float not null,
quantity float not null, quantity_fact float not null, item_id integer, document_id integer, primary key (id));

create table check_kkm_info
(id integer not null auto_increment, amount_received float not null, cash_register_number bigint not null,
check_number integer not null, guest_number integer not null, is_delivery bit not null, is_kkm_checked bit,
is_payed bit not null, is_payed_by_card bit not null, is_return bit not null, table_number integer not null,
date_time datetime(6), waiter varchar(255), check_id integer, primary key (id));

create table item
(id integer not null auto_increment, is_alcohol bit not null, is_garnish bit not null,
is_in_employee_menu bit not null, is_include_garnish bit not null, is_include_sauce bit not null,
is_sauce bit not null, is_weight bit not null, name varchar(255), print_name varchar(255),
reg_time datetime(6) not null, unit varchar(255), workshop varchar(255), parent_id integer,
primary key (id));

create table price
(id integer not null auto_increment, price_date date, price_value float, item_id integer,
primary key (id));

create table project
(id integer not null auto_increment, name varchar(255), primary key (id));

create table storage
(id integer not null auto_increment, name varchar(255), type varchar(255), primary key (id));

create table users
(id integer not null auto_increment, birth_date date not null, email varchar(255), first_name varchar(255),
last_name varchar(255), password varchar(255), phone varchar(255), reg_time datetime(6) not null,
role varchar(255), primary key (id));

--create table account
--(id int not null auto_increment, account_number varchar(20), bank_name varchar(120),
--bank_number int not null, cor_account_number varchar(20), company_id int, primary key (id));

--create table company
--(id int not null auto_increment, inn bigint not null, is_mine bit not null, kpp integer not null, name varchar(120),
--primary key (id));

--create table dinners
--(item_id int not null, in_dinners_id int not null);

--create table document
--(doc_type varchar(50) not null, id int not null auto_increment, date_time datetime, is_hold bit not null,
--is_payed bit not null, number int not null, amount float(7,2), tax float(7,2), payment_type varchar(50),
--author_id int not null, individual_id int, project_id int not null, recipient_id int, storage_to_id int,
--storage_from_id int, supplier_id int, base_document_id int, d_type int, primary key (id));

--create table document_item
--(id int not null auto_increment, price float(5,2) not null, discount float(5,2), quantity float(5,3), item_id int,
--document_id int, primary key (id));

--create table check_KKM_info
--(id int not null auto_increment, check_number int, cash_register_number bigint, amount_received float(7,2) not null,
--guest_number tinyint, table_number tinyint, waiter varchar(120), time datetime, is_return bit not null,
--is_KKM_checked bit not null, is_payed bit not null, is_payed_by_card bit not null, is_delivery bit not null,
--check_id int, primary key (id));

--create table item
--(id int not null auto_increment, is_alcohol bit not null, is_garnish bit not null, is_in_employee_menu bit not null,
--is_include_garnish bit not null, is_include_sauce bit not null, is_sauce bit not null, is_weight bit not null,
--name varchar(100), print_name varchar(100), reg_time datetime not null, parent_id int, unit varchar(50),
--workshop varchar(20), primary key (id));
--
--create table price
--(id int not null auto_increment, price_date date, price_value float(5,2), item_id int, primary key (id));
--
--create table project
--(id int not null auto_increment, name varchar(50), primary key (id));
--
--create table storage
--(id int not null auto_increment, name varchar(50), type varchar(50), primary key (id));
--
--create table users
--(id int not null auto_increment, birth_date date not null, email varchar(120), first_name varchar(50),
--last_name varchar(50), password varchar(100), phone varchar(12), reg_time datetime not null, role varchar(255),
--primary key (id));