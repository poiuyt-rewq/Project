# CampusDesk — Student Issue Tracking System

CampusDesk is a small Java console application for reporting and tracking campus issues. It keeps a local record of submitted tickets so the list survives program restarts.
## Features
- Report an issue with a category, title, description, and priority
- List all tickets
- Find a ticket by ID
- Filter tickets by status (Open, In Progress, Resolved)
- Update a ticket's status
- Delete a ticket with a confirmation prompt
- Save and reload tickets from a local text file
- Basic input validation
## Requirements
- Java 17 or newer (Java 14+ supports the switch syntax used here; Java 17 is recommended)
## Run
From the project root:
```bash
javac src/CampusDesk.java
java -cp src CampusDesk
```
On the first run, the `data` directory and file are created when a ticket is saved. The data file is local to this project folder.
## Storage notes
Ticket fields are stored in `data/tickets.txt`. Text fields are Base64 encoded to protect the file format from tabs and delimiter characters. This is a simple local storage mechanism, not encryption.
## Ideas for future improvements
- Add a GUI or web front end
- Add user/admin accounts
- Export tickets to CSV
- Add sorting by priority and creation time
- Add automated tests
## What I learned
Working on this project practices object-oriented modelling, collections, enums, input validation, file I/O, and breaking a larger task into smaller methods.

