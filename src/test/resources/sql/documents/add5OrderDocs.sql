insert into document
(id, author_id, base_document_id, date_time, doc_type, payment_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values
(11, 2, null, '2022-04-15T01:00:00.006', 'CREDIT_ORDER_DOC', 'COST_PAYMENT', 1, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1000.00, 0.0, 2),
(12, 2, null, '2022-04-15T01:00:00.007', 'CREDIT_ORDER_DOC', 'SALARY_PAYMENT', 3, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1100.00, 0.0, 2),
(13, 2, null, '2022-04-15T01:00:00.008', 'CREDIT_ORDER_DOC', 'SALARY_PAYMENT', 2, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1200.00, 0.0, 2),
(14, 2, null, '2022-04-16T01:00:00.011', 'CREDIT_ORDER_DOC', 'SALARY_PAYMENT', 4, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1300.00, 0.0, 2),
(15, 2, null, '2022-04-16T01:00:00.012', 'CREDIT_ORDER_DOC', 'SALARY_PAYMENT', 3, B'1', B'1', 6, 3, 1, null, null, null, B'0', 1400.00, 0.0, 2);