### Stage 1: build
FROM node:18-alpine AS build
WORKDIR /app

# Install dependencies and build
COPY package*.json ./
RUN npm ci --silent
COPY . .
RUN npm run build

### Stage 2: production nginx
FROM nginx:1.25-alpine
# copy nginx config (ensure your nginx.conf does NOT contain a 'user' directive if running non-root)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# copy built app
COPY --from=build /app/dist /usr/share/nginx/html

# create a non-root user
RUN addgroup -g 1001 -S nodejs && adduser -S nextjs -u 1001 -G nodejs

# ensure directories exist and are owned by nextjs
RUN mkdir -p /var/run/nginx /var/cache/nginx /var/log/nginx \
    && chown -R nextjs:nodejs /usr/share/nginx/html \
    && chown -R nextjs:nodejs /var/cache/nginx \
    && chown -R nextjs:nodejs /var/log/nginx \
    && chown -R nextjs:nodejs /etc/nginx/conf.d \
    && touch /var/run/nginx.pid \
    && chown nextjs:nodejs /var/run/nginx.pid

# switch to non-root user
USER nextjs

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# start nginx
CMD ["nginx", "-g", "daemon off;"]
