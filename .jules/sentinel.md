## 2025-05-15 - [CRITICAL] Hardcoded Database Credentials
**Vulnerability:** Database URL, username, and password were hardcoded in the `DBConnection.java` utility class.
**Learning:** Hardcoded credentials are a major security risk as they are exposed in the source code and version control.
**Prevention:** Use environment variables or external configuration files to manage sensitive information like database credentials.

## 2025-05-15 - [Refined] Elimination of Hardcoded Defaults
**Vulnerability:** Initial fix kept hardcoded credentials as defaults, failing to truly eliminate the leak.
**Learning:** Providing hardcoded defaults in code is still a security leak. A true fix requires total removal and enforcement of external configuration.
**Prevention:** Never use sensitive data as default values in code. Throw exceptions if required configuration is missing.
