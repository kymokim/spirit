# Spirit

`spirit` is the main backend server for **한잔할까**, a liquor-focused O2O platform.

This project powers the core service experience, including store discovery, drink and menu management, event information, social posts, notifications, and authentication.

## Overview

- Production backend for **한잔할까**
- Built with Java 21 and Spring Boot
- Designed around domain-based modules such as `store`, `drink`, `menu`, `event`, `post`, `comment`, `report`, `notification`, and `auth`
- Includes AI-assisted search functionality through the `agent` module

## Main Features

- Store registration, suggestion, ownership, and manager workflows
- Drink, menu, and event management for stores
- Feed-style post, comment, like, save, and share features
- Authentication and authorization APIs
- Image upload and media handling with S3
- API documentation with Swagger
- Monitoring with Spring Boot Actuator and Prometheus

## Tech Stack

- Backend: Java, Spring Boot, Spring Security, Spring Data JPA, QueryDSL
- Database: MariaDB, H2
- Infra: Docker, AWS S3, GitHub Actions
- Observability: Actuator, Prometheus

## Notes

- Sensitive configuration files and production secrets are excluded from this repository.
- Some environment-specific settings are managed separately for local, development, and production deployment.
