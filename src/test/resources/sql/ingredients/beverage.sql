insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(10, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Bear (1)", "Bear", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 10);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(11, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Пюре карт (р)", "Пюре карт (р)", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 11),
(12, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 12),
(13, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 13);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce, is_weight,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(14, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Картофель", "Картофель", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 14),
(15, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Молоко", "Молоко", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 15),
(16, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Масло сл", "Масло сл", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16),
(17, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000', B'000000',
"Bear", "Bear", "2012-02-02T12:00:00", 3, "LITER", "NONE", B'0', 17);

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 17);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(1, 1, '2022-02-02', 0.3, 'NET'),
(2, 1, '2022-02-02', 0.3, 'GROSS'),
(3, 1, '2022-02-02', 1, 'ENABLE');