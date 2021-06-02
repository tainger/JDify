package io.terminus.dalaran.console.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class DeleteLogRetentionPolicy implements LogRetentionPolicy {

    private Integer duration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DeleteLogRetentionPolicy(Integer duration) {
        this.duration = duration;
    }

    @Override
    public void processing() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -duration);
        String date = dateFormat.format(now.getTime());
        String delete = "delete from dalaran_tracing_log where created_at < '" + date + "'";
        jdbcTemplate.setQueryTimeout(600);
        jdbcTemplate.execute(delete);
    }
}
