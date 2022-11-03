insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T09:30:36.395', 'POSTING_DOC', null, B'0', B'0', 1, 3, null, 1, null, 3, B'0', 1),
(2, 2, null, '2022-03-16T10:30:36.395', 'RECEIPT_DOC', null, B'0', B'0', 1, 3, 1, 1, null, 3, B'0', 1),
(3, 2, null, '2022-03-16T11:30:36.395', 'WRITE_OFF_DOC', null, B'0', B'0', 1, 3, 1, 1, 3, null, B'0', 1),
(4, 2, null, '2022-03-16T12:30:36.395', 'MOVEMENT_DOC', null, B'0', B'0', 1, 3, 1, 1, 3, 1, B'0', 1),
(5, 2, null, '2022-03-16T13:30:36.395', 'REQUEST_DOC', null, B'0', B'0', 1, 3, 1, 1, 3, 1, B'0', 1);

insert into document_item
(id, item_id, document_id, price, d_quantity, quantity_fact, discount)
values
(1, 7, 1, 200.00, 5.0, 0.0, 0.0),
(2, 8, 2, 100.00, 5.0, 0.0, 0.0),
(3, 7, 3, 0.00, 3.0, 0.0, 0.0),
(4, 8, 4, 0.00, 3.0, 0.0, 0.0),
(5, 7, 5, 0.00, 5.0, 0.0, 0.0);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values
(6, 2, null, '2022-03-16T14:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 6, 3, 1, null, null, null, B'0', 1000.00, 0.0, 2);