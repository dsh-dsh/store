alter table price add price_type varchar(255);

update price set price_type = 'RETAIL' where id = 1;
update price set price_type = 'RETAIL' where id = 2;
update price set price_type = 'RETAIL' where id = 3;