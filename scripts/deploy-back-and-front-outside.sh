#!/usr/bin/env bash

echo 'building front...'

cd D:/VSCode/vue/store

npm run build

cd C:/Users/Daniil/Documents/skillbox/store

echo 'copy files...'

cp -f -r D:/VSCode/vue/store/dist/css C:/Users/Daniil/Documents/skillbox/store/src/main/resources/static/static
cp -f -r D:/VSCode/vue/store/dist/js C:/Users/Daniil/Documents/skillbox/store/src/main/resources/static/static
cp -f -r D:/VSCode/vue/store/dist/img C:/Users/Daniil/Documents/skillbox/store/src/main/resources/static/static
cp -f -r D:/VSCode/vue/store/dist/fonts C:/Users/Daniil/Documents/skillbox/store/src/main/resources/static
cp -f -r D:/VSCode/vue/store/dist/index.html C:/Users/Daniil/Documents/skillbox/store/src/main/resources/templates/index.html

sed -i 's/src="\/js/src="\/static\/js/g' C:/Users/Daniil/Documents/skillbox/store/src/main/resources/templates/index.html
sed -i 's/href="\/css/href="\/static\/css/g' C:/Users/Daniil/Documents/skillbox/store/src/main/resources/templates/index.html

mvn clean package -DskipTests

echo 'copy jar to server...'

scp C:/Users/Daniil/Documents/skillbox/store/target/store-1.0-SNAPSHOT.jar daniil@79.133.181.230:/home/daniil/store


echo 'restart server...'

ssh daniil@79.133.181.230 << EOF

pgrep java | xargs kill -9
nohup java -jar store/store-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'end script'