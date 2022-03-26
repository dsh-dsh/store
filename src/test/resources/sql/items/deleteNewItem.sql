delete from price where item_id = (select id from item where name = 'Новое блюдо');
delete from item where name = 'Новое блюдо';
delete from price where item_id = (select id from item where name = 'Пиво');
delete from item where name = 'Пиво';