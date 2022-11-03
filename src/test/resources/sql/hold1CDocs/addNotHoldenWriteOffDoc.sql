insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(3, 2, null, '2022-04-18T11:00:00.000', 'WRITE_OFF_DOC', null, B'0', B'1', 1, 3, 1, 1, 3, null, B'0', 1),
(4, 2, null, '2022-04-19T11:00:00.000', 'WRITE_OFF_DOC', null, B'0', B'1', 1, 3, 1, 1, 3, null, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(5, 0.0, 7, 3, 0.00, 30.0, 0.0),
(6, 0.0, 8, 4, 0.00, 50.0, 0.0);