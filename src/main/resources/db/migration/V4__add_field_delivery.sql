alter table check_KKM_info add is_delivery bit not null;
alter table document drop column is_delivery;