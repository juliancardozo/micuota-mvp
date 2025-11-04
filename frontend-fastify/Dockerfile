FROM nginx:stable-alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx content
RUN rm -rf ./*

# Copy static frontend files into nginx html directory
COPY public/ .

EXPOSE 80

CMD ["/bin/sh", "-c", "nginx -g 'daemon off;'"]
