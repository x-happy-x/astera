FROM azul/zulu-openjdk:25-jre

# Переменные окружения по умолчанию
ENV LANG=C.UTF-8 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
