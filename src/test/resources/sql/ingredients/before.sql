insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(10, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 10);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(11, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 11),
(12, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 12),
(13, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 13);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(14, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 14),
(15, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 15),
(16, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16),
(17, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 17);

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 11),
(2, 10, 12),
(3, 11, 13),
(4, 12, 14),
(5, 12, 15),
(6, 13, 16),
(7, 13, 17);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(1, 1, '2022-02-02', 1, 'NET'),
(2, 1, '2022-02-02', 1, 'GROSS'),
(3, 2, '2022-02-02', 2, 'NET'),
(4, 2, '2022-02-02', 1, 'GROSS'),
(5, 3, '2022-02-02', 1.2, 'NET'),
(6, 3, '2022-02-02', 1, 'GROSS'),
(7, 4, '2022-02-02', 1.5, 'NET'),
(8, 4, '2022-02-02', 1, 'GROSS'),
(9, 5, '2022-02-02', 1.8, 'NET'),
(10, 5, '2022-02-02', 1, 'GROSS'),
(11, 6, '2022-02-02', 2, 'NET'),
(12, 6, '2022-02-02', 1, 'GROSS'),
(13, 7, '2022-02-02', 2, 'NET'),
(14, 7, '2022-02-02', 1, 'GROSS');