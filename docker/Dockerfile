FROM java:8-alpine
VOLUME /tmp
ADD *.jar app.jar
ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
