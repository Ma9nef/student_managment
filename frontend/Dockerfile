# 1️⃣ Build Angular
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 2️⃣ Serve with Nginx
FROM nginx:alpine

# Supprimer la config par défaut
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copier la config nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier le build Angular
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
