#!/usr/bin/env bash

cd C:/Users/Daniil/Documents/skillbox/store

mvn clean package -DskipTests

echo 'copy jar to server...'

scp C:/Users/Daniil/Documents/skillbox/store/target/store-1.0-SNAPSHOT.jar daniil@79.133.181.230:/home/daniil/store


echo 'restart server...'

ssh daniil@79.133.181.230 << EOF

pgrep java | xargs kill -9
nohup java -jar store/store-1.0-SNAPSHOT.jar &

EOF

echo 'end script'