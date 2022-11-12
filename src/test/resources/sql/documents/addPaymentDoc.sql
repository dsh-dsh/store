insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, amount, tax, d_type)
values (2, 2, null, '2022-10-16T06:30:36.395', 'CREDIT_ORDER_DOC', 1, B'0', B'0', 2, 3, null, 2, null, null, B'0', 400.00, 0.0, 2);

update document set base_document_id = 2, is_payed = B'1' where id = 1;