CREATE DATABASE IF NOT EXISTS project_control
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE project_control;


-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE users (
                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL,
                       name VARCHAR(150) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;


-- =========================================================
-- PROJECTS
-- =========================================================

CREATE TABLE projects (
                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          user_id BIGINT UNSIGNED NOT NULL,

                          name VARCHAR(150) NOT NULL,
                          slug VARCHAR(180) NOT NULL,
                          description TEXT NULL,

                          repository_url VARCHAR(500) NULL,
                          production_url VARCHAR(500) NULL,

                          project_status ENUM(
        'active',
        'paused',
        'maintenance',
        'archived',
        'discontinued'
    ) NOT NULL DEFAULT 'active',

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),

                          UNIQUE KEY uq_projects_user_slug (user_id, slug),

                          CONSTRAINT fk_projects_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
                                  ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- ENVIRONMENTS
-- =========================================================

CREATE TABLE environments (
                              id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                              project_id BIGINT UNSIGNED NOT NULL,

                              name VARCHAR(100) NOT NULL,

                              environment_type ENUM(
        'development',
        'staging',
        'production'
    ) NOT NULL,

                              status ENUM(
        'unknown',
        'online',
        'degraded',
        'offline',
        'maintenance'
    ) NOT NULL DEFAULT 'unknown',

                              last_heartbeat_at DATETIME NULL,

                              health_check_url VARCHAR(500) NULL,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,

                              PRIMARY KEY (id),

                              UNIQUE KEY uq_environment_project_name (project_id, name),

                              KEY idx_environments_status (status),
                              KEY idx_environments_heartbeat (last_heartbeat_at),

                              CONSTRAINT fk_environments_project
                                  FOREIGN KEY (project_id)
                                      REFERENCES projects(id)
                                      ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- ENVIRONMENT STATUS HISTORY
-- =========================================================

CREATE TABLE environment_status_history (
                                            id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                            environment_id BIGINT UNSIGNED NOT NULL,

                                            old_status ENUM(
        'unknown',
        'online',
        'degraded',
        'offline',
        'maintenance'
    ) NULL,

                                            new_status ENUM(
        'unknown',
        'online',
        'degraded',
        'offline',
        'maintenance'
    ) NOT NULL,

                                            reason VARCHAR(255) NULL,

                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                            PRIMARY KEY (id),

                                            KEY idx_status_history_environment_date
                                                (environment_id, created_at),

                                            CONSTRAINT fk_status_history_environment
                                                FOREIGN KEY (environment_id)
                                                    REFERENCES environments(id)
                                                    ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- PROJECT SERVICES
-- =========================================================

CREATE TABLE project_services (
                                  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                  environment_id BIGINT UNSIGNED NOT NULL,

                                  name VARCHAR(150) NOT NULL,

                                  service_type ENUM(
        'application',
        'database',
        'cache',
        'queue',
        'worker',
        'cron',
        'docker',
        'other'
    ) NOT NULL DEFAULT 'application',

                                  status ENUM(
        'unknown',
        'online',
        'degraded',
        'offline',
        'stopped'
    ) NOT NULL DEFAULT 'unknown',

                                  host VARCHAR(255) NULL,
                                  port INT UNSIGNED NULL,

                                  last_check_at DATETIME NULL,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,

                                  PRIMARY KEY (id),

                                  UNIQUE KEY uq_service_environment_name
                                      (environment_id, name),

                                  KEY idx_services_status (status),

                                  CONSTRAINT fk_services_environment
                                      FOREIGN KEY (environment_id)
                                          REFERENCES environments(id)
                                          ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- DEPLOYMENTS
-- =========================================================

CREATE TABLE deployments (
                             id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             environment_id BIGINT UNSIGNED NOT NULL,

                             version VARCHAR(100) NULL,
                             commit_hash VARCHAR(100) NULL,

                             status ENUM(
        'pending',
        'running',
        'success',
        'failed',
        'cancelled'
    ) NOT NULL DEFAULT 'pending',

                             started_at DATETIME NULL,
                             finished_at DATETIME NULL,

                             triggered_by BIGINT UNSIGNED NULL,

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             PRIMARY KEY (id),

                             KEY idx_deployments_environment_date
                                 (environment_id, created_at),

                             CONSTRAINT fk_deployments_environment
                                 FOREIGN KEY (environment_id)
                                     REFERENCES environments(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_deployments_user
                                 FOREIGN KEY (triggered_by)
                                     REFERENCES users(id)
                                     ON DELETE SET NULL
) ENGINE=InnoDB;


-- =========================================================
-- PROJECT EVENTS
-- =========================================================

CREATE TABLE project_events (
                                id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                project_id BIGINT UNSIGNED NOT NULL,

                                event_type VARCHAR(100) NOT NULL,

                                message TEXT NULL,

                                metadata JSON NULL,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY (id),

                                KEY idx_project_events_project_date
                                    (project_id, created_at),

                                KEY idx_project_events_type (event_type),

                                CONSTRAINT fk_project_events_project
                                    FOREIGN KEY (project_id)
                                        REFERENCES projects(id)
                                        ON DELETE CASCADE
) ENGINE=InnoDB;