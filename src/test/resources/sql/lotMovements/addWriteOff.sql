insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(3, 2, null, '2022-03-20T11:00:00.000', 'WRITE_OFF_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, null, B'0', 1),
(4, 2, null, '2022-03-25T11:00:00.000', 'WRITE_OFF_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, null, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(3, 0.0, 7, 3, 0.00, 3.0, 0.0),
(4, 0.0, 8, 4, 0.00, 5.0, 0.0);

insert into lot_movement
(id, movement_time, lot_id, storage_id, document_id, quantity)
values
(3, '2022-03-20 11:00:00.000000', 1, 3, 3, -3.00),
(4, '2022-03-25 11:00:00.000000', 2, 3, 4, -5.00);