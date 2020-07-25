## Miro assignment

Notable design choices:

Java 14. I wanted to use an immutable structure for the Widgets, I chose to use records, in preview right now.
That did cause some downstream issues. My usual code coverage tools and linter don't work, so that is a bit of a shame.
I think test coverage is ok though.

I did most of the optionals, except the SQL based back end. I got pretty far using jOOQ (also a thing I never used but wanted to try), but ran out of time.

It was unclear to me if the last_updated flag needed to be updated by the backend. I've left it untouched for now.

### CI
The build test runs here: 

[![CircleCI](https://circleci.com/gh/flyaruu/miro-widget.svg?style=svg)](https://circleci.com/gh/flyaruu/miro-widget)


### Dependencies
I used bucket4j for the rate limiter. I used a rtree2 from David Moten for the geo queries.
I don't really like the fact that the rate limiting is intermixed so much with the actual application code, but there isn't much I can do about that. As an architecture, I'd prefer to move rate limiting to somewhere in the ingress pipeline.

### Building & Running

Make sure you've got Java 14 active, you might need to point the JAVA_HOME to the right version.

```bash
mvn test
```
... should do the trick.

To run the jar:
```bash
cd target/
java -jar --enable-preview miroassignment-0.0.1-SNAPSHOT.jar 
```
Note the --enable-preview flag, this is required for Java 14.

When running, you can check a swagger-ui:

http://localhost:8080/swagger-ui/index.html

to see a basic API documentation.


