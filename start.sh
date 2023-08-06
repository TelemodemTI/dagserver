#!/bin/bash
cd /usr/local/tomcat/bin
sh catalina.sh run > /usr/local/tomcat/logs/catalina.log 2>&1 &

echo "cooltime"
sleep 30

# Ruta del archivo que esperamos que exista
archivo_esperado="/usr/local/tomcat/webapps/dagserver/WEB-INF/cli/assets/defaults.js"

# Bucle para esperar hasta que el archivo esperado exista
while [ ! -e "$archivo_esperado" ]; do
    echo "Esperando a que el archivo $archivo_esperado exista..."
    sleep 10  # Puedes ajustar el tiempo de espera en segundos (opcional)
done
sh catalina.sh stop

# Una vez que el archivo esperado existe, reemplazarlo
echo 'var environment = {\n dagserverUri : "'$DAGSERVERURI'"};' > /usr/local/tomcat/webapps/dagserver/WEB-INF/cli/assets/defaults.js

sh catalina.sh run >> /usr/local/tomcat/logs/catalina.log 2>&1 &
tail -f /usr/local/tomcat/logs/catalina.log