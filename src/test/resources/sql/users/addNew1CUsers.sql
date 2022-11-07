insert into users
(birth_date, email, first_name, last_name, password, phone, reg_time, role, is_node, code, parent_id)
values
("2001-03-03T00:00:00", "new_user@mail.ru", "Иван", "Иванов",
"$2y$12$NKArmf9agtEQw7rPDN4zb.rE90zeewGAUWNRkSrYW662FwL77NyCS",
"89180165010", "2012-03-03T12:00:00", "NONE", B'0', 123, 1),
("2001-03-03T00:00:00", "new_parent_user@mail.ru", "", "parent", "",
"", "2012-03-03T12:00:00", "NONE", b'1', 321, 0);