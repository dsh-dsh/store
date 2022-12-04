insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-01T06:30:36.395', 'POSTING_DOC', null, B'0', B'1', 1, 3, 1, 2, null, 3, B'0', 1),
(2, 2, null, '2022-03-12T06:30:36.395', 'POSTING_DOC', null, B'0', B'0', 1, 3, 1, 2, null, 3, B'0', 1),
(3, 2, null, '2022-03-13T06:30:36.395', 'POSTING_DOC', null, B'0', B'0', 1, 3, 1, 2, null, 3, B'0', 1),
(4, 2, null, '2022-03-14T06:30:36.395', 'POSTING_DOC', null, B'0', B'0', 1, 3, 2, 1, null, 3, B'0', 1),
(5, 2, null, '2022-03-15T06:30:36.395', 'POSTING_DOC', null, B'0', B'0', 1, 3, 3, 2, null, 3, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(1, 0.0, 2, 1, 100.00, 1.0, 0.0),
(2, 0.0, 3, 1, 200.00, 1.0, 0.0),
(3, 0.0, 2, 2, 300.00, 1.0, 0.0),
(4, 0.0, 3, 2, 400.00, 1.0, 0.0),
(5, 0.0, 3, 2, 500.00, 1.0, 0.0);