delete from default_property_setting where user_id = (select id from users where email = 'system@user.com');
delete from users where email = 'system@user.com';