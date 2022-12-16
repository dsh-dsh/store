insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(4, 2, null, '2022-03-16T06:30:36.395', 'RECEIPT_DOC', null, B'1', B'1', 332, 3, null, 1, null, 3, B'0', 1),
(5, 2, null, '2022-03-16T06:30:36.395', 'WRITE_OFF_DOC', null, B'1', B'1', 221, 3, null, 1, null, 3, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact)
values
(0.0, 2, 4, 200.00, 1.0, 0.0),
(0.0, 3, 4, 100.00, 2.0, 0.0),
(0.0, 2, 5, 200.00, 1.0, 0.0),
(0.0, 3, 5, 100.00, 2.0, 0.0);