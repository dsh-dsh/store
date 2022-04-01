create table if not exists lot
(id bigint not null, item_id int not null, lot_date datetime(6), primary key (id));

create table if not exists lot_movement
(id bigint not null, movement_date datetime(6), lot_id bigint not null, storage_id int not null,
document_id int not null, amount float not null, quantity float not null, primary key (id));

alter table lot add constraint FK_lot_item_id foreign key (item_id) references item (id);
alter table lot_movement add constraint FK_lot_id foreign key (lot_id) references lot (id);
alter table lot_movement add constraint FK_lot_storage_id foreign key (storage_id) references storage (id);
alter table lot_movement add constraint FK_lot_document_id foreign key (document_id) references document (id);

create table check_info_seq (next_val int);
insert into check_info_seq values (1);
create table doc_item_seq (next_val int);
insert into doc_item_seq values (1);
create table doc_seq (next_val int);
insert into doc_seq values (1);
create table ingredient_seq (next_val int);
insert into ingredient_seq values (1);
create table lot_seq (next_val bigint);
insert into lot_seq values (1);
create table lot_movement_seq (next_val bigint);
insert into lot_movement_seq values (1);