FROM amazoncorretto:17-alpine
# ENV TEST_ENV="default_value123123"
ENV PROFILE=dev
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]