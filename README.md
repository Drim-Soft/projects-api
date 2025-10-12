docker build -t planifika-projects .

docker run --env-file .env -p 8080:8080 planifika-projects
