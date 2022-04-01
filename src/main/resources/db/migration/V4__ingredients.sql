create table if not exists ingredient
(id integer not null auto_increment, parent_id integer not null, child_id integer not null,
is_deleted bit not null default B'0', primary key (id));

create table if not exists periodic_quantity
(id integer not null auto_increment, ingredient_id integer not null, data datetime(6),
quantity float not null, type varchar(255), primary key (id));

alter table ingredient add constraint FK_parent_id foreign key (parent_id) references item (id);
alter table ingredient add constraint FK_child_id foreign key (child_id) references item (id);
alter table periodic_quantity add constraint FK_ingredient_id foreign key (ingredient_id) references ingredient (id);