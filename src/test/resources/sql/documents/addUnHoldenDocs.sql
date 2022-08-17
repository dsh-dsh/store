insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-04-16T01:00:00.001', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(2, 2, null, '2022-04-16T01:00:00.002', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(3, 2, null, '2022-04-16T01:00:00.003', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(4, 2, null, '2022-04-16T01:00:00.004', 'RECEIPT_DOC', null, B'0', B'1', 1, 3, 1, 1, null, 3, B'0', 1),
(5, 2, null, '2022-04-16T01:00:00.005', 'WRITE_OFF_DOC', null, B'0', B'1', 1, 3, 1, 1, 3, null, B'0', 1);