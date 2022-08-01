create table if not exists lot
(id bigint not null auto_increment, document_item_id integer not null, lot_time datetime(6), primary key (id));

create table if not exists lot_movement
(id bigint not null auto_increment, movement_time datetime(6), lot_id bigint not null, storage_id int not null,
document_id int not null, quantity float not null, primary key (id));

alter table lot add constraint FK_lot_document_item_id foreign key (document_item_id) references document_item (id);
alter table lot_movement add constraint FK_lot_id foreign key (lot_id) references lot (id);
alter table lot_movement add constraint FK_lot_storage_id foreign key (storage_id) references storage (id);
alter table lot_movement add constraint FK_lot_document_id foreign key (document_id) references document (id);