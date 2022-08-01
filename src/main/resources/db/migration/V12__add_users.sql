insert into users
(id, birth_date, email, first_name, last_name, password, phone, reg_time, role, is_node, parent_id)
values
(5, "2001-03-03T00:00:00", "imployers@mail.ru", " ", "Сотрудники", "", "", "2012-03-03T12:00:00", "NONE", B'1', 0),
(6, "2001-03-03T00:00:00", "system@user.com", "system", "user", "", "", "2012-03-03T12:00:00", "NONE", B'0', 0);

update users set parent_id = 5 where id < 5;