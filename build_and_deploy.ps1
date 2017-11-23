$lines = Get-Content ..\secrets\secrets.env
foreach ($line in $lines) {
    $a,$b = $line.split('=')
    Set-Variable $a $b
}

mvn clean install

plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} rm -rf /opt/pi-lightcontrol
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} mkdir /opt/pi-lightcontrol

# copy to server
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk scripts\pi-lightcontrol-start.sh ${pi_zwave_ip}:/usr/local/bin/
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk scripts\pi-lightcontrol-stop.sh ${pi_zwave_ip}:/usr/local/bin/
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk target\pi-lightcontrol.jar ${pi_zwave_ip}:/opt/pi-lightcontrol
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk scripts\pi-lightcontrol ${pi_zwave_ip}:/etc/init.d

# execute remote commands
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} chmod 700 /usr/local/bin/pi-lightcontrol-start.sh
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} chmod 700 /usr/local/bin/pi-lightcontrol-stop.sh
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} chmod 700 /etc/init.d/pi-lightcontrol
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} sudo update-rc.d pi-lightcontrol defaults

# start server
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} /etc/init.d/pi-lightcontrol stop
plink -batch -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} "/etc/init.d/pi-lightcontrol start &"
