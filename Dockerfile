# Build Stage
FROM bellsoft/liberica-openjdk-alpine:21 AS builder

#2. 컨테이너 내부 작업 폴더 설정.
WORKDIR /app

#3. 코드를 app폴더로 복사.
COPY . .

RUN chmod +x ./gradlew

#4. 컨테이너의 복사된 코드로 실행파일(.jar)을 생성
RUN ./gradlew clean build -x test

# Run Stage
FROM bellsoft/liberica-openjdk-alpine:21

#컨테이너 내부 작업 폴더 설정
WORKDIR /app

#실행 할 때 .jar만 복사해서 실행.
COPY --from=builder /app/build/libs/*.jar app.jar

#사용하는 포트가 8080임을 명시
EXPOSE 8080

#컨테이너가 실행되자마자 생성된 jar가 실행되도록 수정
ENTRYPOINT ["java", "-jar", "app.jar"]