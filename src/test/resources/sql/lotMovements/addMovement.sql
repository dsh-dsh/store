insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(5, 2, null, '2022-03-29T11:00:00.000', 'MOVEMENT_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, 2, B'0', 1),
(6, 2, null, '2022-03-30T11:00:00.000', 'MOVEMENT_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, 1, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(5, 0.0, 7, 5, 0.00, 2.0, 0.0),
(6, 0.0, 8, 6, 0.00, 2.0, 0.0);

insert into lot_movement
(id, movement_time, lot_id, storage_id, document_id, d_quantity)
values
(5, '2022-03-29 11:00:00.000000', 1, 3, 5, -2.00),
(6, '2022-03-29 11:00:00.000000', 1, 2, 5, 2.00),
(7, '2022-03-30 11:00:00.000000', 2, 3, 3, -2.00),
(8, '2022-03-30 11:00:00.000000', 2, 1, 4, 2.00);