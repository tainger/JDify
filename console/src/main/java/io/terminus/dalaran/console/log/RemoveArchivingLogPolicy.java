package io.terminus.dalaran.console.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
public class RemoveArchivingLogPolicy implements LogRetentionPolicy {

    private final Integer duration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String dbName;

    public RemoveArchivingLogPolicy(Integer duration, String dbName) {
        this.duration = duration;
        this.dbName = dbName;
    }

    @Override
    public void processing() {
        String date = getRemoveTime(duration);
        String sql = "select table_name from information_schema.tables where table_schema= '%s';";
        String preparedSql = String.format(sql, dbName);
        List<String> tableNameList = jdbcTemplate.queryForList(preparedSql, String.class);
        List<String> filteredTableNameList = filterTable(tableNameList, Long.valueOf(date));
        String tableName = StringUtils.join(filteredTableNameList, ",");
        if (!StringUtils.isEmpty(tableName)) {
            String deleteSql = "DROP table %s;";
            String preparedDeleteSql = String.format(deleteSql, tableName);
            log.info("sql:{}", preparedDeleteSql);
            jdbcTemplate.execute(preparedDeleteSql);
        }
    }

    private String getRemoveTime(Integer duration) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -duration);
        return simpleDateFormat.format(now.getTime());
    }

    private List<String> filterTable(List<String> tableNameList, Long criticalTime) {
        List<String> tableTobeDeleted = new ArrayList<>();
        for (String tableName : tableNameList) {
            String log_name = "dalaran_tracing_log_";
            if (tableName.startsWith(log_name)) {
                String substring = tableName.substring(20);
                Long whenGenerated = Long.valueOf(substring);
                if (whenGenerated <= criticalTime) {
                    tableTobeDeleted.add(tableName);
                }
            }
        }
        return tableTobeDeleted;
    }
}
