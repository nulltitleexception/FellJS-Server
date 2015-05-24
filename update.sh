cd /var/staging_area/FellJS-Server/
git reset --hard
git pull
kill -9 `cat save_pid.txt`
rm /var/java/FellJS-Server.jar
cp FellJS-Server.jar /var/java
nohup java -jar /var/java/FellJS-Server.jar > /root/output.log&
echo $! > save_pid.txt
echo "Update complete."