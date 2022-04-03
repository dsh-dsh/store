--приход 10 кг картофеля фри по 50.00 на третий склад
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (1, 2, null, '2022-03-01T11:00:00.000', 'RECEIPT_DOC', null, B'1', B'1', 1, 3, 1, 1, null, 3, B'0', 1);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 8, 1, 50.00, 10.0, 0.0);

--приход 10 кг картофеля фри по 60.00 на третий склад
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (2, 2, null, '2022-03-10T11:00:00.000', 'RECEIPT_DOC', null, B'1', B'1', 1, 3, 1, 1, null, 3, B'0', 1);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 8, 2, 0.00, 10.0, 0.0);

--списание 3 кг картофеля фри с третьего склад
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (3, 2, null, '2022-03-05T11:00:00.000', 'WRITE_OFF_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, null, B'0', 1);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 8, 3, 0.00, 3.0, 0.0);

--списание 3 кг картофеля фри с третьего склад
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (4, 2, null, '2022-03-08T11:00:00.000', 'WRITE_OFF_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, null, B'0', 1);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 8, 4, 0.00, 3.0, 0.0);

--перемещение 10 кг картофеля фри с 3 на 2 склад
insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id, supplier_id,
recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (5, 2, null, '2022-03-15T11:00:00.000', 'MOVEMENT_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, 3, B'0', 1);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 8, 5, 0.00, 10.0, 0.0);