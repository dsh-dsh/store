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
"Пюре карт (р)", "Пюре карт (р)", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 11);


insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(12, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Картофель", "Картофель", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 14),
(13, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Молоко", "Молоко", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 15),
(14, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Масло сл", "Масло сл", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 16);

insert into ingredient
(id, parent_id, child_id)
values
(1, 10, 11),
(2, 11, 12),
(3, 11, 13),
(4, 11, 14);

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

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(15, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 15);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(16, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 16),
(17, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 17),
(18, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 19);


insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(19, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 19),
(20, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 20),
(21, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 21),
(22, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 22);


insert into ingredient
(id, parent_id, child_id)
values
(5, 15, 16),
(6, 15, 17),
(7, 16, 18),
(8, 17, 19),
(9, 17, 20),
(10, 18, 21),
(11, 18, 22);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(13, 5, '2022-02-02', 0.5, 'NET'),
(14, 5, '2022-02-02', 0.5, 'GROSS'),
(15, 5, '2022-02-02', 1, 'ENABLE'),
(16, 6, '2022-02-02', 1, 'NET'),
(17, 6, '2022-02-02', 2, 'GROSS'),
(18, 6, '2022-02-02', 1, 'ENABLE'),
(19, 7, '2022-02-02', 1, 'NET'),
(20, 7, '2022-02-02', 1.2, 'GROSS'),
(21, 7, '2022-02-02', 1, 'ENABLE'),
(22, 8, '2022-02-02', 1, 'NET'),
(23, 8, '2022-02-02', 1.5, 'GROSS'),
(24, 8, '2022-02-02', 1, 'ENABLE'),
(25, 9, '2022-02-02', 1, 'NET'),
(26, 9, '2022-02-02', 1.8, 'GROSS'),
(27, 9, '2022-02-02', 1, 'ENABLE'),
(28, 10, '2022-02-02', 1, 'NET'),
(29, 10, '2022-02-02', 2, 'GROSS'),
(30, 10, '2022-02-02', 1, 'ENABLE'),
(31, 11, '2022-02-02', 1, 'NET'),
(32, 11, '2022-02-02', 2, 'GROSS'),
(33, 11, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(23, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Beer (1)", "Beer", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 23),
(24, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Bear", "Bear", "2012-02-02T12:00:00", 3, "LITER", "NONE", B'0', 24);

insert into ingredient
(id, parent_id, child_id)
values
(12, 23, 24);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(34, 12, '2022-02-02', 0.3, 'NET'),
(35, 12, '2022-02-02', 0.3, 'GROSS'),
(36, 12, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(25, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Beer (1)", "Beer", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 25),
(26, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Beer bottle", "Beer bottle", "2012-02-02T12:00:00", 2, "PIECE", "NONE", B'0', 26);

insert into ingredient
(id, parent_id, child_id)
values
(13, 25, 26);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(37, 13, '2022-02-02', 1, 'NET'),
(38, 13, '2022-02-02', 1, 'GROSS'),
(39, 13, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(27, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 27);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(28, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 28),
(29, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 29),
(30, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 30);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(31, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 31),
(32, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 32),
(33, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 33),
(34, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 34);

insert into ingredient
(id, parent_id, child_id)
values
(14, 27, 28),
(15, 27, 29),
(16, 28, 30),
(17, 29, 31),
(18, 29, 32),
(19, 30, 33),
(20, 30, 34);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(40, 14, '2022-02-02', 0.2, 'NET'),
(41, 14, '2022-02-02', 0.3, 'GROSS'),
(42, 14, '2022-02-02', 1, 'ENABLE'),
(43, 15, '2022-02-02', 0.3, 'NET'),
(44, 15, '2022-02-02', 0.4, 'GROSS'),
(45, 15, '2022-02-02', 1, 'ENABLE'),
(46, 16, '2022-02-02', 0.5, 'NET'),
(47, 16, '2022-02-02', 1.2, 'GROSS'),
(48, 16, '2022-02-02', 1, 'ENABLE'),
(49, 17, '2022-02-02', 0.6, 'NET'),
(50, 17, '2022-02-02', 0.7, 'GROSS'),
(51, 17, '2022-02-02', 1, 'ENABLE'),
(52, 18, '2022-02-02', 0.9, 'NET'),
(53, 18, '2022-02-02', 1.2, 'GROSS'),
(54, 18, '2022-02-02', 1, 'ENABLE'),
(55, 19, '2022-02-02', 0.3, 'NET'),
(56, 19, '2022-02-02', 0.5, 'GROSS'),
(57, 19, '2022-02-02', 1, 'ENABLE'),
(58, 20, '2022-02-02', 0.5, 'NET'),
(59, 20, '2022-02-02', 0.6, 'GROSS'),
(60, 20, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(35, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 35);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(36, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 36),
(37, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 37),
(38, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 38),
(39, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Украшение тарелки", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 39);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(40, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 40),
(41, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 41),
(42, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 42),
(43, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 43),
(44, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Помидоры", "Помидоры", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 44),
(45, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Огурцы", "Огурцы", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 45);

insert into ingredient
(id, parent_id, child_id)
values
(21, 35, 36),
(22, 35, 37),
(23, 35, 39),
(24, 36, 38),
(25, 37, 40),
(26, 37, 41),
(27, 38, 42),
(28, 38, 43),
(29, 39, 44),
(30, 39, 45);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(61, 21, '2022-02-02', 0.15, 'NET'),
(62, 21, '2022-02-02', 0.15, 'GROSS'),
(63, 21, '2022-02-02', 1, 'ENABLE'),
(64, 22, '2022-02-02', 0.15, 'NET'),
(65, 22, '2022-02-02', 0.15, 'GROSS'),
(66, 22, '2022-02-02', 1, 'ENABLE'),
(67, 23, '2022-02-02', 1, 'NET'),
(68, 23, '2022-02-02', 1, 'GROSS'),
(69, 23, '2022-02-02', 1, 'ENABLE'),
(70, 24, '2022-02-02', 0.9, 'NET'),
(71, 24, '2022-02-02', 1.2, 'GROSS'),
(72, 24, '2022-02-02', 1, 'ENABLE'),
(73, 25, '2022-02-02', 0.6, 'NET'),
(74, 25, '2022-02-02', 0.7, 'GROSS'),
(75, 25, '2022-02-02', 1, 'ENABLE'),
(76, 26, '2022-02-02', 0.9, 'NET'),
(77, 26, '2022-02-02', 1.2, 'GROSS'),
(78, 26, '2022-02-02', 1, 'ENABLE'),
(79, 27, '2022-02-02', 0.3, 'NET'),
(80, 27, '2022-02-02', 0.4, 'GROSS'),
(81, 27, '2022-02-02', 1, 'ENABLE'),
(82, 28, '2022-02-02', 0.5, 'NET'),
(83, 28, '2022-02-02', 0.7, 'GROSS'),
(84, 28, '2022-02-02', 1, 'ENABLE'),
(85, 29, '2022-02-02', 0.015, 'NET'),
(86, 29, '2022-02-02', 0.02, 'GROSS'),
(87, 29, '2022-02-02', 1, 'ENABLE'),
(88, 30, '2022-02-02', 0.02, 'NET'),
(89, 30, '2022-02-02', 0.03, 'GROSS'),
(90, 30, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(46, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 46);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(47, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 47),
(48, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 48),
(49, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 49),
(50, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Украшение тарелки", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 50);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(51, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 51),
(52, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 52),
(53, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 53),
(54, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 54),
(55, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Помидоры", "Помидоры", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 55),
(56, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Огурцы", "Огурцы", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 56);

insert into ingredient
(id, parent_id, child_id)
values
(31, 46, 47),
(32, 46, 48),
(33, 47, 49),
(34, 47, 50),
(35, 48, 51),
(36, 48, 52),
(37, 49, 53),
(38, 49, 54),
(39, 50, 55),
(40, 50, 56);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(91, 31, '2022-02-02', 0.15, 'NET'),
(92, 31, '2022-02-02', 0.15, 'GROSS'),
(93, 31, '2022-02-02', 1, 'ENABLE'),
(94, 32, '2022-02-02', 0.15, 'NET'),
(95, 32, '2022-02-02', 0.15, 'GROSS'),
(96, 32, '2022-02-02', 1, 'ENABLE'),
(97, 33, '2022-02-02', 0.9, 'NET'),
(98, 33, '2022-02-02', 1.2, 'GROSS'),
(99, 33, '2022-02-02', 1, 'ENABLE'),
(100, 34, '2022-02-02', 1, 'NET'),
(101, 34, '2022-02-02', 1, 'GROSS'),
(102, 34, '2022-02-02', 1, 'ENABLE'),
(103, 35, '2022-02-02', 0.6, 'NET'),
(104, 35, '2022-02-02', 0.7, 'GROSS'),
(105, 35, '2022-02-02', 1, 'ENABLE'),
(106, 36, '2022-02-02', 0.9, 'NET'),
(107, 36, '2022-02-02', 1.2, 'GROSS'),
(108, 36, '2022-02-02', 1, 'ENABLE'),
(109, 37, '2022-02-02', 0.3, 'NET'),
(110, 37, '2022-02-02', 0.4, 'GROSS'),
(111, 37, '2022-02-02', 1, 'ENABLE'),
(112, 38, '2022-02-02', 0.5, 'NET'),
(113, 38, '2022-02-02', 0.7, 'GROSS'),
(114, 38, '2022-02-02', 1, 'ENABLE'),
(115, 39, '2022-02-02', 0.015, 'NET'),
(116, 39, '2022-02-02', 0.02, 'GROSS'),
(117, 39, '2022-02-02', 1, 'ENABLE'),
(118, 40, '2022-02-02', 0.02, 'NET'),
(119, 40, '2022-02-02', 0.03, 'GROSS'),
(120, 40, '2022-02-02', 1, 'ENABLE');

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(121, 18, '2022-03-03', 0, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(57, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 57);

insert into ingredient
(id, parent_id, child_id)
values
(41, 57, 15),
(42, 57, 15);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(122, 41, '2022-02-02', 1, 'NET'),
(123, 41, '2022-02-02', 1, 'GROSS'),
(124, 41, '2022-02-02', 1, 'ENABLE'),
(125, 42, '2022-02-02', 1, 'NET'),
(126, 42, '2022-02-02', 1, 'GROSS'),
(127, 42, '2022-02-02', 1, 'ENABLE');

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(58, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Некое блюдо", "Некое блюдо", "2012-02-02T12:00:00", 1, "PORTION", "KITCHEN", B'0', 58);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(59, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 1", "Полуфабрикат 1", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 59),
(60, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 2", "Полуфабрикат 2", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 61),
(61, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Полуфабрикат 3", "Полуфабрикат 3", "2012-02-02T12:00:00", 2, "KG", "NONE", B'0', 61);

insert into item
(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
values
(62, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 1", "Ингредиент 1", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 62),
(63, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 2", "Ингредиент 2", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 63),
(64, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 3", "Ингредиент 3", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 64),
(65, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
"Ингредиент 4", "Ингредиент 4", "2012-02-02T12:00:00", 3, "KG", "NONE", B'0', 65);

insert into ingredient
(id, parent_id, child_id)
values
(43, 58, 59),
(44, 58, 60),
(45, 59, 61),
(46, 60, 62),
(47, 60, 63),
(48, 61, 64),
(49, 61, 65);

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(128, 43, '2022-02-02', 1, 'NET'),
(129, 43, '2022-02-02', 1, 'GROSS'),
(130, 43, '2022-02-02', 1, 'ENABLE'),
(131, 44, '2022-02-02', 1, 'NET'),
(132, 44, '2022-02-02', 2, 'GROSS'),
(133, 44, '2022-02-02', 1, 'ENABLE'),
(134, 45, '2022-02-02', 1, 'NET'),
(135, 45, '2022-02-02', 1.2, 'GROSS'),
(136, 45, '2022-02-02', 1, 'ENABLE'),
(137, 46, '2022-02-02', 1, 'NET'),
(138, 46, '2022-02-02', 1.5, 'GROSS'),
(139, 46, '2022-02-02', 1, 'ENABLE'),
(140, 47, '2022-02-02', 1, 'NET'),
(141, 47, '2022-02-02', 1.8, 'GROSS'),
(142, 47, '2022-02-02', 1, 'ENABLE'),
(143, 48, '2022-02-02', 1, 'NET'),
(144, 48, '2022-02-02', 2, 'GROSS'),
(145, 48, '2022-02-02', 1, 'ENABLE'),
(146, 49, '2022-02-02', 1, 'NET'),
(147, 49, '2022-02-02', 2, 'GROSS'),
(148, 49, '2022-02-02', 1, 'ENABLE');

insert into periodic_quantity
(id, ingredient_id, data, quantity, type)
values
(149, 48, '2022-03-03', 0, 'ENABLE'),
(150, 49, '2022-03-03', 0, 'ENABLE');

--insert into item
--(id, is_alcohol, is_not_in_price_list, is_garnish, is_not_in_employee_menu, is_include_garnish, is_include_sauce, is_sauce,
--name, print_name, reg_time, parent_id, unit, workshop, is_deleted, number)
--values
--(66, B'000000', B'000000', B'000000', B'000001', B'000000', B'000001', B'000000',
--"Ингредиент 5", "Ингредиент 5", "2012-02-02T12:00:00", 3, "PORTION", "NONE", B'0', 66);
--
--insert into ingredient
--(id, parent_id, child_id)
--values
--(50, 61, 66);
--
--insert into periodic_quantity
--(id, ingredient_id, data, quantity, type)
--values
--(151, 50, '2022-03-03', 1, 'NET'),
--(152, 50, '2022-03-03', 1, 'GROSS'),
--(153, 50, '2022-03-03', 1, 'ENABLE');