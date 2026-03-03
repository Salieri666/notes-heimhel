# notes-heimhel

http://localhost:8090/swagger-ui/index.html

# it used keycloak

## private client: 
- home url: http://localhost:4200
- valid redirect urls: http://localhost:4200/*
- web origins: http://localhost:4200
- client authentication: on
- standard flow: on
- direct access grant: on
- service account roles: on

## public client:
- valid redirect urls: http://192.168.0.240:4200/*
- root url: http://192.168.0.240/
- Valid post logout redirect URIs: "+"
- web origins: http://192.168.0.240:4200
- client authentication: off
- standard flow: on
- direct access grant: on
- service account roles: off
- Advanced->advanced settings->Proof key for code exchange->s256

# For local frontend
docker compose --env-file .env up -d

# How to create dumps of postgres database
pg_dump -h remote_host -p 5432 -U db_user -d db_name -F c -f backup.dump

# How to restore dumps of postgres database
pg_restore -h remote_host -p 5432 -U db_user -d db_name backup.dump

# How to create dump from container
docker exec -e PGPASSWORD='postgres' compli-sql-db \
pg_dump -U postgres -d cmpl -F t -f /tmp/20260303_dump.tar

docker cp compli-sql-db:/tmp/20260303_dump.tar .
