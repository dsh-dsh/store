create table if not exists default_property_setting
(id integer not null auto_increment, user_id integer not null, setting_type varchar(255),
property integer not null, primary key (id));

alter table default_property_setting add constraint FK_user_id foreign key (user_id) references users (id);