#Сначала собрать образы
docker-compose build

#Запускать поэтапно
docker-compose up -d config-server eureka-server
sleep 20
docker-compose up -d auth-service notification-service order-service product-service
sleep 15
docker-compose up -d gateway


C:.
├───.idea                  # Конфигурация IDE (например, IntelliJ IDEA)
├───auth-service           # Сервис аутентификации
│   ├───.mvn               # Maven wrapper
│   ├───src                # Исходные файлы
│   └───target             # Скомпилированные файлы
├───config-server          # Сервис конфигурации
├───docker                 # Docker файлы и конфигурации
├───eureka-server          # Сервис Eureka для регистрации микросервисов
├───gateway                # API Gateway
├───notification           # Сервис уведомлений
├───order                  # Сервис заказов
├───product                # Сервис продуктов
└───src                    # Общие исходные файлы
