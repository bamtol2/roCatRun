CREATE TABLE members (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loginType VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    socialId VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255),
    createAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastLoginAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    height INT,
    weight INT,
    age INT,
    gender VARCHAR(50)
);

CREATE TABLE game_characters (
    character_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(8) NOT NULL UNIQUE,
    level INT NOT NULL,
    experience INT DEFAULT 0 NOT NULL,
    character_image VARCHAR(255) DEFAULT 'default.png' NOT NULL,
    coin INT DEFAULT 100 NOT NULL,
    total_games INT DEFAULT 0 NOT NULL,
    wins INT DEFAULT 0 NOT NULL,
    losses INT DEFAULT 0 NOT NULL,
    member_id BIGINT UNIQUE,
    FOREIGN KEY (level) REFERENCES levels(level),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_character_id BIGINT NOT NULL,
    FOREIGN KEY (game_character_id) REFERENCES game_characters(character_id) ON DELETE CASCADE
);

CREATE TABLE game_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id BIGINT NOT NULL,
    boss_level VARCHAR(50) NOT NULL,
    is_cleared BOOLEAN NOT NULL,
    running_time BIGINT,
    total_distance DOUBLE,
    pace_avg DOUBLE,
    heart_rate_avg DOUBLE,
    cadence_avg DOUBLE,
    item_use_count INT,
    reward_exp INT,
    reward_coin INT,
    calories INT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (character_id) REFERENCES game_characters(character_id) ON DELETE CASCADE
);


CREATE TABLE inventories (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id BIGINT,
    item_id BIGINT,
    isEquipped BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (character_id) REFERENCES game_characters(character_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE SET NULL
);

CREATE TABLE levels (
    level INT PRIMARY KEY,
    required_exp INT NOT NULL
);

CREATE TABLE boss (
    id INT AUTO_INCREMENT PRIMARY KEY,
    difficulty VARCHAR(50) NOT NULL,
    time_limit INT NOT NULL,
    hp_per_km INT NOT NULL,
    distance VARCHAR(50) NOT NULL,
    boss_image VARCHAR(255) NOT NULL,
    boss_name VARCHAR(100) NOT NULL,
    exp_reward_min INT NOT NULL,
    exp_reward_max INT NOT NULL,
    fever_condition VARCHAR(255),
    coin_reward_min INT NOT NULL,
    coin_reward_max INT NOT NULL
);

CREATE TABLE items (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,          
    korean_name VARCHAR(255) NOT NULL,   
    description TEXT NOT NULL,         
    is_gif BOOLEAN NOT NULL,           
    category ENUM('AURA', 'BALLOON', 'HEADBAND', 'PAINT') NOT NULL, 
    rarity ENUM('NORMAL', 'RARE', 'EPIC', 'UNIQUE', 'LEGENDARY') NOT NULL,
    probability DOUBLE NOT NULL,        
    price INT NOT NULL                  
);

INSERT INTO items (name, korean_name, description, is_gif, category, rarity, probability, price) VALUES
('ora_nyang', '오라냥~', '귀여운 고양이 오라를 두르고 경쾌하게 질주해보자! 냥~', true, 'AURA', 'RARE', 0.30, 30),
('ora_pizza', '피자 한 조각 오라', '바삭~ 촉촉~ 피자 오라와 함께 달리면 배고픔도 잊어버릴걸?', true, 'AURA', 'EPIC', 0.20, 50),
('ora_rainbow', '레인보우 빛 오라', '무지개빛 오라로 화려하게 빛나는 나! 내가 바로 달리기의 주인공!', true, 'AURA', 'UNIQUE', 0.04, 500),
('balloon_bird', '새둥실 풍선', '귀여운 새 모양 풍선과 함께 둥실둥실~ 하늘을 나는 기분!', true, 'BALLOON', 'UNIQUE', 0.04, 500),
('balloon_cat', '고양이 퐁퐁 풍선', '고양이 모양 풍선과 함께라면 어디든 귀염뽀짝! 미야옹~', true, 'BALLOON', 'UNIQUE', 0.04, 500),
('balloon_purple', '몽환 보라 풍선', '보라색 풍선을 달고 몽환적인 분위기로 하늘을 날아보자!', true, 'BALLOON', 'NORMAL', 0.45, 10),
('balloon_rainbow', '레인보우 반짝 풍선', '무지개빛 풍선과 함께라면 달릴 때마다 화려한 색이 반짝반짝!', true, 'BALLOON', 'RARE', 0.30, 30),
('balloon_yellow', '햇살 노랑 풍선', '산뜻한 노란색 풍선과 함께라면 기분까지 밝아질 거야!', true, 'BALLOON', 'NORMAL', 0.45, 10),
('balloon_blue', '바다빛 파랑 풍선', '시원한 파란색 풍선과 함께 달리면 청량함이 느껴진다!', true, 'BALLOON', 'NORMAL', 0.45, 10),
('headband_angel', '천사 윙 머리띠', '천사의 머리띠를 쓰면 착한 마음이 두 배! 날개도 달리는 건가?!', true, 'HEADBAND', 'LEGENDARY', 0.01, 1000),
('headband_bird', '깃털 새 머리띠', '새처럼 깜찍한 머리띠를 착용하면, 나도 귀여운 새가 될 수 있을까?', false, 'HEADBAND', 'UNIQUE', 0.04, 500),
('headband_devil', '장난꾸러기 악마 머리띠', '악마 머리띠를 착용하면 장난기 +10! 뿔난 모습도 멋지지 않나?', true, 'HEADBAND', 'LEGENDARY', 0.01, 1000),
('headband_mandarin', '귤톡톡 머리띠', '상큼한 귤 머리띠를 쓰면 기분까지 싱그럽게! 귤 한 개 드실래요?', true, 'HEADBAND', 'EPIC', 0.20, 50),
('headband_pineapple', '파인애플 껍질', '가람쌤이 먹다남긴 파인애플 껍질.. 난 왜 파인애플 안주냥!!', false, 'HEADBAND', 'EPIC', 0.20, 50),
('headband_sprout', '새싹 뾰족 머리띠', '싱그러운 새싹이 머리 위에서 쏘옥! 귀여움이 한층 업그레이드!', true, 'HEADBAND', 'RARE', 0.30, 30),
('color_blue', '하늘빛 페인트', '시원한 파란색 페인트로 물들여보자! 푸른 하늘 같은 기분!', true, 'PAINT', 'LEGENDARY', 0.01, 1000),
('color_brown', '모카 브라운 페인트', '고급스러운 갈색 페인트로 물들여보세요! 묵직한 분위기가 매력적!', true, 'PAINT', 'EPIC', 0.20, 50),
('color_gran', '그란 블렌드 페인트', '그란색 페인트로 나만의 특별한 스타일을 완성해보자!', true, 'PAINT', 'UNIQUE', 0.04, 500),
('color_gray', '시크 그레이 페인트', '세련된 회색 페인트로 변신! 차분하면서도 도시적인 느낌!', true, 'PAINT', 'RARE', 0.30, 30),
('color_navy', '딥 네이비 페인트', '깊이 있는 네이비 컬러로 스타일 업! 우아한 느낌이 물씬~', true, 'PAINT', 'LEGENDARY', 0.01, 1000),
('color_orange', '비타 오렌지 페인트', '에너지 넘치는 주황색 페인트! 발랄한 분위기가 살아난다!', true, 'PAINT', 'EPIC', 0.20, 50),
('color_pink', '러블리 핑크 페인트', '사랑스러운 핑크색 페인트로 로맨틱한 변신! 핑크 덕후 필수템!', true, 'PAINT', 'LEGENDARY', 0.01, 1000),
('balloon_devil', '악마 풍선', '규리풍선 달고 달려보아요!', true, 'BALLOON', 'LEGENDARY', 0.01, 1000),
('balloon_angel', '천사 풍선', '혜원 풍선과 함께 하늘을 날아보세요!!', true, 'BALLOON', 'LEGENDARY', 0.01, 1000),
('balloon_pink', '핑크 풍선', '보라색 풍선과 함께 하늘을 날아보세요!', true, 'BALLOON', 'NORMAL', 0.45, 10),
('headband_alienglass', '외계인 아무개의 선글라스', '착한 외계인 친구가 주고간 선글라스이다. 이걸 끼면 나도 외계인이다냥?', false, 'HEADBAND', 'EPIC', 0.20, 500),
('headband_alienglass2', '나일론 마스크의 선글라스', '나일론 마스크에게서 빼앗은 선글라스이다. 이걸 끼면 나도 마스크다냥?', false, 'HEADBAND', 'EPIC', 0.20, 500),
('headband_banggleglass', '뱅글라스', '이걸끼면 나도 뱅글뱅글.. 어.. 이느낌은 디스코팡팡이다냥!', false, 'HEADBAND', 'EPIC', 0.20, 500),
('headband_glass', '범생이의 안경', '음.. 전교1등 할거다냥!', false, 'HEADBAND', 'NORMAL', 0.45, 10),
('headband_sparklingeyes', '반짝반짝냥!', '츄르를 주거라냥! 지나가는 슬라임에게 구걸하는 법을 배웠다냐앙!', true, 'HEADBAND', 'UNIQUE', 0.04, 500),
('headband_wakwakhairband', '왁왁', '왁왁팀과 콜라보한 한정 아이템. 왁왁팀에 가서 보여주면 선물을 줄지도?!', true, 'HEADBAND', 'UNIQUE', 0.04, 500),
('ora_devil', '악마 마법진', '이 위에 올라가면... 누가 소환될 거 같은 이 느낌! 엇?! 규리다냥!!!', true, 'AURA', 'LEGENDARY', 0.01, 1000),
('ora_angel', '천사 마법진', '이 위에 올라가면... 누가 소환될 거 같은 이 느낌! 엇?! 혜원이다냥!!!', true, 'AURA', 'LEGENDARY', 0.01, 1000),
('balloon_devilwing', '악마의 날개', '혜원이의 등에서 떨어진 거 같은 날개다냥..', true, 'AURA', 'LEGENDARY', 0.01, 1000);

INSERT INTO levels (level, required_exp) VALUES
(1, 0),
(2, 400),
(3, 900),
(4, 1500),
(5, 2200),
(6, 3000),
(7, 3900),
(8, 4900),
(9, 6000),
(10, 7200),
(11, 8500),
(12, 9900),
(13, 11400),
(14, 13000),
(15, 14700),
(16, 16500),
(17, 18400),
(18, 20400),
(19, 22500),
(20, 24700),
(21, 27000),
(22, 29400),
(23, 31900),
(24, 34500),
(25, 37200),
(26, 40000),
(27, 42900),
(28, 45900),
(29, 49000),
(30, 52200),
(31, 55500),
(32, 58900),
(33, 62400),
(34, 66000),
(35, 69700),
(36, 73500),
(37, 77400),
(38, 81400),
(39, 85500),
(40, 89700),
(41, 94000),
(42, 98400),
(43, 102900),
(44, 107500),
(45, 112200),
(46, 117000),
(47, 121900),
(48, 126900),
(49, 132000),
(50, 137200);

INSERT INTO `boss` VALUES (1,'https://rocatrun-bucket.s3.ap-northeast-2.amazonaws.com/boss_img/img_-_monster2.png','사채업자 해파리',150,20,0,'1인당 4km',450,60,'모든 유저가 아이템 2회 사용',1800,1000),(2,'https://rocatrun-bucket.s3.ap-northeast-2.amazonaws.com/boss_img/img_-_monster3.png','땅콩수집 로봇',300,40,1,'1인당 5km',750,100,'모든 유저가 아이템 2회 사용',1800,1250),(3,'https://rocatrun-bucket.s3.ap-northeast-2.amazonaws.com/boss_img/img_-_monster1.png','나일론 마스크',525,70,2,'1인당 6km',1200,160,'모든 유저가 아이템 2회 사용',1800,1500);
