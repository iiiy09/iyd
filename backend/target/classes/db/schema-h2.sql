CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(128),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    stage VARCHAR(20),
    role VARCHAR(20) DEFAULT 'student',
    status INT DEFAULT 1,
    login_platform VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS question_error (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_content TEXT,
    question_image VARCHAR(500),
    answer TEXT,
    user_answer TEXT,
    knowledge_point VARCHAR(200),
    error_reason VARCHAR(50),
    error_count INT DEFAULT 1,
    mastered INT DEFAULT 0,
    subject VARCHAR(50),
    stage VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL,
    stage VARCHAR(20),
    grade VARCHAR(20),
    subject VARCHAR(50),
    semester VARCHAR(20),
    video_url VARCHAR(500),
    cover_url VARCHAR(500),
    duration INT,
    description TEXT,
    teacher_name VARCHAR(50),
    category VARCHAR(20),
    play_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    watched_duration INT DEFAULT 0,
    total_duration INT,
    status INT DEFAULT 0,
    last_watch_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recite_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_text TEXT,
    user_answer TEXT,
    handwritten_image VARCHAR(500),
    recite_video VARCHAR(500),
    check_type INT DEFAULT 1,
    score DOUBLE,
    error_details TEXT,
    suggestion TEXT,
    subject VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS english_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    phonetic VARCHAR(200),
    meaning TEXT,
    example VARCHAR(500),
    stage VARCHAR(20),
    category VARCHAR(50),
    memory_status INT DEFAULT 0,
    user_id BIGINT,
    review_count INT DEFAULT 0,
    last_review_time TIMESTAMP,
    next_review_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS note_folder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    folder_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    content TEXT,
    ocr_image VARCHAR(500),
    mind_map_image VARCHAR(500),
    folder_id BIGINT DEFAULT 0,
    note_type INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_content VARCHAR(500) NOT NULL,
    estimated_minutes INT,
    deadline TIMESTAMP,
    status INT DEFAULT 0,
    checkin_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_name VARCHAR(200) NOT NULL,
    stage VARCHAR(20),
    grade VARCHAR(20),
    subject VARCHAR(50),
    resource_type VARCHAR(30),
    file_url VARCHAR(500),
    source VARCHAR(20) DEFAULT 'manual',
    audit_status INT DEFAULT 1,
    uploader_id BIGINT,
    download_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resource_sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(50),
    new_resource_count INT DEFAULT 0,
    resource_ids TEXT,
    operation_type VARCHAR(20),
    operator_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS score_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_file VARCHAR(500),
    chinese_score VARCHAR(20),
    math_score VARCHAR(20),
    english_score VARCHAR(20),
    physics_score VARCHAR(20),
    chemistry_score VARCHAR(20),
    biology_score VARCHAR(20),
    history_score VARCHAR(20),
    geography_score VARCHAR(20),
    politics_score VARCHAR(20),
    ai_report TEXT,
    study_plan TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_generate_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    generate_type INT,
    input_content TEXT,
    output_content TEXT,
    output_image VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    collection_type INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, resource_id, collection_type)
);

CREATE TABLE IF NOT EXISTS pk_battle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    user1_score INT DEFAULT 0,
    user2_score INT DEFAULT 0,
    winner_id BIGINT,
    questions TEXT,
    status INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS daily_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    study_minutes INT DEFAULT 0,
    task_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, checkin_date)
);
