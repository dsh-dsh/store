create table if not exists account
(id integer not null auto_increment, account_number varchar(255), bank_name varchar(255),
bank_number integer not null, cor_account_number varchar(255), company_id integer, primary key (id));

create table if not exists company
(id integer not null auto_increment, inn bigint not null, is_mine bit not null default B'0',
kpp integer not null, name varchar(255), primary key (id));

create table if not exists sets
(id integer not null auto_increment, number integer not null, item_id integer not null, set_id integer not null, primary key (id));

create table if not exists document
(d_type integer not null, id integer not null auto_increment, date_time datetime(6), doc_type varchar(255),
is_hold bit not null default B'0', is_payed bit not null default B'0', number integer not null, amount float,
payment_type varchar(255), tax float, author_id integer not null, base_document_id integer,
individual_id integer, project_id integer not null, supplier_id integer, recipient_id integer,
storage_from_id integer, storage_to_id integer, is_deleted bit not null default B'0', primary key (id));

create table if not exists document_item
(id integer not null auto_increment, discount float not null, price float not null,
quantity float not null, quantity_fact float not null, item_id integer, document_id integer, primary key (id));

create table if not exists check_kkm_info
(id integer not null auto_increment, amount_received float not null, cash_register_number bigint not null,
check_number integer not null, guest_number integer not null, is_delivery bit not null default B'0',
is_kkm_checked bit not null default B'0',is_payed bit not null default B'0', is_payed_by_card bit not null default B'0',
is_return bit not null default B'0', table_number integer not null,date_time datetime(6), waiter varchar(255),
check_id integer, primary key (id));

create table if not exists item
(id integer not null auto_increment, is_alcohol bit not null default B'0', is_not_in_price_list bit not null default B'0',
is_garnish bit not null default B'0', is_in_employee_menu bit not null default B'0', is_include_garnish bit not null default B'0',
is_include_sauce bit not null default B'0', is_sauce bit not null default B'0', is_weight bit not null default B'0', name varchar(255),
print_name varchar(255), comment varchar(255), reg_time datetime(6) not null, unit varchar(255), is_node bit not null default B'0',
workshop varchar(255), parent_id integer, is_deleted bit not null default B'0', number integer not null, primary key (id));

create table if not exists price
(id integer not null auto_increment, price_date date, price_value float, item_id integer, price_type varchar(255),
primary key (id));

create table if not exists project
(id integer not null auto_increment, name varchar(255), primary key (id));

create table storage
(id integer not null auto_increment, name varchar(255), type varchar(255), primary key (id));

create table users
(id integer not null auto_increment, birth_date date not null, email varchar(255), first_name varchar(255),
last_name varchar(255), password varchar(255), phone varchar(255), reg_time datetime(6) not null,
role varchar(255), is_node bit not null default B'0', primary key (id));