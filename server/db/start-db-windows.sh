set -e

BASEDIR=$(dirname "$0")

# Use Windows-style path
DOCKER_COMPOSE_FILE=$(cygpath -w "${BASEDIR}/docker-compose.yml")

# Start Docker Compose using the Windows-style path
docker-compose --file "${DOCKER_COMPOSE_FILE}" up