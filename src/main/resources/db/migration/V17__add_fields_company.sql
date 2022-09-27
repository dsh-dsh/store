ALTER TABLE company ADD is_node  bit not null default B'0';
ALTER TABLE company ADD parent_id integer default 0;
ALTER TABLE company ADD code integer default 0;
ALTER TABLE company ADD phone varchar(255);
ALTER TABLE company ADD email varchar(255);