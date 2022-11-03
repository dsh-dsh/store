insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-04-16 11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 332, 3, null, 1, null, 3, B'0', 1),
(2, 2, null, '2022-04-20 11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 332, 3, null, 1, null, 3, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(1, 0.0, 7, 1, 100.00, 10.0, 0.0),
(2, 0.0, 7, 2, 200.00, 10.0, 0.0);

insert into lot
(id, document_item_id, lot_time)
values
(1, 1, '2022-04-16 11:00:00.000'),
(2, 2, '2022-04-20 11:00:00.000');

insert into lot_movement
(id, movement_time, lot_id, storage_id, document_id, d_quantity)
values
(1, '2022-04-16 11:00:00.000000', 1, 3, 1, 10.00),
(2, '2022-04-20 11:00:00.000000', 2, 3, 2, 10.00);