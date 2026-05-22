| Java | 21 LTS | Linguagem principal |
| Spring Boot | 3.5.0 | Framework backend |
| Spring Data JPA | via Spring Boot | Acesso a dados / ORM |
| PostgreSQL | 16 | Banco de dados relacional |
| Swagger/OpenAPI | springdoc 2.8.6 | Documentação da API |
| JUnit 5 + Mockito | via Spring Boot | Testes unitários |
| HTML/CSS/JS | Vanilla | Frontend |
| GSAP | 3.12.5 | Animações |

| `GET` | `/api/videos`
| `GET` | `/api/videos/{id}`
| `GET` | `/api/videos/search?title=...`
| `GET` | `/api/videos/filter?watched=...&favorite=...&categoryId=...`
| `POST` | `/api/videos` 
| `PUT` | `/api/videos/{id}`
| `PATCH` | `/api/videos/{id}/watched`
| `PATCH` | `/api/videos/{id}/favorite`
| `DELETE` | `/api/videos/{id}`
| `GET` | `/api/categories` 
| `GET` | `/api/categories/{id}` 
| `GET` | `/api/categories/{id}/videos` 
| `POST` | `/api/categories` 
| `PUT` | `/api/categories/{id}` 
| `DELETE` | `/api/categories/{id}`