# Менеджер задач (Java)

[![hexlet-check](https://github.com/EgorAl15/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/EgorAl15/java-project-99/actions)
[![Java CI](https://github.com/EgorAl15/java-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/EgorAl15/java-project-99/actions/workflows/build.yml)

**Менеджер задач** — веб-приложение для создания и управления задачами.

Проект выполнен в рамках обучения на Хекслете и позволяет на практике познакомиться с разработкой полноценного веб-приложения на Java.

В ходе работы над проектом используются проектирование базы данных, связи между сущностями, ORM, Spring Boot, Spring Security, Swagger, развертывание приложения на PaaS и мониторинг ошибок.

Учебный проект Хекслета: https://ru.hexlet.io/programs/java

Пример работы приложения: https://files.hexlet.app/a/xg6yxv

## Демо

Развёрнутое приложение доступно на Render:

https://java-project-99-oi9e.onrender.com/welcome

## Возможности

Приложение позволяет:

- регистрировать пользователей;
- авторизовываться в системе;
- создавать задачи;
- редактировать существующие задачи;
- удалять задачи;
- назначать исполнителя;
- изменять статус задачи;
- добавлять метки к задачам;
- фильтровать задачи по различным параметрам;
- просматривать список пользователей;
- управлять статусами и метками задач.

## Стек

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- H2
- Gradle
- Swagger / OpenAPI
- JUnit
- GitHub Actions
- Render
- Sentry

## Основные сущности

В приложении используются несколько основных сущностей:

- **User** — пользователь системы;
- **Task** — задача;
- **TaskStatus** — статус задачи;
- **Label** — метка задачи.

Задача может быть связана с пользователем-исполнителем, статусом и одной или несколькими метками.

## Установка

Склонируйте репозиторий:

```bash
git clone https://github.com/EgorAl15/java-project-99.git
cd java-project-99