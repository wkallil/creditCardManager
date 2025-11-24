## Credit Card Manager (IN PROGRESS)

## 🇺🇸 About the Project 🇬🇧

This is a simple personal application designed to manage credit card expenses. It allows you to:

* 📇 Register an **Owner**
* 🧾 Create an **Expense**
* 🔁 Create a **Monthly Charge** (recurring monthly expense)
* 📧 Automatically send an **email summary** to each Owner with their expenses

    * The email template is written in Portuguese since the application is for personal use

The application is built with:

* Java 21
* Spring Boot
* PostgreSQL
* Email sending
* Scheduled monthly execution

---

### 📚 Technologies

* **Java 21**
* **Spring Boot**
* **Spring Data JPA**
* **Spring Mail**
* **PostgreSQL**
* **Docker**

---

### 🚀 How to Run

1. Clone the repository:

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
```

2. Configure the `application.yml` with your database and email credentials.

3. Run the application:

```bash
mvn spring-boot:run
```

4. The application will be available at:

```
http://localhost:8080
```

---

### 📬 Email Scheduler

The system sends a summary email.

You can also trigger it manually via endpoint.

# 📌 Credit Card Expense Manager

> 🇧🇷 Aplicação pessoal para controle de gastos de cartão de crédito
> 
> 🇺🇸 Personal application for managing credit card expenses

---

## 🇧🇷 Sobre o Projeto

Esta é uma aplicação simples desenvolvida para uso pessoal, com o objetivo de organizar gastos de cartão de crédito. Ela permite:

* 📇 Cadastrar um **Owner** (dono do cartão ou responsável pelo gasto)
* 🧾 Criar uma **Expense** (gasto avulso)
* 🔁 Criar uma **Monthly Charge** (lançamento fixo mensal)
* 📧 Enviar automaticamente um **e-mail de resumo** para cada Owner com seus gastos

* O template do e-mail está em português, pois será utilizado pessoalmente

A aplicação utiliza:

* Java 21
* Spring Boot
* PostgreSQL
* Envio de e-mails
* Scheduler para disparo mensal

---

### 📚 Tecnologias

* **Java 21**
* **Spring Boot**
* **Spring Data JPA**
* **Spring Mail**
* **PostgreSQL**
* **Docker**

---

### 🚀 Como Executar

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
```

2. Configure o `application.yml` com as credenciais de banco e e-mail.

3. Execute o projeto:

```bash
mvn spring-boot:run
```

4. A aplicação estará disponível em:

```
http://localhost:8080
```

---

### 📬 Agendamento de E-mails

O sistema envia um e-mail, contendo o resumo de gastos do período.

Também é possível disparar manualmente via endpoint.

---


