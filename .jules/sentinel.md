## 2026-05-29 - [Hardcoded Database Credentials]
**Vulnerability:** The application had hardcoded database credentials (URL, username, and password) in the `DBConnection.java` class.
**Learning:** Hardcoding credentials makes them easily discoverable in the source code, increasing the risk of unauthorized database access if the code is exposed.
**Prevention:** Always use environment variables or a secure configuration management system to handle sensitive information like database credentials. Providing safe defaults for development is acceptable, but production secrets must be externalized.
