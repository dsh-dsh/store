insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T06:30:36.395', 'POSTING_DOC', null, B'1', B'1', 332, 3, null, 1, null, 3, B'0', 1),
(2, 2, null, '2022-03-16T06:30:36.395', 'WRITE_OFF_DOC', null, B'1', B'1', 221, 3, null, 1, 3, null, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact)
values
(0.0, 7, 1, 200.00, 10.0, 0.0),
(0.0, 8, 1, 100.00, 10.0, 0.0),
(0.0, 7, 2, 200.00, 1.0, 0.0),
(0.0, 8, 2, 100.00, 2.0, 0.0);

insert into lot
(id, document_id, item_id, lot_time, price, quantity)
values
(1, 1, 7, '2022-03-01 11:00:00.000', 200.00, 10.00),
(2, 1, 8, '2022-03-01 11:00:00.000', 100.00, 10.00);

insert into lot_movement
(id, movement_time, lot_id, storage_id, document_id, quantity)
values
(1, '2022-03-01 11:00:00.000000', 1, 3, 1, 10.00),
(2, '2022-03-10 11:00:00.000000', 2, 3, 1, 10.00);