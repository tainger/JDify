package io.terminus.dalaran.console.log;

import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteLogRetentionPolicy implements LogRetentionPolicy {

    @Autowired
    private TracingLogRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Integer duration;

    public DeleteLogRetentionPolicy(Integer duration) {
        this.duration = duration;
    }

    @Override
    @Transactional
    public void processing() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        String rename = "ALTER TABLE dalaran_tracing_log RENAME dalaran_tracing_log_" + date;
        String create = "CREATE TABLE `dalaran_tracing_log` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `elapsed` bigint(20) NOT NULL,\n" +
                "  `flow_id` bigint(20) NOT NULL,\n" +
                "  `input_body` longtext COLLATE utf8mb4_unicode_ci,\n" +
                "  `input_body_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `main` bit(1) NOT NULL,\n" +
                "  `module_id` bigint(20) DEFAULT NULL,\n" +
                "  `output_body` longtext COLLATE utf8mb4_unicode_ci,\n" +
                "  `output_body_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `processor_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n" +
                "  `record_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',\n" +
                "  `successful` bit(1) NOT NULL,\n" +
                "  `timestamp` bigint(20) NOT NULL,\n" +
                "  `tracing_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                "  `main_record_id` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n" +
                "  `child_record_id` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `recordId` (`record_id`),\n" +
                "  KEY `flowId` (`flow_id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        jdbcTemplate.execute(rename);
        jdbcTemplate.execute(create);
//        Calendar now = Calendar.getInstance();
//        now.add(Calendar.DAY_OF_MONTH, -duration);
//        repository.deleteByCreatedAtBefore(now.getTime());
    }
}
