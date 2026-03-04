#!/bin/sh
set -e

echo "→ Substituting variables from .env into prometheus.yml.tmpl..."

if [ -f /etc/prometheus/prometheus.yml.tmpl ]; then
  envsubst < /etc/prometheus/prometheus.yml.tmpl > /etc/prometheus/prometheus.yml
  echo "prometheus.yml was generated"
else
  echo "prometheus.yml.tmpl not found"
fi

echo "→ Running Prometheus..."
exec /bin/prometheus \
  --config.file=/etc/prometheus/prometheus.yml \
  --storage.tsdb.path=/prometheus \
  --web.route-prefix="${PROMETHEUS_WEB_ROUTE_PREFIX:-}" \
  --web.external-url="${PROMETHEUS_WEB_EXTERNAL_URL:-}" \
  --web.console.libraries=/usr/share/prometheus/console_libraries \
  --web.console.templates=/usr/share/prometheus/consoles \
  --web.enable-remote-write-receiver \
  --web.enable-lifecycle \
  "$@"