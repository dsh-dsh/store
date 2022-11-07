insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(18, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 18);

insert into ingredient
(id, parent_id, child_id)
values
(8, 18, 10),
(9, 18, 10);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(22, 8, '2022-02-02', 1, 'NET'),
(23, 8, '2022-02-02', 1, 'GROSS'),
(24, 8, '2022-02-02', 1, 'ENABLE'),
(25, 9, '2022-02-02', 1, 'NET'),
(26, 9, '2022-02-02', 1, 'GROSS'),
(27, 9, '2022-02-02', 1, 'ENABLE');