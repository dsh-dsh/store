insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T06:30:36.395', 'POSTING_DOC', null, B'1', B'0', 1, 3, 2, 1, null, 3, B'0', 1),
(2, 2, null, '2022-03-20T06:30:36.395', 'POSTING_DOC', null, B'1', B'0', 1, 3, 2, 1, null, 3, B'0', 1),
(3, 2, null, '2022-03-25T06:30:36.395', 'POSTING_DOC', null, B'1', B'0', 1, 3, 2, 1, null, 3, B'0', 1);

insert into document_item (id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(1, 0.0, 2, 1, 200.00, 1.0, 0.0),
(2, 0.0, 3, 1, 300.00, 2.0, 0.0),
(3, 0.0, 2, 2, 400.00, 1.0, 0.0),
(4, 0.0, 3, 2, 500.00, 2.0, 0.0),
(5, 0.0, 2, 3, 600.00, 1.0, 0.0),
(6, 0.0, 3, 3, 700.00, 2.0, 0.0);