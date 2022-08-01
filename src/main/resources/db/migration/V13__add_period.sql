create table if not exists period
(id integer not null auto_increment, start_date date not null, end_date date not null,
is_current bit not null default B'0', primary key (id));