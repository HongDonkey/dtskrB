# Production database roles

Use separate MySQL accounts for application runtime queries and Flyway migrations.

## Runtime account

The runtime account needs only data access:

```sql
CREATE USER 'dtskr_app'@'127.0.0.1' IDENTIFIED BY '<runtime-secret>';
GRANT SELECT, INSERT, UPDATE, DELETE
ON `digimon_time_stranger`.* TO 'dtskr_app'@'127.0.0.1';
```

Do not grant `CREATE`, `ALTER`, `DROP`, `INDEX`, `GRANT OPTION`, or MySQL user-management
privileges to this account.

## Flyway account

The Flyway account needs schema migration privileges:

```sql
CREATE USER 'dtskr_flyway'@'127.0.0.1' IDENTIFIED BY '<migration-secret>';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES
ON `digimon_time_stranger`.* TO 'dtskr_flyway'@'127.0.0.1';
```

Restrict the host portion to the real backend host or private network. Do not use `'%'` unless
network controls make it unavoidable.

## Application environment

```text
SPRING_PROFILES_ACTIVE=prod
DB_USERNAME=dtskr_app
DB_PASSWORD=<runtime-secret>
FLYWAY_DB_USERNAME=dtskr_flyway
FLYWAY_DB_PASSWORD=<migration-secret>
```

The production profile falls back to `DB_USERNAME` and `DB_PASSWORD` when Flyway-specific values
are absent, but separate credentials are recommended before public deployment.

After migrations finish, the application continues using only the runtime datasource account.
Keep both passwords in the deployment platform's secret manager, not in source control or shell
scripts.
