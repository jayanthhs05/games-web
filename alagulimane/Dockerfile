# ---- build the WebAssembly bundle ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x gradlew && ./gradlew --no-daemon wasmJsBrowserDistribution

# ---- serve the static site ----
FROM nginx:1.27-alpine
COPY --from=build /src/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
