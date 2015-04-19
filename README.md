# oauth2

oauth2

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Database

### 升级数据库

	lein ragtime migrate

### 回退数据库

	lein ragtime rollback

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2015 FIXME
