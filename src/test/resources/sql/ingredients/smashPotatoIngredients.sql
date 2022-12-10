insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(10, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Пюре (1)", "Пюре", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 10);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(11, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Пюре карт (р)", "Пюре карт (р)", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 11),
(12, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 12),
(13, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 13);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(14, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Картофель", "Картофель", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 14),
(15, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Молоко", "Молоко", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 15),
(16, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Масло сл", "Масло сл", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16),
(17, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 17);

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 11),
(2, 11, 14),
(3, 11, 15),
(4, 11, 16);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(1, 1, '2022-02-02', 0.2, 'NET'),
(2, 1, '2022-02-02', 0.2, 'GROSS'),
(3, 1, '2022-02-02', 1, 'ENABLE'),
(4, 2, '2022-02-02', 4, 'NET'),
(5, 2, '2022-02-02', 5, 'GROSS'),
(6, 2, '2022-02-02', 1, 'ENABLE'),
(7, 3, '2022-02-02', 0.8, 'NET'),
(8, 3, '2022-02-02', 1.2, 'GROSS'),
(9, 3, '2022-02-02', 1, 'ENABLE'),
(10, 4, '2022-02-02', 0.3, 'NET'),
(11, 4, '2022-02-02', 0.4, 'GROSS'),
(12, 4, '2022-02-02', 1, 'ENABLE');