## 2025-05-15 - Hardcoded Database Credentials
**Vulnerability:** The application stored database URL, username, and password as plaintext constants in `DBConnection.java`.
**Learning:** Common in early-stage or academic projects, but poses a risk if code is committed to public repositories.
**Prevention:** Use environment variables or a secure configuration management system to handle sensitive credentials.
