insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 1, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(2, 1, null, '2022-03-16T08:30:36.395', 'INVENTORY_DOC', null, B'1', B'1', 2, 3, 1, 1, null, 3, B'0', 1),
(3, 1, null, '2022-03-16T09:30:36.395', 'POSTING_DOC', null, B'1', B'1', 3, 3, null, 1, null, 3, B'0', 1),
(4, 6, null, '2022-10-16T01:00:00.400', 'RECEIPT_DOC', null, B'1', B'1', 4, 3, 1, 1, null, 3, B'0', 1),
(5, 6, null, '2022-10-16T01:00:00.401', 'WRITE_OFF_DOC', null, B'1', B'1', 4, 3, 1, 1, 3, null, B'0', 1),
(6, 2, null, '2022-10-28T11:30:36.395', 'REQUEST_DOC', null, B'1', B'1', 11, 3, 1, 1, null, 3, B'0', 1);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values
(7, 6, null, '2022-10-16T01:00:00.402', 'CREDIT_ORDER_DOC', 1, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1000.00, 0.0, 2);