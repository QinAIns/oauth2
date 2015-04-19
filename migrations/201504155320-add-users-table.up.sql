CREATE TABLE users(
id uuid PRIMARY KEY,
password VARCHAR(100),
login_name VARCHAR(32),
nickname VARCHAR(32),
email VARCHAR(128),
phone VARCHAR(16),
created timestamp with time zone,
modified timestamp with time zone,
is_active BOOLEAN
);
CREATE INDEX password ON users (password);
CREATE INDEX email ON users (email);
CREATE INDEX phone ON users (phone);
CREATE INDEX login_name ON users (login_name);


CREATE TABLE clients (
id uuid PRIMARY KEY,
secret VARCHAR(64),
domain VARCHAR(256),
created timestamp with time zone,
modified timestamp with time zone
);
CREATE INDEX secret ON clients (secret);

CREATE TABLE tokens (
id uuid PRIMARY KEY,
token_type VARCHAR(32),
created timestamp with time zone,
modified timestamp with time zone,
refresh_token uuid
);
CREATE INDEX refresh_token ON tokens (refresh_token);
