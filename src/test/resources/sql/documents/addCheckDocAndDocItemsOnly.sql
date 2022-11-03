insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (7, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(6, 0.0, 1, 7, 50.0, 1.0, 0.0),
(7, 0.0, 2, 7, 100.0, 2.0, 0.0),
(8, 0.0, 3, 7, 150.0, 3.0, 0.0);