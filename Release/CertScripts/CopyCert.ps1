Start-Process powershell.exe -Verb runAs @(

#-----Change those parameters-------------------------------------------------
$certBotDir = "C:\Certbot\live\lklm.host\";
$certPassword = "emb8237";
$pfxCertName = "cert.pfx";
$dectination = "C:\Users\Serega\Desktop\Release\_ReleaseBuild\ServerWorkDirectory";
$opensslPath = "C:\Users\Serega\Desktop\Release\CertScripts\openssl.exe";
#----------------------------------------------------------------------------

$fullchain = $certBotDir+"\fullchain.pem";
$privkey = $certBotDir+"\privkey.pem";
Copy-Item $fullchain -Destination $dectination;
Copy-Item $privkey -Destination $dectination;

$Command = "pkcs12 -inkey privkey.pem -in fullchain.pem -export -out $pfxCertName -passout pass:$certPassword";
$Command | Clip;

cd $dectination;
Start-Process -FilePath $opensslPath;
Read-Host -Prompt "Press any letter and Enter to delete extra files";
Remove-Item fullchain.pem;
Remove-Item privkey.pem;
)