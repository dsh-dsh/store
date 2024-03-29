insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-04-15T01:00:00.001', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(2, 2, null, '2022-04-15T01:00:00.002', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(3, 2, null, '2022-04-15T01:00:00.003', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(4, 2, null, '2022-04-15T01:00:00.004', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(5, 2, null, '2022-04-15T01:00:00.005', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(6, 2, null, '2022-04-16T01:00:00.006', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(7, 2, null, '2022-04-16T01:00:00.007', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(8, 2, null, '2022-04-16T01:00:00.008', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(9, 2, null, '2022-04-16T01:00:00.009', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(10, 2, null, '2022-04-16T01:00:00.0010', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into check_kkm_info
(amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
check_payment_type, is_kkm_checked, is_payed, is_return, table_number, waiter)
values
(1000.0, 63214823871, 1, 65469, '2022-03-16T06:30:36.395', 1, B'1', 'DELIVERY_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 2, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'CARD_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 3, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'CASH_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 4, 65469, '2022-03-16T06:30:36.395', 1, B'1', 'DELIVERY_PAYMENT', B'1', B'0', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 5, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'QR_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 6, 65469, '2022-03-16T06:30:36.395', 1, B'1', 'DELIVERY_PAYMENT', B'1', B'0', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 7, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'CARD_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 8, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'CARD_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 9, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'QR_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10'),
(1000.0, 63214823871, 10, 65469, '2022-03-16T06:30:36.395', 1, B'0', 'CASH_PAYMENT', B'1', B'1', B'1', 12, 'Официант 10');

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(1, 0.0, 4, 1, 120.0, 1.0, 0.0),
(2, 0.0, 5, 2, 150.0, 1.0, 0.0),
(3, 0.0, 6, 3, 180.0, 1.0, 0.0),
(4, 0.0, 4, 4, 120.0, 1.0, 0.0),
(5, 0.0, 5, 5, 150.0, 1.0, 0.0),
(6, 0.0, 9, 6, 200.0, 1.0, 0.0),
(7, 0.0, 4, 7, 120.0, 2.0, 0.0),
(8, 0.0, 9, 8, 200.0, 2.0, 0.0),
(9, 0.0, 5, 9, 150.0, 2.0, 0.0),
(10, 0.0, 5, 10, 150.0, 2.0, 0.0);