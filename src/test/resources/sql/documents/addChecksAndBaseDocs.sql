insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, 5, '2022-04-16T01:00:00.001', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(2, 2, 5, '2022-04-16T01:00:00.002', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(3, 2, 5, '2022-04-16T01:00:00.003', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(4, 2, 5, '2022-04-16T01:00:00.004', 'POSTING_DOC', null, B'1', B'1', 1, 3, 1, 1, null, 3, B'0', 1),
(5, 2, null, '2022-04-16T01:00:00.005', 'WRITE_OFF_DOC', null, B'1', B'1', 1, 3, 1, 1, 3, null, B'0', 1);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values
(6, 2, 5, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1000.00, 0.0, 2);

insert into check_kkm_info
(id, amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
is_kkm_checked, is_payed, is_payed_by_card, is_return, table_number, waiter)
values
(1, 1000.0, 63214823871, 1, 65469, '2022-04-16T01:00:00.004', 1, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(2, 1000.0, 63214823871, 2, 65469, '2022-04-16T01:00:00.004', 2, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(3, 1000.0, 63214823871, 3, 65469, '2022-04-16T01:00:00.004', 3, B'1', B'1', B'1', B'0', B'1', 12, 'Официант 10');

insert into document_item
(id, discount, item_id, document_id, price, d_quantity, quantity_fact)
values
(1, 0.0, 4, 1, 120.0, 1.0, 0.0),
(2, 0.0, 5, 1, 150.0, 1.0, 0.0),
(3, 0.0, 6, 1, 180.0, 1.0, 0.0),
(4, 0.0, 4, 2, 120.0, 1.0, 0.0),
(5, 0.0, 5, 2, 150.0, 1.0, 0.0),
(6, 0.0, 9, 2, 200.0, 1.0, 0.0),
(7, 0.0, 4, 3, 120.0, 2.0, 0.0),
(8, 0.0, 9, 3, 200.0, 2.0, 0.0),
(9, 0.0, 5, 3, 150.0, 2.0, 0.0),
(10, 0.0, 2, 4, 200.00, 1.0, 0.0),
(11, 0.0, 2, 4, 200.00, 1.0, 0.0);