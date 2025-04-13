#Сначала собрать образы
docker-compose build

#Запускать поэтапно
docker-compose up -d config-server eureka-server
sleep 20
docker-compose up -d auth-service notification-service order-service product-service
sleep 15
docker-compose up -d gateway
