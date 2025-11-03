FROM ghcr.io/graalvm/native-image-community:21 AS build
WORKDIR /home-finance-manager
COPY . .
RUN microdnf install findutils
RUN ./gradlew clean ulid-sequencer:nativeCompile

FROM debian:bookworm-20250113-slim AS lib
ARG TARGETPLATFORM
RUN apt-get update \
    && apt-get install -y --no-install-recommends zlib1g \
    && rm -rf /var/lib/apt/lists/*
RUN set -eu; \
    case "${TARGETPLATFORM}" in \
        "linux/amd64") \
            LIB_SRC="/usr/lib/x86_64-linux-gnu"; \
            LIB_DST="/opt/rootfs/usr/lib/x86_64-linux-gnu"; \
            ;; \
        "linux/arm64") \
            LIB_SRC="/usr/lib/aarch64-linux-gnu"; \
            LIB_DST="/opt/rootfs/usr/lib/aarch64-linux-gnu"; \
            ;; \
        *) echo "unsupported TARGETPLATFORM: ${TARGETPLATFORM}" >&2; exit 1 ;; \
    esac; \
    mkdir -p "${LIB_DST}"; \
    cp "${LIB_SRC}/libz.so.1" "${LIB_DST}/libz.so.1"

FROM gcr.io/distroless/base-debian12
ARG TARGETPLATFORM
EXPOSE 8080
COPY --from=build /home-finance-manager/ulid-sequencer/build/native/nativeCompile/ulid-sequencer /app
COPY --from=lib /opt/rootfs/ /
ENTRYPOINT ["./app"]
