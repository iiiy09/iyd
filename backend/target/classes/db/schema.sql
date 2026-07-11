CREATE DATABASE IF NOT EXISTS iyd_learning
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE iyd_learning;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    password VARCHAR(128) COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    stage VARCHAR(20) COMMENT '学段:小学/初中/高中/大学',
    role VARCHAR(20) DEFAULT 'student' COMMENT '角色:student/admin',
    status INT DEFAULT 1 COMMENT '状态:1正常0禁用',
    login_platform VARCHAR(20) COMMENT '登录平台:web/miniapp',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_phone(phone),
    INDEX idx_stage(stage)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE IF NOT EXISTS question_error (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_content TEXT COMMENT '题目内容',
    question_image VARCHAR(500) COMMENT '题目图片',
    answer TEXT COMMENT '正确答案',
    user_answer TEXT COMMENT '用户答案',
    knowledge_point VARCHAR(200) COMMENT '知识点',
    error_reason VARCHAR(50) COMMENT '错误原因',
    error_count INT DEFAULT 1 COMMENT '错误次数',
    mastered INT DEFAULT 0 COMMENT '是否掌握',
    subject VARCHAR(50) COMMENT '科目',
    stage VARCHAR(20) COMMENT '学段',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id),
    INDEX idx_stage_subject(stage,subject)
) ENGINE=InnoDB COMMENT='错题记录表';

CREATE TABLE IF NOT EXISTS course_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL COMMENT '课程名称',
    stage VARCHAR(20) COMMENT '学段',
    grade VARCHAR(20) COMMENT '年级',
    subject VARCHAR(50) COMMENT '科目',
    semester VARCHAR(20) COMMENT '学期',
    video_url VARCHAR(500) COMMENT '视频地址',
    cover_url VARCHAR(500) COMMENT '封面图',
    duration INT COMMENT '时长(秒)',
    description TEXT COMMENT '简介',
    teacher_name VARCHAR(50) COMMENT '讲师',
    category VARCHAR(20) COMMENT '分类:同步课堂/公开课',
    play_count INT DEFAULT 0 COMMENT '播放次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stage_grade(stage,grade),
    INDEX idx_subject(subject)
) ENGINE=InnoDB COMMENT='网课课程表';

CREATE TABLE IF NOT EXISTS course_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    watched_duration INT DEFAULT 0 COMMENT '已观看时长(秒)',
    total_duration INT COMMENT '总时长(秒)',
    status INT DEFAULT 0 COMMENT '0未完成1已完成',
    last_watch_time DATETIME COMMENT '最后观看时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_course(user_id,course_id)
) ENGINE=InnoDB COMMENT='课程观看记录表';

CREATE TABLE IF NOT EXISTS recite_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_text TEXT COMMENT '背诵原文',
    user_answer TEXT COMMENT '用户作答',
    handwritten_image VARCHAR(500) COMMENT '手写图片地址',
    recite_video VARCHAR(500) COMMENT '背诵视频地址',
    check_type INT DEFAULT 1 COMMENT '批改类型:1文字2拍照3视频',
    score DOUBLE COMMENT '得分',
    error_details TEXT COMMENT '错误详情JSON',
    suggestion TEXT COMMENT '复习建议',
    subject VARCHAR(50) COMMENT '科目',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id),
    INDEX idx_type(check_type)
) ENGINE=InnoDB COMMENT='背诵记录表';

CREATE TABLE IF NOT EXISTS english_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL COMMENT '单词',
    phonetic VARCHAR(200) COMMENT '音标',
    meaning TEXT COMMENT '释义',
    example VARCHAR(500) COMMENT '例句',
    stage VARCHAR(20) COMMENT '学段',
    category VARCHAR(50) COMMENT '词库分类',
    memory_status INT DEFAULT 0 COMMENT '记忆状态',
    user_id BIGINT COMMENT '用户ID',
    review_count INT DEFAULT 0 COMMENT '复习次数',
    last_review_time DATETIME COMMENT '上次复习时间',
    next_review_time DATETIME COMMENT '下次复习时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_word(user_id,word),
    INDEX idx_stage_category(stage,category)
) ENGINE=InnoDB COMMENT='英语单词词库表';

CREATE TABLE IF NOT EXISTS note_folder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    folder_name VARCHAR(100) NOT NULL COMMENT '文件夹名',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id)
) ENGINE=InnoDB COMMENT='笔记文件夹表';

CREATE TABLE IF NOT EXISTS user_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) COMMENT '标题',
    content TEXT COMMENT '笔记内容',
    ocr_image VARCHAR(500) COMMENT 'OCR原图',
    mind_map_image VARCHAR(500) COMMENT '思维导图图片',
    folder_id BIGINT DEFAULT 0 COMMENT '文件夹ID',
    note_type INT DEFAULT 1 COMMENT '类型:1文本2卡片3OCR',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_folder(user_id,folder_id)
) ENGINE=InnoDB COMMENT='笔记信息表';

CREATE TABLE IF NOT EXISTS study_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_content VARCHAR(500) NOT NULL COMMENT '任务内容',
    estimated_minutes INT COMMENT '预计时长(分钟)',
    deadline DATETIME COMMENT '截止时间',
    status INT DEFAULT 0 COMMENT '状态:0未完成1已完成',
    checkin_time DATETIME COMMENT '打卡时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_status(user_id,status),
    INDEX idx_deadline(deadline)
) ENGINE=InnoDB COMMENT='自律学习任务表';

CREATE TABLE IF NOT EXISTS study_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_name VARCHAR(200) NOT NULL COMMENT '资料名称',
    stage VARCHAR(20) COMMENT '学段',
    grade VARCHAR(20) COMMENT '年级',
    subject VARCHAR(50) COMMENT '科目',
    resource_type VARCHAR(30) COMMENT '类型:课件/真题/提纲/答案',
    file_url VARCHAR(500) COMMENT '文件地址',
    source VARCHAR(20) DEFAULT 'manual' COMMENT '来源:init/sync/manual',
    audit_status INT DEFAULT 1 COMMENT '审核状态:0待审1通过2驳回',
    uploader_id BIGINT COMMENT '上传者ID',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stage_grade(stage,grade),
    INDEX idx_subject_type(subject,resource_type),
    INDEX idx_audit(audit_status)
) ENGINE=InnoDB COMMENT='学习资料表';

CREATE TABLE IF NOT EXISTS resource_sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(50) COMMENT '同步批次号',
    new_resource_count INT DEFAULT 0 COMMENT '新增资料数',
    resource_ids TEXT COMMENT '新增资源ID列表',
    operation_type VARCHAR(20) COMMENT '操作类型:auto/manual',
    operator_id BIGINT COMMENT '操作员ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_batch(batch_no),
    INDEX idx_time(create_time)
) ENGINE=InnoDB COMMENT='资料同步日志表';

CREATE TABLE IF NOT EXISTS score_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_file VARCHAR(500) COMMENT '原始成绩文件',
    chinese_score VARCHAR(20) COMMENT '语文',
    math_score VARCHAR(20) COMMENT '数学',
    english_score VARCHAR(20) COMMENT '英语',
    physics_score VARCHAR(20) COMMENT '物理',
    chemistry_score VARCHAR(20) COMMENT '化学',
    biology_score VARCHAR(20) COMMENT '生物',
    history_score VARCHAR(20) COMMENT '历史',
    geography_score VARCHAR(20) COMMENT '地理',
    politics_score VARCHAR(20) COMMENT '政治',
    ai_report TEXT COMMENT 'AI学情报告',
    study_plan TEXT COMMENT '学习计划',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id)
) ENGINE=InnoDB COMMENT='成绩分析表';

CREATE TABLE IF NOT EXISTS ai_generate_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    generate_type INT COMMENT '类型:1作文2PPT3导图4文案',
    input_content TEXT COMMENT '输入内容',
    output_content TEXT COMMENT '输出内容',
    output_image VARCHAR(500) COMMENT '输出图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_type(user_id,generate_type)
) ENGINE=InnoDB COMMENT='AI生成记录表';

CREATE TABLE IF NOT EXISTS user_collection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    collection_type INT DEFAULT 1 COMMENT '1资料2课程3笔记',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id),
    UNIQUE INDEX uk_user_resource(user_id,resource_id,collection_type)
) ENGINE=InnoDB COMMENT='用户收藏表';

CREATE TABLE IF NOT EXISTS pk_battle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    user1_score INT DEFAULT 0,
    user2_score INT DEFAULT 0,
    winner_id BIGINT COMMENT '胜者ID',
    questions TEXT COMMENT '题目JSON',
    status INT DEFAULT 0 COMMENT '0进行中1已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user1(user1_id),
    INDEX idx_user2(user2_id)
) ENGINE=InnoDB COMMENT='刷题PK表';

CREATE TABLE IF NOT EXISTS daily_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    study_minutes INT DEFAULT 0 COMMENT '学习时长(分钟)',
    task_count INT DEFAULT 0 COMMENT '完成数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_user_date(user_id,checkin_date)
) ENGINE=InnoDB COMMENT='每日打卡表';
