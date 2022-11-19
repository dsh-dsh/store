
echo 'restart server...'

ssh daniil@79.133.181.230 << EOF

pgrep java | xargs kill -9
nohup java -jar store/store-1.0-SNAPSHOT.jar &

EOF

echo 'end script'