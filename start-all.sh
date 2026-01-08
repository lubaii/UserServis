#!/bin/bash

echo "=== Запуск всех сервисов ==="

# Шаг 1: Проверка PostgreSQL
echo "1. Проверка PostgreSQL..."
sudo systemctl start postgresql 2>/dev/null || true
sleep 2

# Шаг 2: Запуск Kafka
echo "2. Запуск Kafka..."
cd "/home/luba/Documents/Обучение/UserServis"
sudo docker compose up -d
echo "Ожидание запуска Kafka (15 секунд)..."
sleep 15
docker compose ps

# Шаг 3: Установка переменных окружения
echo "3. Настройка переменных окружения..."
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=1111  # ЗАМЕНИТЕ НА ВАШ ПАРОЛЬ!

echo ""
echo "=== Сервисы готовы к запуску ==="
echo ""
echo "ТЕРМИНАЛ 1 - Запустите Notification Service:"
echo "  cd notification-service && mvn spring-boot:run"
echo ""
echo "ТЕРМИНАЛ 2 - Запустите User Service:"
echo "  mvn spring-boot:run"
echo ""
echo "Переменные окружения уже установлены в текущей сессии."

