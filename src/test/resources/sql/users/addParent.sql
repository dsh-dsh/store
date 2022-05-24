insert into users
(id, birth_date, email, first_name, last_name, password, phone, reg_time, role, parent_id)
values
(5, "2001-05-24T00:00:00", "parent@mail.ru", "Сотрудники", "", "", "", "2012-03-03T12:00:00", "NONE", 0);

update users set parent_id = 5 where id < 5;