FROM openjdk:17
WORKDIR /app
COPY teste.java .
RUN javac teste.java
CMD ["java", "teste", "&"]
