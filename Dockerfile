FROM openjdk:17-alpine
WORKDIR /app
CMD ["sh", "-c", "javac Main.java 2> compile.err || true; java Main"]
