#!/bin/bash

source ../secrets/secrets.env
export $(cut -d= -f1 ../secrets/secrets.env)

OPTIONS="-Djavax.net.ssl.trustStore=../secrets/hsm.keystore -Djavax.net.ssl.trustStorePassword=${ssl_storepass} -Dhsm.user=${apache_httpd_passwd_user} -Dhsm.secret=${apache_httpd_passwd_password} -Dipinterative_ip=${ipinterative_ip}"
echo ${OPTIONS}

java -Duser.timezone=Europe/Berlin $OPTIONS -jar pi-lightcontrol.jar
