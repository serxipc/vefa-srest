@ECHO OFF

:: Test environment
::SET AP_ADDRESS=https://ringo-test.domain.com

:: Production environment
SET AP_ADDRESS=https://ringo.domain.com

:: cd to directory in which the bat file resides
cd /d %~dp0

for %%i in (..\lib\ringo-client*.jar) do (
 set RINGO=%%i
)
echo Ringo jar file is : %RINGO%

java.exe -Dlogback.configurationFile=..\conf\logback-upload.xml -cp %RINGO% no.sr.ringo.standalone.RingoClientMain --username %1 --password %2 --outboxPath %3 --archivePath %4 --upload --channelId %5 --address %AP_ADDRESS%
