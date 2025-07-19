@echo off

rem Ejecutar pruebas JUnit 
call mvn test -P junit-tests > junit-tests.log 2>&1

rem Ejecutar pruebas TestNG principales
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/login-test.xml > testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/apikey-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/credentials-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/keystore-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/properties-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/system.jobs-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/jobs-test.xml >> testng-tests.log 2>&1

rem Ejecutar pruebas TestNG Operadores
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-cmd-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-excel-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-file-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-ftp-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-groovy-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-http-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-java-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-jdbc-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-mail-test.xml >> testng-tests.log 2>&1
call mvn test -P testng-tests -DsuiteXmlFile=src/test/resources/suites/operator-sftp-test.xml >> testng-tests.log 2>&1
