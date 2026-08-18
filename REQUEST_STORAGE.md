# Request attachment storage

Request-board attachments are stored outside the application artifact. In the `prod` profile,
`REQUEST_UPLOAD_DIR` must be an absolute path. Application startup fails when a relative path is
used so that a deployment cannot silently write files into an ephemeral working directory.

Recommended Linux setting:

```text
REQUEST_UPLOAD_DIR=/var/lib/dtskr/uploads/requests
```

Container deployments must mount that path as a persistent volume. Do not keep uploads only in a
container writable layer.

## Access controls

- Grant write access only to the backend service account.
- Do not expose the upload directory through Nginx or another static-file server.
- Attachments must be read only through the authenticated administrator API.
- The backend resolves real filesystem paths and rejects files outside the configured upload root,
  including symbolic-link escapes.

## Backup and restore

The database and attachment directory form one logical backup. Back them up together:

1. Record or pause request-board writes.
2. Dump the MySQL database.
3. Snapshot or archive `REQUEST_UPLOAD_DIR`.
4. Store both artifacts under the same backup identifier and timestamp.
5. Resume writes.

For restoration, restore the database and attachment snapshot from the same backup identifier.
After restoration, verify that every `LOCAL` `request_attachment.storage_key` resolves under
`REQUEST_UPLOAD_DIR` and that the referenced file exists.

Test restoration before production release and on a recurring schedule. A database-only backup
does not preserve request attachments.
