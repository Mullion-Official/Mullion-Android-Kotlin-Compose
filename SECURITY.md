# Security policy

## Supported versions

| Version | Supported |
| --- | --- |
| 1.x | ✅ |
| < 1.0 | ❌ |

## Reporting a vulnerability

Please **do not open a public issue** for security problems.

Use GitHub’s private vulnerability reporting on this repository
(**Security → Report a vulnerability**) if available, or contact a maintainer
privately. Include:

- affected module and version,
- a minimal reproduction (sample screen or code snippet),
- the Android API / device or emulator configuration,
- impact and any suggested mitigation.

We will acknowledge a report as soon as possible, coordinate a fix and a
disclosure date, and credit reporters who want credit.

## Scope notes

MULLION is a UI/layout framework. It does not:

- request or grant Android runtime permissions (`PermissionSheet` only renders a
  host-driven UI),
- store or transmit user data,
- perform networking,
- use reflection to load classes.

Dependency vulnerabilities in AndroidX, Kotlin or Compose should be reported to
their respective projects, but we will bump affected versions in a patch release
when notified.
