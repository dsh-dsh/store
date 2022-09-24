#!/usr/bin/env bash

echo 'copy files...'

cp -f -r /mnt/d/VSCode/vue/store/dist/css src/main/resources/static/static
cp -f -r /mnt/d/VSCode/vue/store/dist/js src/main/resources/static/static
cp -f -r /mnt/d/VSCode/vue/store/dist/img src/main/resources/static/static
cp -f -r /mnt/d/VSCode/vue/store/dist/fonts src/main/resources/static
cp -f -r /mnt/d/VSCode/vue/store/dist/index.html src/main/resources/templates/index.html

sed -i 's/src="\/js/src="\/static\/js/g' src/main/resources/templates/index.html
sed -i 's/href="\/css/href="\/static\/css/g' src/main/resources/templates/index.html

mvn clean package -DskipTests

echo 'copy jar to server...'

scp ~/.ssh/id_rsa target/store-1.0-SNAPSHOT.jar daniil@79.133.181.230:/home/daniil/store

echo 'restart server...'

ssh ~/.ssh/id_rsa  daniil@79.133.181.230 << EOF

pgrep java | xargs kill -9
nohup java -jar store/store-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'end script'