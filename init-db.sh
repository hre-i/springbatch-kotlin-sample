#!/bin/bash
#
PGPASS=${HOME}/.pgpass
if [ -f ${PGPASS} ] && (grep 'sample:sample' ${PGPASS} > /dev/null); then
    :
else
    touch ${PGPASS}
    chmod 600 ${PGPASS}
    echo "localhost:5432:sample:sample:sample" >> ${PGPASS}
fi

${DOCKER_POSTGRES} createuser -h localhost -U postgres sample
echo "ALTER ROLE sample WITH PASSWORD 'sample';" | ${DOCKER_POSTGRES} psql -h localhost -U postgres postgres

${DOCKER_POSTGRES} dropdb -h localhost -U postgres sample
${DOCKER_POSTGRES} createdb -h localhost -U postgres -O sample sample
