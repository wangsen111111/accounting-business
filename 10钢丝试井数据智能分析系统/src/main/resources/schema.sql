CREATE TABLE IF NOT EXISTS well_info(id BIGINT PRIMARY KEY AUTO_INCREMENT,well_code VARCHAR(60) NOT NULL,well_name VARCHAR(120) NOT NULL,block_name VARCHAR(120),well_type VARCHAR(60),location VARCHAR(200),status VARCHAR(30) DEFAULT '在用',created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS test_job(id BIGINT PRIMARY KEY AUTO_INCREMENT,well_id BIGINT,test_no VARCHAR(80),test_date VARCHAR(30),operator VARCHAR(60),tool_model VARCHAR(100),max_depth DOUBLE,cable_tension DOUBLE,conclusion VARCHAR(500),analysis_result VARCHAR(500),risk_level VARCHAR(30),created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS wireline_data(id BIGINT PRIMARY KEY AUTO_INCREMENT,well_id BIGINT,sample_time VARCHAR(40),depth DOUBLE,tension DOUBLE,speed DOUBLE,pressure_value DOUBLE,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS analysis_alert(id BIGINT PRIMARY KEY AUTO_INCREMENT,test_id BIGINT,alert_level VARCHAR(30),alert_title VARCHAR(120),alert_detail VARCHAR(500),status VARCHAR(30),created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS analysis_rule(id BIGINT PRIMARY KEY AUTO_INCREMENT,rule_name VARCHAR(100),metric VARCHAR(60),threshold_value DOUBLE,unit VARCHAR(30),description VARCHAR(300));
INSERT INTO analysis_rule(rule_name,metric,threshold_value,unit,description) SELECT '钢丝张力预警','cable_tension',12,'kN','超过阈值生成关注提示' WHERE NOT EXISTS(SELECT 1 FROM analysis_rule);
INSERT INTO well_info(well_code,well_name,block_name,well_type,location,status) SELECT 'W-001','示例试验井 A1','东部区块','生产井','示例区域','在用' WHERE NOT EXISTS(SELECT 1 FROM well_info);
INSERT INTO test_job(well_id,test_no,test_date,operator,tool_model,max_depth,cable_tension,conclusion) SELECT 1,'WL-2026-001','2026-11-16','张工','常规钢丝工具串',2850,10.6,'测试完成，数据待复核' WHERE NOT EXISTS(SELECT 1 FROM test_job);

-- 采集点数据表可存储现场采集的深度、张力、速度及压力序列
INSERT INTO wireline_data(well_id,sample_time,depth,tension,speed,pressure_value)
SELECT 1,'2026-11-16 09:00:00',500,8.5,16,7.2 WHERE NOT EXISTS(SELECT 1 FROM wireline_data);
INSERT INTO wireline_data(well_id,sample_time,depth,tension,speed,pressure_value)
SELECT 1,'2026-11-16 09:05:00',750,9.1,17,7.5 WHERE (SELECT count(*) FROM wireline_data)<2;
INSERT INTO wireline_data(well_id,sample_time,depth,tension,speed,pressure_value)
SELECT 1,'2026-11-16 09:10:00',1000,11.8,18,8.1 WHERE (SELECT count(*) FROM wireline_data)<3;
INSERT INTO wireline_data(well_id,sample_time,depth,tension,speed,pressure_value)
SELECT 1,'2026-11-16 09:15:00',1250,13.2,19,8.4 WHERE (SELECT count(*) FROM wireline_data)<4;

CREATE TABLE IF NOT EXISTS operation_log(
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 action_type VARCHAR(60),
 action_detail VARCHAR(1000),
 operator_name VARCHAR(60),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
