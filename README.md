# takehome
How to setup and run

Prerequisites:
1. Java 17 installed
2. docker, docker-compose installed

Instructions:
1. Navigate to cloned project root
2. Run: ./gradlew clean build jar
3. Run: docker-compose up
4. Endpoint is ready to be requestsed at localhost:8080
5. Run: curl --location --request GET 'localhost:8080/country-quiz/solution?codes=US,CA,UA,BA' to test output or use postman collection
