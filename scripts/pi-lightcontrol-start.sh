#!/bin/bash
#
# Location: /usr/local/bin/
#

source /opt/secrets/secrets.env
export $(cut -d= -f1 /opt/secrets/secrets.env)

OPTIONS="-Djavax.net.ssl.trustStore=/opt/secrets/hsm.keystore -Djavax.net.ssl.trustStorePassword=${ssl_storepass} -Dhsm.user=${apache_httpd_passwd_user} -Dhsm.secret=${apache_httpd_passwd_password} -Dipinterative_ip=${ipinterative_ip}"

java -Duser.timezone=Europe/Berlin $OPTIONS -jar /opt/pi-lightcontrol/pi-lightcontrol.jar
