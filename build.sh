docker build . -t dagserver --file ./Dockerfile
docker run -d --privileged -p 8080:8080 -p 2587:2587 dagserver