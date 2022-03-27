delete from sets where item_id = 10;

delete from price where item_id = (select id from item where name = 'Новое блюдо');
delete from periodic_quantity;
delete from ingredient;
delete from sets where item_id = (select id from item where name = 'Новое блюдо');
delete from item where name = 'Новое блюдо';

delete from price where item_id = (select id from item where name = 'Пиво');
delete from periodic_quantity;
delete from ingredient;
delete from sets where item_id = (select id from item where name = 'Пиво');
delete from item where name = 'Пиво';
