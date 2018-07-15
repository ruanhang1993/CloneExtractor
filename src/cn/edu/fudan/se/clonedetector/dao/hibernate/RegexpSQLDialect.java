package cn.edu.fudan.se.clonedetector.dao.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;

public class RegexpSQLDialect extends MySQLDialect {
	public RegexpSQLDialect() {
        super();
        /**
         * Function to evaluate regexp in MySQL
         */
        registerFunction("regexp", new SQLFunctionTemplate(Hibernate.INTEGER, "?1 REGEXP ?2"));
    }
}
