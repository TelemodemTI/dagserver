@echo off

echo ==== Inicio de pruebas: %date% %time% ==== > test-output.log
rem Eliminar la imagen Docker maximolira/dagserver si existe
call docker image rm -f maximolira/dagserver

rem Ejecutar pruebas JUnit 
call mvn test -P junit-tests >> test-output.log 2>&1
if errorlevel 1 exit /b 1

rem Contruyendo el contenedor Docker
call docker build -t maximolira/dagserver . >> test-output.log 2>&1
if errorlevel 1 exit /b 1

rem Ejecutar pruebas TestNG principales
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/login-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/apikey-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/credentials-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/keystore-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/properties-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/system.jobs-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/jobs-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1

rem Ejecutar pruebas TestNG Operadores
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-pathdir-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-cmd-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-excel-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-file-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-ftp-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-groovy-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-http-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-java-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-jdbc-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-mail-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-sftp-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-minio-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1
call mvn surefire:test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-aether-test.xml >> test-output.log 2>&1
if errorlevel 1 exit /b 1



echo ==== Fin de pruebas: %date% %time% ==== >> test-output.log