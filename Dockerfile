FROM azul/zulu-openjdk:17.0.14-17.56
WORKDIR /app

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 빌드된 jar 파일 복사
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# SSL 인증서 파일 복사 및 권한 설정
ARG CERT_FILE
COPY ${CERT_FILE} cert.p12
RUN chown 1001:1001 cert.p12 && chmod 644 cert.p12

EXPOSE 8080

# 작업 디렉토리 권한 설정
RUN chown -R 1001:1001 /app

# 비루트 사용자로 전환
USER 1001 

ENV SERVER_SSL_KEY_STORE=/app/cert.p12

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -k -f https://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
CMD java -jar app.jar
