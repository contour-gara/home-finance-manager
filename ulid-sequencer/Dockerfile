FROM ghcr.io/graalvm/native-image-community:21 AS build
WORKDIR /home-finance-manager
COPY . .
RUN microdnf install findutils
RUN ./gradlew clean ulid-sequencer:nativeCompile

FROM debian:bookworm-20250113-slim AS lib
RUN apt-get update \
    && apt-get install -y --no-install-recommends zlib1g \
    && rm -rf /var/lib/apt/lists/*

FROM gcr.io/distroless/base-debian12
EXPOSE 8080
COPY --from=build /home-finance-manager/ulid-sequencer/build/native/nativeCompile/ulid-sequencer /app
COPY --from=lib /usr/lib/aarch64-linux-gnu/libz.so.1 /usr/lib/aarch64-linux-gnu/libz.so.1
ENTRYPOINT ["./app"]
