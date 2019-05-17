package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.ZData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface ZDataDao extends JpaRepository<ZData, String> {
    DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("H:m:s.S");
    DateTimeFormatter TOT = DateTimeFormatter.ofPattern("yyyy-M-d");

//	private static final String CREATE_SQL = "CREATE TABLE `%s` ( " + "  `id` bigint(20) NOT NULL AUTO_INCREMENT, "
//			+ "  `switchID` int(11) NOT NULL, " + "  `signalsTypeID` smallint(6) NOT NULL, "
//			+ "  `year` year(4) NOT NULL, " + "  `time` time(3) NOT NULL, " + "  `value` double NOT NULL, "
//			+ "  PRIMARY KEY (`id`,`switchID`), " + "  KEY `idx_value` (`switchID`,`signalsTypeID`,`year`,`time`) "
//			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据表(DayOfYear=%d)' "
//			+ "/*!50100 PARTITION BY HASH (`switchID`) " + "PARTITIONS 100 */";

    /**
     * 获取对应某天的数据分表表名。
     *
     * @param doy 一年中的哪一天：DayOfYear(1-366)。
     * @return 对应某天的数据分表表名。
     */
    public static String getZDataTableName(int doy) {
        return String.format("zdata_%03d", doy);
    }

    /**
     * 获取对应某天的数据分表表名。
     *
     * @param dt 该天的日期。
     * @return 对应某天的数据分表表名。
     */
    public static String getZDataTableName(LocalDateTime dt) {
        return getZDataTableName(dt.getDayOfYear());
    }

//    /**
//     * 创建对应每天(DayOfYear)的数据分表。
//     */
//	private static void createZDatas() {
//		Connection conn = null;
//		Statement stmt = null;
//		String sql;
//		try {
//			conn = JDBCMysql.getConnection("116.62.38.203", "power_manager", "root", "root123456");
//			conn.setAutoCommit(false);
//			stmt = conn.createStatement();
//			for (int i = 1; i <= 366; i++) {
//				sql = String.format(CREATE_SQL, getZDataTableName(i), i);
//				stmt.execute(sql);
//			}
//			conn.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//				conn = null;
//			}
//		}
//	}

    /**
     * 保存数据记录。
     *
     * @param tableName      表名
     * @param switchID       switchID
     * @param signalsTypeIDs signalsTypeID
     * @param mYear          数据时间year
     * @param mTime          数据时间time
     * @param mValue         数据值
     */
    default int addData(EntityManager entityManager, String tableName, int switchID, short signalsTypeIDs, int mYear, String mTime, double mValue) throws Exception {
    	String sql = String.format("INSERT INTO :tableName (switchID,signalsTypeID,`year`,`time`,`value`) VALUES (%d,%d,%d,'%s',%f)",
    			switchID, signalsTypeIDs, mYear, mTime, mValue);
        return entityManager.createNativeQuery(sql).executeUpdate();
    }

    /**
     * APP查询
     *
     * @param switchID      switchID
     * @param signalsTypeID signalsTypeID
     * @param mYear
     * @return
     */
    @SuppressWarnings("unchecked")
	default List<ZData> findBySwitchAndSignalsTypeByDay(EntityManager entityManager, String tableName, Integer switchID, Short signalsTypeID, Integer mYear) throws Exception {
    	String sql = String.format("SELECT * FROM %s WHERE switchID = %d AND signalsTypeID = %d AND `year` = %d ORDER BY time ASC",
    			tableName, switchID, signalsTypeID, mYear);
    	return entityManager.createNativeQuery(sql, ZData.class).getResultList();
    }

//	public static void main(String[] args) {
//		createZDatas();
//	}

    /**
     * 保存多个数据记录。
     *
     * @param dt             数据时间
     * @param switchID       switchID
     * @param signalsTypeIDs 各signalsTypeID
     * @param values         各对应的数据值
     * @param count          数据个数
     * @throws Exception
     */
    default int addData(EntityManager entityManager, final LocalDateTime dt, int switchID, short[] signalsTypeIDs, double[] values, int count) throws Exception {
        if (count <= 0)
            return count;
        int year = dt.getYear();
        String stime = "'" + dt.format(TIMEFORMAT) + "'";
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(getZDataTableName(dt));
        sb.append("(switchID,signalsTypeID,year,time,value) VALUES ");
        for (int i = 0; i < count; i++) {
            if (i > 0)
                sb.append(",");
            sb.append("(");
            sb.append(switchID);
            sb.append(",");
            sb.append(signalsTypeIDs[i]);
            sb.append(",");
            sb.append(year);
            sb.append(",");
            sb.append(stime);
            sb.append(",");
            sb.append(values[i]);
            sb.append(")");
        }
        return entityManager.createNativeQuery(sb.toString()).executeUpdate();
    }
}
