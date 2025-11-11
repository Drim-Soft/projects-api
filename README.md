docker build -t planifika-projects .

docker run --env-file .env -p 8081:8081 planifika-projects
