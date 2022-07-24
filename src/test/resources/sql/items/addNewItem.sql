insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight, name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(10, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000', "Новое блюдо", "Новое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 444);

insert into price
(price_date, price_value, item_id, price_type)
values
("2022-01-01", 100.00, 10, 'RETAIL'),
("2022-01-15", 150.00, 10, 'DELIVERY'),
("2022-03-01", 200.00, 10, 'RETAIL'),
("2022-03-01", 250.00, 10, 'DELIVERY');

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 6),
(2, 10, 8);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(1, 1, '2022-03-01', 0.25, 'NET'),
(2, 1, '2022-03-01', 0.2, 'GROSS'),
(3, 2, '2022-03-01', 0.35, 'NET'),
(4, 2, '2022-03-01', 0.3, 'GROSS');

insert into sets
(item_id, set_id)
values
(10, 9);