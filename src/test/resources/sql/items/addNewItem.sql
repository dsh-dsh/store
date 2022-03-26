insert into item
(id, is_alcohol, is_garnish, is_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight, name, print_name, reg_time, parent_id, unit, workshop)
values
(10, B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000', "Новое блюдо", "Новое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN");


insert into price
(price_date, price_value, item_id, price_type)
values
("2022-01-01", 100.00, 10, 'RETAIL'),
("2022-01-15", 150.00, 10, 'DELIVERY'),
("2022-03-01", 300.00, 10, 'RETAIL'),
("2022-03-01", 350.00, 10, 'DELIVERY');