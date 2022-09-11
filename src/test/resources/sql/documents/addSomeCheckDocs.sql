insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(2, 2, null, '2022-03-17T06:30:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(3, 2, null, '2022-03-17T06:31:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(4, 2, null, '2022-03-18T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(5, 2, null, '2022-03-18T06:31:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(6, 2, null, '2022-03-19T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1);