delete from price where item_id = (select id from item where name = 'Новое блюдо (1)');
delete from item where name = 'Новое блюдо (1)';