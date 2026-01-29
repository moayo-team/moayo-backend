# 1. 어떤 환경에서 실행할지 결정
FROM openjdk:17-jdk-slim

# 2. 빌드된 실행 파일(.jar)이 어디 있는지 지정
ARG JAR_FILE=build/libs/*.jar

# 3. 그 파일을 도시락 통 안으로 'app.jar'라는 이름으로 복사
COPY ${JAR_FILE} app.jar

# 4. 도시락 통이 열릴 때 실행할 명령어 (서버 실행!)
ENTRYPOINT ["java", "-jar", "/app.jar"]