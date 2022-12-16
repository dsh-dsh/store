alter table document_item drop column quantity;
alter table lot_movement drop column quantity;
alter table document_item rename column d_quantity to quantity;
alter table lot_movement rename column d_quantity to quantity;