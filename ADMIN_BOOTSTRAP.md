# Administrator bootstrap

Flyway V15 disables the publicly known V11 seed credential when its original hash is still
present. Existing administrators whose password hash has already changed remain active, but they
must change their password once because V15 adds `password_change_required` with a secure default.

When no active `ADMIN` account exists, initialize one by setting these deployment secrets and
restarting the backend:

```text
ADMIN_BOOTSTRAP_LOGIN_ID=<administrator-login-id>
ADMIN_BOOTSTRAP_PASSWORD=<temporary-strong-password>
ADMIN_BOOTSTRAP_DISPLAY_NAME=Administrator
```

The temporary password must be 8-72 characters and contain a letter, number, and symbol. The
backend stores only a BCrypt hash. It does not log the password.

After the bootstrap succeeds:

1. Remove `ADMIN_BOOTSTRAP_PASSWORD` from the deployment environment.
2. Sign in with the temporary password.
3. The UI will show only the required password-change form.
4. Set a new password that is not the bootstrap password.
5. Sign in again with the new password.

Changing an administrator password increments `credential_version`, logs out the current session,
and invalidates all other sessions for that administrator on their next request.

If the server logs `No active administrator exists`, no administrator endpoint can be used until
the bootstrap environment variables are supplied and the backend is restarted. Public catalog
features remain available.
