insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, amount, tax, payment_type, is_deleted, d_type)
values
(1, 2, null, '2022-03-16 11:00:00.000', 'POSTING_DOC', null, B'0', B'1', 1, 3, null, 1, null, 3, 0.00, 0.00, '', B'0', 1),
(2, 2, null, '2022-03-17 11:00:00.000', 'POSTING_DOC', null, B'0', B'1', 2, 3, null, 1, null, 3, 0.00, 0.00, '', B'0', 1),
(3, 2, null, '2022-03-20T11:00:00.000', 'WRITE_OFF_DOC', null, B'0', B'1', 3, 3, 1, 1, 3, null, 0.00, 0.00, '', B'0', 1),
(4, 2, null, '2022-03-29T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 4, 3, 1, 1, 3, 2, 0.00, 0.00, '', B'0', 1),
(5, 2, null, '2022-03-30T11:00:00.000', 'MOVEMENT_DOC', null, B'0', B'1', 5, 3, 1, 1, 3, 1, 0.00, 0.00, '', B'0', 1),
(6, 2, null, '2022-03-30T12:00:00.000', 'CREDIT_ORDER_DOC', 1, B'0', B'1', 6, 3, 1, 1, null, null, 0.00, 0.00, 'SALE_CASH_PAYMENT', B'0', 2),
(7, 2, null, '2022-03-30T13:00:00.000', 'WITHDRAW_ORDER_DOC', 1, B'0', B'1', 7, 3, 1, 1, null, null, 2000.00, 0.00, 'SALARY_PAYMENT', B'0', 2);

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