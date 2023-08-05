::rmdir dagserver /S/q
::rmdir dagserver-front /S/q

::git clone https://github.com/maximolira/dagserver-front.git
::cd dagserver-front
::docker build . -t dagserver-front --file ./DockerFile


::cd ../

::git clone https://github.com/maximolira/dagserver.git
::cd dagserver
::docker build . -t dagserver --file ./DockerFile

::cd ../

docker-compose up