CREATE DATABASE IF NOT EXISTS heat_pipeline DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE heat_pipeline;

CREATE TABLE IF NOT EXISTS heat_pipe (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 pipe_code VARCHAR(40) NOT NULL UNIQUE,
 pipe_name VARCHAR(100) NOT NULL,
 area VARCHAR(100), diameter VARCHAR(30), material VARCHAR(80),
 length_km DECIMAL(10,2) DEFAULT 0,
 design_pressure DECIMAL(8,2) DEFAULT 1.60,
 install_year INT,
 status VARCHAR(30) DEFAULT '运行中',
 remark VARCHAR(255),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS heat_station (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 station_code VARCHAR(40) NOT NULL UNIQUE,
 station_name VARCHAR(100) NOT NULL,
 area VARCHAR(100),
 supply_temp DECIMAL(6,2) DEFAULT 0,
 return_temp DECIMAL(6,2) DEFAULT 0,
 pressure DECIMAL(6,3) DEFAULT 0,
 flow_rate DECIMAL(10,2) DEFAULT 0,
 status VARCHAR(30) DEFAULT '运行中',
 contact_person VARCHAR(50),
 phone VARCHAR(30)
);
CREATE TABLE IF NOT EXISTS heat_alarm (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 alarm_code VARCHAR(40),
 alarm_type VARCHAR(60),
 device_name VARCHAR(100),
 area VARCHAR(100),
 alarm_level VARCHAR(30),
 alarm_time DATETIME DEFAULT CURRENT_TIMESTAMP,
 description VARCHAR(255),
 status VARCHAR(30) DEFAULT '待处理',
 handler VARCHAR(50),
 handle_time DATETIME
);
CREATE TABLE IF NOT EXISTS monitor_point (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 point_name VARCHAR(100),
 area VARCHAR(100),
 supply_temp DECIMAL(6,2),
 return_temp DECIMAL(6,2),
 pressure DECIMAL(6,3),
 flow_rate DECIMAL(10,2),
 status VARCHAR(30),
 update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO heat_pipe(pipe_code,pipe_name,area,diameter,material,length_km,design_pressure,install_year,status) VALUES
('HP-001','城东主干线','城东片区','DN600','预制直埋保温管',8.60,1.60,2019,'运行中'),
('HP-002','滨河支线','滨河片区','DN400','螺旋焊接钢管',5.20,1.60,2020,'运行中'),
('HP-003','高新园区供热管','高新区','DN500','预制直埋保温管',6.80,1.60,2021,'运行中'),
('HP-004','北城联络线','北城片区','DN350','无缝钢管',3.40,1.25,2016,'检修中'),
('HP-005','南站热力支线','南站片区','DN300','预制直埋保温管',2.70,1.25,2022,'运行中');

INSERT IGNORE INTO heat_station(station_code,station_name,area,supply_temp,return_temp,pressure,flow_rate,status,contact_person,phone) VALUES
('HS-001','城东第一换热站','城东片区',84.2,46.8,0.860,328,'运行中','张工','13800000001'),
('HS-002','滨河换热站','滨河片区',81.6,45.2,0.790,256,'运行中','李工','13800000002'),
('HS-003','高新园区换热站','高新区',83.1,47.1,0.830,301,'运行中','王工','13800000003'),
('HS-004','北城换热站','北城片区',78.4,44.0,0.680,0,'检修中','赵工','13800000004'),
('HS-005','南站换热站','南站片区',82.4,46.1,0.810,198,'运行中','陈工','13800000005');

INSERT INTO heat_alarm(alarm_code,alarm_type,device_name,area,alarm_level,alarm_time,description,status)
SELECT 'AL-20261118-001','供水温度偏高','城东第一换热站','城东片区','一般',NOW(),'供水温度达到 88.5℃，超过设定阈值','待处理'
WHERE NOT EXISTS (SELECT 1 FROM heat_alarm WHERE alarm_code='AL-20261118-001');
INSERT INTO heat_alarm(alarm_code,alarm_type,device_name,area,alarm_level,alarm_time,description,status)
SELECT 'AL-20261118-002','管网压力波动','滨河支线压力传感器','滨河片区','重要',DATE_SUB(NOW(),INTERVAL 35 MINUTE),'压力短时波动，请检查阀门状态','待处理'
WHERE NOT EXISTS (SELECT 1 FROM heat_alarm WHERE alarm_code='AL-20261118-002');
INSERT INTO heat_alarm(alarm_code,alarm_type,device_name,area,alarm_level,alarm_time,description,status)
SELECT 'AL-20261118-003','设备离线','北城换热站采集终端','北城片区','一般',DATE_SUB(NOW(),INTERVAL 2 HOUR),'采集终端暂未上报数据','已处理'
WHERE NOT EXISTS (SELECT 1 FROM heat_alarm WHERE alarm_code='AL-20261118-003');

INSERT INTO monitor_point(point_name,area,supply_temp,return_temp,pressure,flow_rate,status,update_time)
SELECT '城东主干线测点01','城东片区',84.2,46.8,0.860,328,'正常',NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_point WHERE point_name='城东主干线测点01');
INSERT INTO monitor_point(point_name,area,supply_temp,return_temp,pressure,flow_rate,status,update_time)
SELECT '滨河支线测点02','滨河片区',81.6,45.2,0.790,256,'正常',NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_point WHERE point_name='滨河支线测点02');
INSERT INTO monitor_point(point_name,area,supply_temp,return_temp,pressure,flow_rate,status,update_time)
SELECT '高新园区测点03','高新区',83.1,47.1,0.830,301,'正常',NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_point WHERE point_name='高新园区测点03');
INSERT INTO monitor_point(point_name,area,supply_temp,return_temp,pressure,flow_rate,status,update_time)
SELECT '北城联络线测点04','北城片区',78.4,44.0,0.680,0,'离线',NOW()
WHERE NOT EXISTS (SELECT 1 FROM monitor_point WHERE point_name='北城联络线测点04');

-- 专业升级版新增业务表（重复执行不会删除原有数据）
CREATE TABLE IF NOT EXISTS sys_user (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE,
 password VARCHAR(100) NOT NULL, real_name VARCHAR(50), role_name VARCHAR(30) DEFAULT '管理员',
 enabled TINYINT DEFAULT 1, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS work_order (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, order_code VARCHAR(40) NOT NULL UNIQUE,
 title VARCHAR(120) NOT NULL, area VARCHAR(100), priority VARCHAR(20) DEFAULT '一般',
 assignee VARCHAR(50), description VARCHAR(500), status VARCHAR(20) DEFAULT '待派工',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME
);
CREATE TABLE IF NOT EXISTS patrol_record (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, station_name VARCHAR(100), inspector VARCHAR(50),
 result VARCHAR(30), note VARCHAR(500), patrol_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS alarm_rule (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, rule_name VARCHAR(100), metric VARCHAR(40),
 operator VARCHAR(5), threshold_value DECIMAL(10,2), alarm_level VARCHAR(20), enabled TINYINT DEFAULT 1
);
INSERT IGNORE INTO sys_user(username,password,real_name,role_name,enabled) VALUES('admin','admin123','系统管理员','超级管理员',1);
INSERT INTO alarm_rule(rule_name,metric,operator,threshold_value,alarm_level,enabled)
SELECT '供水温度高温预警','供水温度','>',90,'重要',1 WHERE NOT EXISTS(SELECT 1 FROM alarm_rule WHERE rule_name='供水温度高温预警');
INSERT INTO alarm_rule(rule_name,metric,operator,threshold_value,alarm_level,enabled)
SELECT '回水温度低温提示','回水温度','<',35,'一般',1 WHERE NOT EXISTS(SELECT 1 FROM alarm_rule WHERE rule_name='回水温度低温提示');
INSERT INTO alarm_rule(rule_name,metric,operator,threshold_value,alarm_level,enabled)
SELECT '管网压力高压预警','压力','>',1.80,'紧急',1 WHERE NOT EXISTS(SELECT 1 FROM alarm_rule WHERE rule_name='管网压力高压预警');
INSERT INTO alarm_rule(rule_name,metric,operator,threshold_value,alarm_level,enabled)
SELECT '流量低值提醒','流量','<',50,'一般',1 WHERE NOT EXISTS(SELECT 1 FROM alarm_rule WHERE rule_name='流量低值提醒');
