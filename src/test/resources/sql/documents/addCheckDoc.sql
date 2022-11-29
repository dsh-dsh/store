insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values (1, 2, null, '2022-03-16T06:30:36.395', 'CHECK_DOC', 1, B'1', B'1', 1, 3, 1, null, 3, null, B'0', 1);

insert into check_kkm_info
(id, amount_received, cash_register_number, check_id, check_number, date_time, guest_number, is_delivery,
check_payment_type, is_kkm_checked, is_payed, is_payed_by_card, is_return, table_number, waiter)
values (1, 1000.0, 63214823871, 1, 65469, '2022-03-16T06:30:36.400402400', 1, B'1', 'CARD_PAYMENT', B'1', B'1', B'1', B'1', 12, 'Официант 10');

insert into document_item (discount, item_id, document_id, price, d_quantity, quantity_fact) values (0.0, 1, 1, 0.0, 1.0, 0.0);

insert into document_item (discount, item_id, document_id, price, d_quantity, quantity_fact) values (0.0, 2, 1, 0.0, 2.0, 0.0);

insert into document_item (discount, item_id, document_id, price, d_quantity, quantity_fact) values (0.0, 3, 1, 0.0, 3.0, 0.0);