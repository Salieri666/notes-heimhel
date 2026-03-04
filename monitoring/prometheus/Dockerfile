FROM prom/prometheus:v3.10.0

USER root

ADD --chmod=755 https://github.com/a8m/envsubst/releases/download/v1.2.0/envsubst-Linux-x86_64 /usr/local/bin/envsubst

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

USER nobody

ENTRYPOINT ["/docker-entrypoint.sh"]