create table if not exists doc_info
(id integer not null auto_increment, comment varchar(255), supplier_doc_number varchar(255),
document_id integer, primary key (id));

alter table doc_info add constraint FK_doc_info_doc_id foreign key (document_id) references document (id);