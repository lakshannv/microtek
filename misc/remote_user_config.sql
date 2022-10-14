CREATE USER 'remote'@'%' IDENTIFIED BY 'password';
-- ALTER USER 'remote'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES on *.* TO 'remote'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
