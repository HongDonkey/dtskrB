#!/usr/bin/env bash

set -Eeuo pipefail

SERVICE_NAME="${DTSKR_SERVICE_NAME:-dtskr.service}"
HEALTH_URL="${DTSKR_HEALTH_URL:-http://127.0.0.1:8080/api/health}"
MAX_ATTEMPTS="${DTSKR_HEALTH_MAX_ATTEMPTS:-20}"
RETRY_SECONDS="${DTSKR_HEALTH_RETRY_SECONDS:-2}"

show_recent_logs() {
  echo
  echo "Recent ${SERVICE_NAME} logs:"
  sudo journalctl -u "${SERVICE_NAME}" -n 80 --no-pager || true
}

fail() {
  echo "ERROR: $1" >&2
  show_recent_logs
  exit 1
}

command -v systemctl >/dev/null 2>&1 || {
  echo "ERROR: systemctl is not installed." >&2
  exit 1
}
command -v curl >/dev/null 2>&1 || {
  echo "ERROR: curl is not installed." >&2
  exit 1
}
[[ "${MAX_ATTEMPTS}" =~ ^[1-9][0-9]*$ ]] || {
  echo "ERROR: DTSKR_HEALTH_MAX_ATTEMPTS must be a positive integer." >&2
  exit 1
}
[[ "${RETRY_SECONDS}" =~ ^[1-9][0-9]*$ ]] || {
  echo "ERROR: DTSKR_HEALTH_RETRY_SECONDS must be a positive integer." >&2
  exit 1
}

echo "Restarting ${SERVICE_NAME}..."
sudo -v
sudo systemctl restart "${SERVICE_NAME}"

sudo systemctl is-active --quiet "${SERVICE_NAME}" || fail "The service did not enter the active state."

for ((attempt = 1; attempt <= MAX_ATTEMPTS; attempt++)); do
  response="$(curl --silent --show-error --fail --max-time 5 "${HEALTH_URL}" 2>/dev/null || true)"
  if [[ "${response}" =~ \"server\"[[:space:]]*:[[:space:]]*\"UP\" ]] &&
     [[ "${response}" =~ \"database\"[[:space:]]*:[[:space:]]*\"UP\" ]]; then
    echo "Backend restart completed: API UP / DB UP"
    sudo systemctl status "${SERVICE_NAME}" --no-pager --lines=8
    exit 0
  fi

  echo "Waiting for health check (${attempt}/${MAX_ATTEMPTS})..."
  sleep "${RETRY_SECONDS}"
done

fail "Health check failed at ${HEALTH_URL}."
