# Hackathon API

This project is a Spring Boot API for managing hackathons, user registration, and login functionalities. It utilizes Java 17 and Maven for dependency management, and MongoDB as the database.

## Features

- User registration and authentication
- Hackathon management
- Problem statement management
- Team management

## Technologies Used

- Java 17
- Spring Boot
- MongoDB
- Maven

## Getting Started

### Prerequisites

- Java 17
- Maven
- MongoDB

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd hackathon-api
   ```

3. Install dependencies:
   ```
   mvn install
   ```

4. Configure your MongoDB connection in `src/main/resources/application.properties`.

5. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Endpoints

- **POST /api/auth/register** - Register a new user
- **POST /api/auth/login** - Login a user
- **GET /api/hackathons** - Retrieve all hackathons
- **POST /api/hackathons** - Create a new hackathon

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.