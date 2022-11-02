alter table lot_movement add d_quantity decimal(10,3) default 0;
update lot_movement set d_quantity = quantity where d_quantity = 0;