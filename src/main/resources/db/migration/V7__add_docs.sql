insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(101, 2, null, '2022-05-01T11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 1, 3, null, 1, null, 3, B'0', 1),
(102, 2, null, '2022-05-02T11:00:00.000', 'POSTING_DOC', null, B'1', B'1', 2, 3, null, 1, null, 3, B'0', 1),
(103, 2, null, '2022-05-03T11:00:00.000', 'WRITE_OFF_DOC', null, B'0', B'1', 3, 3, 1, 1, 3, null, B'0', 1),
(104, 2, null, '2022-05-04T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 4, 3, 1, 1, 3, 2, B'0', 1),
(105, 2, null, '2022-05-05T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 5, 3, 1, 1, 3, 1, B'0', 1);

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(101, 0.0, 7, 101, 100.00, 10.0, 0.0),
(102, 0.0, 8, 101, 200.00, 10.0, 0.0),

(103, 0.0, 7, 102, 120.00, 10.0, 0.0),
(104, 0.0, 8, 102, 240.00, 10.0, 0.0),

(105, 0.0, 7, 103, 0.00, 5.0, 0.0),
(106, 0.0, 8, 103, 0.00, 5.0, 0.0),

(107, 0.0, 7, 104, 0.00, 2.0, 0.0),
(108, 0.0, 8, 104, 0.00, 2.0, 0.0),

(109, 0.0, 8, 105, 0.00, 2.0, 0.0),
(110, 0.0, 8, 105, 0.00, 2.0, 0.0);

insert into document
(id, author_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, amount, tax, payment_type, is_deleted, d_type)
values
(106, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(107, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(108, 2, '2022-03-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(109, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(110, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(111, 2, '2022-03-16T06:30:36.395', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 1, 3, 1, null, 2000.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2);

insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(112, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(113, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1),
(114, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'0', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into check_kkm_info
(id, amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
is_kkm_checked, is_payed, is_payed_by_card, is_return, table_number, waiter)
values
(112, 1000.0, 63214823871, 112, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(113, 1000.0, 63214823871, 113, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'1', B'1', 12, 'Официант 10'),
(114, 1000.0, 63214823871, 114, 65469, '2022-03-16T06:30:36.395', 1, B'1', B'1', B'1', B'0', B'1', 12, 'Официант 10');

insert into document_item
(id, discount, item_id, document_id, price, quantity, quantity_fact)
values
(111, 0.0, 4, 112, 120.0, 1.0, 0.0),
(112, 0.0, 5, 112, 150.0, 1.0, 0.0),
(113, 0.0, 6, 112, 180.0, 1.0, 0.0),
(114, 0.0, 4, 113, 120.0, 1.0, 0.0),
(115, 0.0, 5, 113, 150.0, 1.0, 0.0),
(116, 0.0, 9, 113, 200.0, 1.0, 0.0),
(117, 0.0, 4, 114, 120.0, 2.0, 0.0),
(118, 0.0, 9, 114, 200.0, 2.0, 0.0),
(119, 0.0, 5, 114, 150.0, 2.0, 0.0);