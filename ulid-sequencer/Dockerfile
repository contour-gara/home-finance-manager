FROM amazoncorretto:21.0.10-al2023 AS build
WORKDIR /jlink
RUN jlink \
    --verbose \
    --compress=2 \
    --strip-java-debug-attributes \
    --no-header-files \
    --no-man-pages \
    --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.security.sasl,java.sql,jdk.jfr,jdk.unsupported,jdk.crypto.ec \
    --output jre-min

FROM gcr.io/distroless/java-base-debian12:nonroot
WORKDIR /deployments
COPY --from=build --chown=nonroot:nonroot /jlink/jre-min /opt/jre-min
ENV JAVA_HOME=/opt/jre-min
ENV PATH=$JAVA_HOME/bin:$PATH
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
