FROM debian:bookworm-slim

ARG ROUTER_VERSION=v2.12.1
ARG TARGETARCH

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl tar \
    && rm -rf /var/lib/apt/lists/*

RUN case "${TARGETARCH}" in \
      amd64) ROUTER_ASSET="router-${ROUTER_VERSION}-x86_64-unknown-linux-gnu.tar.gz" ;; \
      arm64) ROUTER_ASSET="router-${ROUTER_VERSION}-aarch64-unknown-linux-gnu.tar.gz" ;; \
      *) echo "Unsupported TARGETARCH: ${TARGETARCH}" >&2; exit 1 ;; \
    esac \
    && curl -fsSL "https://github.com/apollographql/router/releases/download/${ROUTER_VERSION}/${ROUTER_ASSET}" -o /tmp/router.tar.gz \
    && tar -xzf /tmp/router.tar.gz -C /tmp dist/router \
    && mv /tmp/dist/router /usr/local/bin/router \
    && chmod +x /usr/local/bin/router \
    && rm /tmp/router.tar.gz

EXPOSE 4000

ENTRYPOINT ["router"]
