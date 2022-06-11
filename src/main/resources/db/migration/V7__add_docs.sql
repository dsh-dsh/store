insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-05-01T11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 1, 3, null, 1, null, 3, B'0', 1),
(2, 2, null, '2022-05-02T11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 2, 3, null, 1, null, 3, B'0', 1),
(3, 2, null, '2022-05-03T11:00:00.000', 'WRITE_OFF_DOC', null, B'0', B'1', 3, 3, 1, 1, 3, null, B'0', 1),
(4, 2, null, '2022-05-04T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 4, 3, 1, 1, 3, 2, B'0', 1),
(5, 2, null, '2022-05-05T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 5, 3, 1, 1, 3, 1, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(1, 0.0, 7, 1, 100.00, 10.0, 0.0),
(2, 0.0, 8, 1, 200.00, 10.0, 0.0),

(3, 0.0, 7, 2, 120.00, 10.0, 0.0),
(4, 0.0, 8, 2, 240.00, 10.0, 0.0),

(5, 0.0, 7, 3, 0.00, 5.0, 0.0),
(6, 0.0, 8, 3, 0.00, 5.0, 0.0),

(7, 0.0, 7, 4, 0.00, 2.0, 0.0),
(8, 0.0, 8, 4, 0.00, 2.0, 0.0),

(9, 0.0, 8, 5, 0.00, 2.0, 0.0),
(10, 0.0, 8, 5, 0.00, 2.0, 0.0);

insert into document
(id, author_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, amount, tax, payment_type, is_deleted, d_type)
values
(6, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(7, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(8, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(9, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(10, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(11, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(12, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(13, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(14, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into check_kkm_info
(id, amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
is_kkm_checked, is_payed, is_payed_by_card, is_return, table_number, waiter)
values
(1, 1000.0, 63214823871, 12, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(2, 1000.0, 63214823871, 13, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(3, 1000.0, 63214823871, 14, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'0', B'1', 12, 'Официант 10');

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(11, 0.0, 4, 12, 120.0, 1.0, 0.0),
(12, 0.0, 5, 12, 150.0, 1.0, 0.0),
(13, 0.0, 6, 12, 180.0, 1.0, 0.0),
(14, 0.0, 4, 13, 120.0, 1.0, 0.0),
(15, 0.0, 5, 13, 150.0, 1.0, 0.0),
(16, 0.0, 9, 13, 200.0, 1.0, 0.0),
(17, 0.0, 4, 14, 120.0, 2.0, 0.0),
(18, 0.0, 9, 14, 200.0, 2.0, 0.0),
(19, 0.0, 5, 14, 150.0, 2.0, 0.0);