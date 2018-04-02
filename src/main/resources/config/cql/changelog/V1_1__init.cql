CREATE INDEX IF NOT EXISTS ms_name_entity_and_date ON timeline_by_entity_and_date (ms_name);
CREATE INDEX IF NOT EXISTS ms_name_entity_and_operation_and_date ON timeline_by_entity_and_operation_and_date (ms_name);
CREATE INDEX IF NOT EXISTS ms_name_user_and_date ON timeline_by_user_and_date (ms_name);
CREATE INDEX IF NOT EXISTS ms_name_user_and_operation_and_date ON timeline_by_user_and_operation_and_date (ms_name);
