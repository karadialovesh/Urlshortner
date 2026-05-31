# Spring Boot URL Shortener

Welcome to my URL Shortener project! I built this REST API using Spring Boot 3 and Java 17. The main goal was to create a fast and secure service that lets people shorten long links, track how often they are used, and even generate QR codes on the fly. 

To make sure the application stays fast under heavy load, I hooked it up with Redis for caching and added rate limiting to keep things stable.

## What it can do

- Secure Logins: I used JWTs (JSON Web Tokens) so users can securely register and log in. It also separates regular users from admins.
- Fast Redirects: By plugging in Redis, the app caches popular links. This means when someone clicks a short link, they get redirected instantly without putting extra strain on the database.
- Fair Usage (Rate Limiting): To prevent spam, I used Bucket4j. Right now, it limits users to 5 requests per minute so nobody can overload the system.
- Instant QR Codes: Need a QR code for your new link? The app uses ZXing to generate and return one instantly.
- Auto-Cleanup: I set up a scheduled background job that automatically deletes links once they've been expired for a year, keeping the database tidy. You can also manually change when your links expire.
- Custom Links: If you don't want a random string of characters, you can pick your own custom alias (as long as it's not a reserved word like 'admin').

## What I used to build it

- The core is Java 17 and Spring Boot 3.5.x
- Data is stored in MySQL using Spring Data JPA
- Redis (via Spring Data Redis) handles all the high-speed caching
- Security is managed by Spring Security and JJWT
- I also threw in Bucket4j for the rate limits, ZXing for the QR codes, and Lombok to keep the code clean.

## Want to run it yourself?

### What you'll need first
- Java 17
- Maven
- MySQL running on port 3306
- Redis running on port 6379

### Setup Steps

1. First, grab the code:
   git clone <repository-url>
   cd Urlshortner

2. Set up your database:
   Open up src/main/resources/application.properties and point it to your local MySQL database.
   
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
   spring.datasource.username=root
   spring.datasource.password=your_password

3. Fire it up:
   You can start it right from your terminal.
   
   If you're on Windows:
   .\mvnw.cmd spring-boot:run

   If you're on a Mac or Linux:
   ./mvnw spring-boot:run

## How to use the API

I've included a Postman Collection (UrlShortener_Postman_Collection.json) right in the project folder. Just import it into Postman, and you're good to go!

Here is a quick rundown of the endpoints:

### Getting Started
- POST /register : Create a new account.
- POST /login : Get your JWT token.

### Managing Links (You'll need your JWT token for these)
- POST /api/shorten : Make a new short link (you can add a customCode if you want).
- GET /api/my-urls : See all the links you've created.
- PUT /api/update-expiry : Change when a specific link expires.

### Public Endpoints (No token needed)
- GET /u/{shortCode} : This is the actual redirect link. It sends users to the original URL.
- GET /api/qr/{shortCode} : Downloads the QR code image for a link.

### Admin Tools (You need the ADMIN role)
- GET /admin/urls : See every link in the system.
- GET /admin/stats : Check out the total clicks across the platform.
- DELETE /admin/url/{id} : Remove a link from the database.
