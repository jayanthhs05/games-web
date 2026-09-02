# ---- build the WebAssembly bundle ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon wasmJsBrowserDistribution

# ---- serve the static site ----
FROM nginx:1.27-alpine
COPY --from=build /src/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
# nginx:alpine expands *.template with envsubst at startup, so the port is
# whatever the host injects (Render, Cloud Run, ...) and 8080 locally.
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
ENV PORT=8080
EXPOSE 8080
