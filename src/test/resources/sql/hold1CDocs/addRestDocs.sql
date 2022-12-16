--приход на 3 склад четырех ингредиентов по 10 кг
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(4, 2, null, '2022-03-01T11:00:00.000', 'RECEIPT_DOC', null, B'1', B'1', 1, 3, 1, 1, null, 3, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(10, 0.0, 15, 4, 150.00, 10.0, 0.0),
(11, 0.0, 16, 4, 150.00, 10.0, 0.0),
(12, 0.0, 17, 4, 150.00, 10.0, 0.0),
(13, 0.0, 18, 4, 150.00, 10.0, 0.0);