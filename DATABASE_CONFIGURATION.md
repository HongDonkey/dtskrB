# Database environment variables

The application does not contain a default database username or password. Both values must be supplied by the runtime environment.

Required variables:

- `DB_USERNAME`
- `DB_PASSWORD`

Required production profile:

- `SPRING_PROFILES_ACTIVE=prod`

Optional connection variables and their defaults:

- `DB_HOST`: `localhost`
- `DB_PORT`: `3306`
- `DB_NAME`: `digimon_time_stranger`

PowerShell example for the current terminal session:

```powershell
$env:DB_USERNAME='your_database_username'
$env:DB_PASSWORD='your_database_password'
.\gradlew.bat bootRun
```

To inspect SQL with substituted bind values during local development, activate the `dev` profile explicitly:

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'
$env:DB_USERNAME='your_database_username'
$env:DB_PASSWORD='your_database_password'
.\gradlew.bat bootRun
```

For production, activate the safe profile and HTTPS-only session cookies:

```text
SPRING_PROFILES_ACTIVE=prod
SESSION_COOKIE_SECURE=true
```

The `prod` profile uses the MySQL driver directly and disables P6Spy, Hibernate SQL/bind logging, JdbcTemplate parameter logging, and Flyway statement-level DEBUG output. It also hides exception details from HTTP error responses. The `dev` profile is the only profile that enables P6Spy and Flyway SQL DEBUG logging.

For production, configure these values in the hosting platform's secret or environment-variable settings. Do not place real values in `.env.example`, `application.properties`, shell scripts, or source control.
