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
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 13),
(14, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Украшение тарелки", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 10);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(15, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 14),
(16, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 15),
(17, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16),
(18, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 17),
(19, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Помидоры", "Помидоры", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16),
(20, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Огурцы", "Огурцы", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 17);

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 11),
(2, 10, 12),
(3, 10, 14),
(4, 11, 13),
(5, 12, 15),
(6, 12, 16),
(7, 13, 17),
(8, 13, 18),
(9, 14, 19),
(10, 14, 20);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(1, 1, '2022-02-02', 0.15, 'NET'),
(2, 1, '2022-02-02', 0.15, 'GROSS'),
(3, 1, '2022-02-02', 1, 'ENABLE'),
(4, 2, '2022-02-02', 0.15, 'NET'),
(5, 2, '2022-02-02', 0.15, 'GROSS'),
(6, 2, '2022-02-02', 1, 'ENABLE'),
(7, 3, '2022-02-02', 1, 'NET'),
(8, 3, '2022-02-02', 1, 'GROSS'),
(9, 3, '2022-02-02', 1, 'ENABLE'),
(10, 4, '2022-02-02', 0.9, 'NET'),
(11, 4, '2022-02-02', 1.2, 'GROSS'),
(12, 4, '2022-02-02', 1, 'ENABLE'),
(13, 5, '2022-02-02', 0.6, 'NET'),
(14, 5, '2022-02-02', 0.7, 'GROSS'),
(15, 5, '2022-02-02', 1, 'ENABLE'),
(16, 6, '2022-02-02', 0.9, 'NET'),
(17, 6, '2022-02-02', 1.2, 'GROSS'),
(18, 6, '2022-02-02', 1, 'ENABLE'),
(19, 7, '2022-02-02', 0.3, 'NET'),
(20, 7, '2022-02-02', 0.4, 'GROSS'),
(21, 7, '2022-02-02', 1, 'ENABLE'),
(22, 8, '2022-02-02', 0.5, 'NET'),
(23, 8, '2022-02-02', 0.7, 'GROSS'),
(24, 8, '2022-02-02', 1, 'ENABLE'),
(25, 9, '2022-02-02', 0.015, 'NET'),
(26, 9, '2022-02-02', 0.02, 'GROSS'),
(27, 9, '2022-02-02', 1, 'ENABLE'),
(28, 10, '2022-02-02', 0.02, 'NET'),
(29, 10, '2022-02-02', 0.03, 'GROSS'),
(30, 10, '2022-02-02', 1, 'ENABLE');