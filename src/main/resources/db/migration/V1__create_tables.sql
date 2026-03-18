CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             message TEXT NOT NULL,
                                             user_id BIGINT NOT NULL,
                                             another_user_id BIGINT,
                                             status VARCHAR(50) DEFAULT 'отправлено',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_another_user
    FOREIGN KEY (another_user_id) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_notifications_status
    CHECK (status IN ('отправлено', 'получено'))
    );