insert into lot
(id, document_item_id, lot_time)
values
(1, 1, '2022-03-16T09:30:36.395'),
(2, 2, '2022-03-16T10:30:36.395');

insert into lot_movement
(id, movement_time, lot_id, storage_id, document_id, quantity)
values
(1, '2022-03-16 09:30:36.395000', 1, 3, 1, 5),
(2, '2022-03-16 10:30:36.395000', 2, 3, 2, 5),
(3, '2022-03-16 11:30:36.395000', 1, 3, 3, -3),
(4, '2022-03-16 12:30:36.395000', 2, 3, 4, -3),
(5, '2022-03-16 12:30:36.395000', 2, 1, 4, 3);

update document set is_hold = B'1';