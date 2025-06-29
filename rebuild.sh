docker compose down
mvn clean
mvn package
docker compose build
docker compose up -d

