insert into document
(id, author_id, base_document_id, date_time, doc_type, individual_id, is_hold, is_payed, number, project_id,
supplier_id, recipient_id, storage_from_id, storage_to_id, is_deleted, d_type)
values
(1, 2, null, '2022-03-16T06:30:36.395', 'POSTING_DOC', null, B'1', B'1', 1, 3, null, 1, null, 3, B'0', 1),
(2, 2, null, '2022-03-16T06:30:36.396', 'CHECK_DOC', null, B'1', B'1', 1, 3, null, 1, null, 3, B'0', 1);

insert into doc_info
(id, comment, supplier_doc_number, document_id)
values (1, "comment", "SUP_NUM 123456789", 1);