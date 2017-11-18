$lines = Get-Content ..\secrets\secrets.env
foreach ($line in $lines) {
    $a,$b = $line.split('=')
    Set-Variable $a $b
}

mvn clean install

plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} rm -rf ~${pi_zwave_user}/pi-lightcontrol
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} mkdir ~${pi_zwave_user}/pi-lightcontrol

# copy to server
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk target\pi-lightcontrol.jar ${pi_zwave_ip}:~${pi_zwave_user}/pi-lightcontrol
pscp -scp -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk start.sh ${pi_zwave_ip}:~${pi_zwave_user}/pi-lightcontrol

# execute remote commands
plink -l ${pi_zwave_user} -pw ${pi_zwave_ssh_passphrase} -i ..\secrets\raspberry-zwave.ppk ${pi_zwave_ip} chmod 700 ~${pi_zwave_user}/pi-lightcontrol/start.sh
