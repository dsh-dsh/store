insert into project
(name)
values
(" "),
("Жаровня 1"),
("Жаровня 2"),
("Жаровня 3");

insert into users
(birth_date, email, first_name, last_name, password, phone, reg_time, role)
values
("2001-03-03T00:00:00", "customer@mail.ru", "Иван", "Иванов", "$2y$12$NKArmf9agtEQw7rPDN4zb.rE90zeewGAUWNRkSrYW662FwL77NyCS", "89180165010", "2012-03-03T12:00:00", "CUSTOMER"),
("2001-03-03T00:00:00", "cashier@mail.ru", "Сидор", "Сидоров", "$2y$12$NKArmf9agtEQw7rPDN4zb.rE90zeewGAUWNRkSrYW662FwL77NyCS", "89180165020", "2012-03-03T13:00:00", "CASHIER"),
("2001-03-03T00:00:00", "accountant@mail.ru", "Ольга", "Олегова", "$2y$12$NKArmf9agtEQw7rPDN4zb.rE90zeewGAUWNRkSrYW662FwL77NyCS", "89180165030", "2012-03-03T14:00:00", "ACCOUNTANT"),
("2001-03-03T00:00:00", "admin@mail.ru", "Василий", "Васильев", "$2y$12$NKArmf9agtEQw7rPDN4zb.rE90zeewGAUWNRkSrYW662FwL77NyCS", "89180165040", "2012-03-03T15:00:00", "ADMIN");

insert into storage
(name, type)
values
("Склад", "STORE_STORE"),
("Жаровня 1", "CAFE_STORE"),
("Жаровня 3", "CAFE_STORE"),
("Жаровня 4", "CAFE_STORE");

insert into company
(inn, is_mine, kpp, name)
values
("230902612219", B'000001', 0, "ИП Шипилов М.В."),
("230000000001", B'000000', "230501001", 'ООО "Защита"'),
("230000000002", B'000000', "230502001", 'ООО "Работа"');

insert into price
(price_date, price_value, item_id, price_type)
values
("2012-03-03", 200.00, 4, 'RETAIL'),
("2012-03-03", 180.00, 5, 'RETAIL'),
("2012-03-03", 250.00, 4, 'DELIVERY'),
("2012-03-03", 210.00, 5, 'DELIVERY'),
("2012-03-03", 80.00, 6, 'RETAIL');

insert into sets
(item_id, set_id, number)
values
(4, 9, 2),
(5, 9, 2),
(6, 9, 2);