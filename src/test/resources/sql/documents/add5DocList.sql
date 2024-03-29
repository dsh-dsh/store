insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (1, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into check_kkm_info
(amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
is_kkm_checked, is_payed, is_return, table_number, waiter)
values (1000.0, 63214823871, 1, 65469, '2022-03-16T07:30:36.395', 1, B'1', B'1', B'1', B'1', 12, 'Официант 10');

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 1, 1, 0.0, 1.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 2, 1, 0.0, 2.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 3, 1, 0.0, 3.0, 0.0);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (2, 2, null, '2022-03-16T08:30:36.395', 'INVENTORY_DOC', null, B'1', B'1', 2, 3, 1, 1, null, 3, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 2, 2, 200.00, 1.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 3, 2, 100.00, 2.0, 0.0);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (3, 2, null, '2022-03-16T09:30:36.395', 'POSTING_DOC', null, B'1', B'1', 3, 3, null, 1, null, 3, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 2, 3, 200.00, 1.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 3, 3, 100.00, 2.0, 0.0);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (4, 2, null, '2022-03-16T10:30:36.395', 'RECEIPT_DOC', null, B'1', B'1', 4, 3, 1, 1, null, 3, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 2, 4, 200.00, 1.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 3, 4, 100.00, 2.0, 0.0);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (5, 2, null, '2022-03-16T11:30:36.395', 'REQUEST_DOC', null, B'1', B'1', 11, 3, 1, 1, null, 3, B'0', 1);

insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 2, 5, 200.00, 1.0, 0.0);
insert into document_item (discount, item_id, document_id, price, quantity, quantity_fact) values (0.0, 3, 5, 100.00, 2.0, 0.0);