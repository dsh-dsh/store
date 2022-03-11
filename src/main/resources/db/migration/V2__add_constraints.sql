alter table dinners add constraint UK_in_dinners_id unique (in_dinners_id);

alter table users add constraint UK_email unique (email);

alter table account add constraint FK_account_company_id foreign key (company_id) references company (id);

alter table dinners add constraint FK_in_dinners_id foreign key (in_dinners_id) references item (id);

alter table dinners add constraint FK_dinners_item_id foreign key (item_id) references item (id);

alter table document add constraint FK_author_id foreign key (author_id) references users (id);

alter table document add constraint FK_individual_id foreign key (individual_id) references users (id);

alter table document add constraint FK_project_id foreign key (project_id) references project (id);

alter table document add constraint FK_company_id foreign key (company_id) references company (id);

alter table document add constraint FK_recipient_id foreign key (recipient_id) references company (id);

alter table document add constraint FK_storage_to_id foreign key (storage_to_id) references storage (id);

alter table document add constraint FK_storage_from_id foreign key (storage_from_id) references storage (id);

alter table document add constraint FK_supplier_id foreign key (supplier_id) references company (id);

alter table document_item add constraint FK_item_id foreign key (item_id) references item (id);

alter table document_item add constraint FK_document_id foreign key (document_id) references document (id);

alter table price add constraint FK_price_item_id foreign key (item_id) references item (id);