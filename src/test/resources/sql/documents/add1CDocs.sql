insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T02:00:00.001', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 2, null, B'0', 1),
(2, 2, null, '2022-03-16T02:00:00.002', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(3, 2, null, '2022-03-16T02:00:00.003', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 4, null, B'0', 1);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values
(5, 2, null, '2022-03-16T02:00:00.005', 'CREDIT_ORDER_DOC', 1, B'1', B'1', 1000003456, 2, 1, null, null, null, B'0', 1000.00, 0.0, 2),
(7, 2, null, '2022-03-16T02:00:00.006', 'CREDIT_ORDER_DOC', 1, B'1', B'1', 1000003456, 4, 1, null, null, null, B'0', 1000.00, 0.0, 2);