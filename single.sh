docker build . -t dagserver
docker run -d -p 11000:8081  dagserver