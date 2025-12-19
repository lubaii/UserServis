#!/bin/bash
# Скрипт для тестирования REST API User Service

BASE_URL="http://localhost:8080/api/users"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== ТЕСТИРОВАНИЕ REST API User Service ===${NC}\n"

# Проверка здоровья приложения
echo -e "${GREEN}1. Health Check:${NC}"
curl -s http://localhost:8080/actuator/health | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/actuator/health
echo -e "\n"

# Создание первого пользователя
echo -e "${GREEN}2. Создание пользователя (John Doe):${NC}"
RESPONSE1=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","age":30}')
echo "$RESPONSE1" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE1"
USER_ID1=$(echo "$RESPONSE1" | grep -o '"id":[0-9]*' | grep -o '[0-9]*' | head -1)
echo -e "\n"

# Создание второго пользователя
echo -e "${GREEN}3. Создание пользователя (Jane Smith):${NC}"
RESPONSE2=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith","email":"jane@example.com","age":25}')
echo "$RESPONSE2" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE2"
USER_ID2=$(echo "$RESPONSE2" | grep -o '"id":[0-9]*' | grep -o '[0-9]*' | head -1)
echo -e "\n"

# Получение всех пользователей
echo -e "${GREEN}4. Получение всех пользователей:${NC}"
curl -s "$BASE_URL" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL"
echo -e "\n"

# Получение пользователя по ID
if [ ! -z "$USER_ID1" ]; then
    echo -e "${GREEN}5. Получение пользователя по ID ($USER_ID1):${NC}"
    curl -s "$BASE_URL/$USER_ID1" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/$USER_ID1"
    echo -e "\n"
fi

# Обновление пользователя
if [ ! -z "$USER_ID1" ]; then
    echo -e "${GREEN}6. Обновление пользователя (ID=$USER_ID1):${NC}"
    curl -s -X PUT "$BASE_URL/$USER_ID1" \
      -H "Content-Type: application/json" \
      -d '{"name":"John Updated","age":31}' | python3 -m json.tool 2>/dev/null || \
    curl -s -X PUT "$BASE_URL/$USER_ID1" \
      -H "Content-Type: application/json" \
      -d '{"name":"John Updated","age":31}'
    echo -e "\n"
fi

# Тест валидации (невалидный email)
echo -e "${GREEN}7. Тест валидации (невалидный email - ожидается 400):${NC}"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"invalid-email","age":25}' \
  -w "\nHTTP Status: %{http_code}\n" | tail -5
echo -e "\n"

# Удаление пользователя
if [ ! -z "$USER_ID1" ]; then
    echo -e "${GREEN}8. Удаление пользователя (ID=$USER_ID1):${NC}"
    curl -s -X DELETE "$BASE_URL/$USER_ID1" -w "HTTP Status: %{http_code}\n"
    echo -e "\n"
fi

# Проверка после удаления
echo -e "${GREEN}9. Проверка после удаления:${NC}"
curl -s "$BASE_URL" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL"
echo -e "\n"

echo -e "${YELLOW}=== Тестирование завершено ===${NC}"

