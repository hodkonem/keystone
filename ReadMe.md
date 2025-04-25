#Сначала собрать образы
docker-compose build

#Запускать поэтапно
docker-compose up -d config-server eureka-server
sleep 20
docker-compose up -d auth-service notification-service order-service product-service
sleep 15
docker-compose up -d gateway

Keystone Platform - Auth Service
🚀 Быстрый старт
Предварительные требования
Java 17+

Maven 3.8+

PostgreSQL 13+

Docker (опционально)

Установка
Склонируйте репозиторий:

bash
git clone https://github.com/your-repo/keystone.git
cd keystone/auth-service
Создайте файл .env (на основе примера):

bash
cp .env.example .env
Заполните .env актуальными значениями:

properties
DB_URL=jdbc:postgresql://localhost:5432/auth_dev
DB_USERNAME=auth_user
DB_PASSWORD=secret
JWT_SECRET=your_strong_secret_here
⚙️ Конфигурация
Переменные окружения (.env)
Обязательные переменные:

properties
# База данных
DB_URL=jdbc:postgresql://host:port/db_name
DB_USERNAME=username
DB_PASSWORD=password

# JWT
JWT_SECRET=min_32_chars_secret
JWT_EXPIRATION=86400000  # 24 часа в ms

# Порт приложения
SERVER_PORT=8081
Запуск в разных окружениях
Локальная разработка:

bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
Тестирование:

bash
mvn test
Тесты используют src/test/resources/application-test.properties

🛠 Технические детали
Структура проекта
keystone/
├── parent-pom.xml
├── auth-service/
│   ├── src/
│   ├── .env          # Локальные переменные
│   ├── .env.example  # Шаблон
│   └── pom.xml
Работа с переменными окружения
В коде используйте:

java
import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
private static final Dotenv dotenv = Dotenv.load();

    public static String getDbUrl() {
        return dotenv.get("DB_URL");
    }
}
🔧 Устранение неполадок
Если переменные не загружаются
Убедитесь, что .env находится в корне модуля auth-service

Проверьте права на файл:

bash
chmod 600 .env
Для IDE (IntelliJ IDEA):

Включите "EnvFile" плагин или

Добавьте .env в конфигурацию запуска

Частые ошибки
Ошибка: Переменная DB_URL не найдена
Решение: Проверьте наличие .env и его синтаксис (должен быть UTF-8 без BOM)
🤝 Правила для разработчиков
Никогда не коммитьте .env:

bash
# .gitignore
.env
*.secret
Для совместной работы используйте:

bash
cp .env.example .env
При добавлении новой переменной:

Обновите .env.example

Сообщите команде в Slack/чате

🐳 Docker-развертывание (опционально)
bash
docker build -t auth-service .
docker run -p 8081:8081 --env-file .env auth-service
