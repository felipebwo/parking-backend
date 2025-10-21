-- Script de criação de tabelas (simplificado)
CREATE DATABASE IF NOT EXISTS parking;
USE parking;

CREATE TABLE IF NOT EXISTS garages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  base_price DECIMAL(10,2),
  max_capacity INT
);

CREATE TABLE IF NOT EXISTS sectors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  garage_id BIGINT,
  name VARCHAR(50),
  base_price DECIMAL(10,2),
  max_capacity INT,
  closed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS spots (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sector_id BIGINT,
  lat DOUBLE,
  lng DOUBLE,
  occupied BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS parking_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  license_plate VARCHAR(30),
  spot_id BIGINT,
  sector_id BIGINT,
  entry_time DATETIME,
  parked_time DATETIME,
  exit_time DATETIME,
  amount DECIMAL(10,2) DEFAULT 0,
  dynamic_multiplier DECIMAL(5,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS revenue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sector VARCHAR(50),
  date DATE,
  amount DECIMAL(12,2),
  currency VARCHAR(5) DEFAULT 'BRL'
);
