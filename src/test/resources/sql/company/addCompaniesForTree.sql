insert into company
(id, inn, is_mine, kpp, name, code, is_node, parent_id)
values
(4, "", B'0', 0, "ООО", 123, B'1', 0),
(5, "", B'0', 0, "ИП", 124, B'1', 0),
(6, "", B'0', 0, "Company name 1", 125, B'0', 4),
(7, "", B'0', 0, "Company name 2", 126, B'0', 5);