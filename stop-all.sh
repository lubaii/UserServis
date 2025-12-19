#!/bin/bash

echo "=== Остановка всех сервисов ==="

# Остановка Kafka
echo "1. Остановка Kafka..."
cd "/home/luba/Documents/Обучение/UserServis"
docker compose down

echo ""
echo "2. Остановка User Service и Notification Service..."
echo "   Нажмите Ctrl+C в терминалах, где запущены сервисы"
echo ""

# Проверка процессов на портах
echo "3. Проверка процессов на портах 8080 и 8081..."
PIDS_8080=$(lsof -ti :8080 2>/dev/null)
PIDS_8081=$(lsof -ti :8081 2>/dev/null)

if [ ! -z "$PIDS_8080" ]; then
    echo "   Найден процесс на порту 8080 (User Service): $PIDS_8080"
    read -p "   Остановить процесс? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        kill $PIDS_8080
        echo "   Процесс остановлен"
    fi
fi

if [ ! -z "$PIDS_8081" ]; then
    echo "   Найден процесс на порту 8081 (Notification Service): $PIDS_8081"
    read -p "   Остановить процесс? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        kill $PIDS_8081
        echo "   Процесс остановлен"
    fi
fi

echo ""
echo "=== Все сервисы остановлены ==="

