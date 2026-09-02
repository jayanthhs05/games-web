# Serves the game as a web terminal: ttyd runs a real PTY and streams it to
# xterm.js in the browser. The game code is unchanged.
FROM python:3.12-slim

ARG TTYD_VERSION=1.7.7
ARG TTYD_ARCH=x86_64
RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates curl \
 && curl -fsSL -o /usr/local/bin/ttyd \
      "https://github.com/tsl0922/ttyd/releases/download/${TTYD_VERSION}/ttyd.${TTYD_ARCH}" \
 && chmod +x /usr/local/bin/ttyd \
 && apt-get purge -y curl && apt-get autoremove -y \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN chmod +x play.sh && useradd -m player
USER player

# ttyd listens on $PORT (default 7681). -W allows keyboard input; -m caps
# concurrent players (each tab is its own isolated process).
ENV PORT=7681
EXPOSE 7681
CMD ["sh", "-c", "ttyd -p ${PORT} -W -m 25 -t titleFixed=Hangman ./play.sh"]
