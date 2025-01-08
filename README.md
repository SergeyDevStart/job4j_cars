# job4j_cars

## Описание проекта
job4j_cars - сервис по продаже машин.
Возможности сервиса:
1. Создавать посты о продаже машин
   (добавлять фото, марку, владельца, описание и т. д)
2. Редактировать посты
   (менять фото, статус продажи, описание, добавлять владельцев)
3. Просматривать список постов по фильтрам
   (только с фото, за последний день, все)
4. Доступен каталог по фильтрам:
   (марка, кузов, двигатель, привод, коробка передач)
5. Есть возможность подписки на посты
6. Регистрация пользователя
7. Аутентификация пользователя
8. Авторизация пользователя

## Используемые технологии в проекте:
Основные :
- Java 17
- Spring Boot 2.7.6
- Hibernate 5.6
- Thymeleaf
- checkstyle-plugin
- Bootstrap
- PostgreSQL 15.1
- Liquibase 4.15.0
- Lombok 1.18.30

Тестирование :
- H2database 2.1.214
- JUnit 5 + AssertJ
- JaCoCo 0.8.8

### Требования к окружению:
- Java 17 — версии JDK не ниже 17;
- Maven 3.8 — инструмент сборки проекта;
- PostgreSQL 15 — сервер базы данных.

## Запуск проекта:
1. Создайте базу данных PostgreSQL:
    ```sql
    CREATE DATABASE cars_db;
    ```
2. Склонируйте репозиторий:
    ```bash
    git clone https://github.com/SergeyDevStart/job4j_cars
    cd job4j_cars
    ```

3. Соберите проект с помощью Maven:
    ```bash
    mvn clean install
    ```

4. Запустите приложение:
    ```bash
    mvn spring-boot:run
    ```

После успешного запуска приложение будет доступно по адресу: [http://localhost:8080](http://localhost:8080)

## Взаимодействие с приложением:

#### регистрация/вход/авторизация
![](img/registration.png)
![](img/login.png)
![](img/authorization.png)

#### Создание поста
![](img/create.png)

#### Редактирование поста
![](img/update.png)

#### Подробное описание поста своего/другого пользователя
![](img/myDetail.png)
![](img/otherDetail.png)

#### Все посты/мои посты/мои подписки
![](img/all.png)
![](img/myPosts.png)
![](img/subscriptions.png)

#### Поиск по категориям
![](img/category.png)

## Контакты
![SergeyDevStart](https://github.com/SergeyDevStart)
![Telegram](https://t.me/sergey_vasenev)