### 1. Init docker compose
```yaml
  keycloak:
    image: quay.io/keycloak/keycloak:24.0
    command: [ "start-dev", "--http-port", "7080", "--https-port", "7443" ]
    ports:
      - "7080:7080"
      - "7443:7443"
    environment:
      KC_HOSTNAME: localhost
      KC_HOSTNAME_PORT: 7080
      KC_HOSTNAME_STRICT_BACKCHANNEL: "true"
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_HEALTH_ENABLED: "true"
      KC_LOG_LEVEL: info
      DB_VENDOR: POSTGRES
      DB_ADDR: postgres
      DB_DATABASE: keycloakdb
      DB_SCHEMA: public
      DB_USER: keycloakuser
      DB_PASSWORD: keycloakpass
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    container_name: local_postgres
    environment:
      # system db init properties
      POSTGRES_DB: internaldb
      POSTGRES_USER: internal_usr
      POSTGRES_PASSWORD: internalusrpwd
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql  # создаем несколько тестовых БД c пользователями
    command: # Необходимое условие для CDC Debezium
      - "postgres"
      - "-c"
      - "wal_level=logical"  # https://debezium.io/documentation/reference/stable/connectors/postgresql.html#:~:text=Configuring%20the%20PostgreSQL-,server,-If%20you%20are
```
- `init.sql`
```sql
-- 2. Keycloak database
CREATE DATABASE keycloakdb;
CREATE USER keycloakuser WITH PASSWORD 'keycloakpass';
ALTER DATABASE keycloakdb OWNER TO keycloakuser;
GRANT ALL PRIVILEGES ON DATABASE keycloakdb TO keycloakuser;
ALTER USER keycloakuser WITH SUPERUSER;
```

### 2. Create realm

- A realm manages a set of users, credentials, roles, and groups. 
- A user belongs to and logs into a realm. 
- Realms are isolated from one another and can only manage and authenticate the users that they control.

