# Spring Boot + MCP Banking Demo (IBAN Validation)

This project is a **Spring Boot 3 + Java 17** based demo application, developed following **Domain-Driven Design (DDD)** and **Clean Code** principles.  
The purpose is to design a **Swiss IBAN validation service** aligned with real banking standards, expose it via **Swagger**, containerize it with **Docker**, automate with **GitHub Actions CI/CD**, and prepare the foundation for **Claude MCP integration**.

---

## Features
- Swiss IBAN validation service (Mod-97 check + country code validation)
- DDD layered architecture (Domain, DTO, Service, Controller)
- REST API documentation with Swagger/OpenAPI
- Dockerized for portability across environments
- GitHub Actions CI/CD pipeline (automated build & test)
- PII hygiene → IBAN outputs are masked (`CH93**********2957`)
- Ready for Claude MCP integration → MCP Tool (`IbanMcpTools`) is implemented and can be connected with Claude Pro (not tested).

---

## Project Structure
src/main/java/com/mcp_ai
│
├── domain
│   ├── Iban.java
│   └── IbanValidationResult.java
│
├── service
│   └── IbanValidatorService.java
│
├── controller
│   └── IbanController.java
│
└── mcp
└── IbanMcpTools.java 
--- 
- **Domain** → IBAN Value Object and ValidationResult
- **Service** → Business rules (country check, length, digits, mod-97)
- **Controller** → REST API (accessible via Swagger/Postman)
- **MCP Tools** → Tool definition for Claude integration (`chIban.validate`)

---

## Technologies & Dependencies
- Java 17
- Spring Boot 3.5.5
- Spring Web + Validation
- Springdoc OpenAPI (Swagger UI)
- Spring Boot Actuator
- Spring AI MCP Starter
- Maven Wrapper (`mvnw`)
- Docker (containerization)
- GitHub Actions (CI/CD)

---

## API Usage

### Swagger UI
After starting the application:  
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Example Requests

Valid IBAN:
```json
{ "iban": "CH9300762011623852957" } 
```
Invalid Country:
```json
{ "iban": "DE89370400440532013000" }
``` 
Example Response
```json
{
  "valid": false,
  "country": "CH",
  "message": "Mod-97 check failed",
  "maskedIban": "CH93**********2958"
}
```
Docker
Build Image
```json
./mvnw clean package -DskipTests
docker build -t mcp-banking .
```
Run Container
```json
 docker run -p 8081:8080 mcp-banking
``` 
Access Swagger:
```json
http://localhost:8081/swagger-ui.html
```
--- 
CI/CD (GitHub Actions)

The project includes a GitHub Actions workflow to automatically:
1.	Build and test with Maven
2.	Build a Docker image

Workflow file: .github/workflows/ci-cd.yml

⸻

Claude MCP Integration

The project is prepared to integrate with Anthropic Claude MCP Client.
•	IbanMcpTools defines a tool (chIban.validate) callable by Claude.
•	Intended behavior: a user asks in natural language “Is this IBAN valid?”, Claude calls the service, and returns the result in natural language.
•	Note: Integration requires Claude Pro membership; this step has not been tested.

⸻

References
•	Spring Boot Docs
•	Spring AI (MCP)
•	Swagger / Springdoc OpenAPI
•	Docker Docs
•	GitHub Actions
•	Anthropic Claude MCP

--- 
This is a demo project.
The main goal is to demonstrate modern backend technologies applied to a realistic banking use case, with CI/CD, Docker, and MCP integration readiness.
