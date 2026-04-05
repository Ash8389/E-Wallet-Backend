-- Runs once when the MySQL container starts for the first time.
-- Creates a separate database for each microservice.
-- Each service only has access to its own database.

CREATE DATABASE IF NOT EXISTS userdb;
CREATE DATABASE IF NOT EXISTS walletdb;
CREATE DATABASE IF NOT EXISTS transactiondb;

GRANT ALL PRIVILEGES ON userdb.*        TO 'ewallet'@'%';
GRANT ALL PRIVILEGES ON walletdb.*      TO 'ewallet'@'%';
GRANT ALL PRIVILEGES ON transactiondb.* TO 'ewallet'@'%';

FLUSH PRIVILEGES;
