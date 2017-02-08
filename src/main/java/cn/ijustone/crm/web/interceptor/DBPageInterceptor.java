package cn.ijustone.crm.web.interceptor;

import com.google.common.collect.Lists;

import cn.ijustone.crm.utils.page.ConfigDBType;
import cn.ijustone.crm.utils.page.PageResource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * 提供自实现的mysql 分页拦截实现,以提供更简单的分页实现,自动计算总记录数
 * 
 * @author iJustONE
 *
 */
// @Intercepts({ @Signature(method = "prepare", type = StatementHandler.class,
// args = { Connection.class }) })
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,	RowBounds.class, ResultHandler.class }) })
public class DBPageInterceptor implements Interceptor {

	private static final String START_INDEX = "startIndex_";
	private static final String END_INDEX = "endIndex_";
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;

	/*数据库类型*/
	private DB_TYPE dbType ;
	
	/**内部枚举类*/
	private enum DB_TYPE {

		ORACLE("Oracle"), MYSQL("MySQL");

		private String type;

		DB_TYPE(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	/**
	 * 不分页,或针对已经分页查询的数据
	 */
	private static final RowBounds NO_ROW_BOUNDS = RowBounds.DEFAULT;

	/**
	 *   ====网上方法实现说明(实现已注释)====
	 * 	1.对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler， 
	 *    1.1	BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，  
	 * 		1.1.1	SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，
    					而CallableStatementHandler是处理CallableStatement的。
	 *	2.Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个StatementHandler类型的delegate属性，
    			RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、PreparedStatementHandler或CallableStatementHandler，
    			在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。  
     *  3.我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，
     *  	又因为Mybatis只有在建立RoutingStatementHandler的时候 是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。
     */
	public Object intercept(Invocation invocation) throws Throwable {
		processIntercept((Executor) invocation.getTarget(), invocation.getArgs());
		return invocation.proceed();
	}

	void processIntercept(Executor executor, final Object[] queryArgs) throws SQLException {
		final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
		// 只有提供分页信息时,才处理
		if (rowBounds == null)
			return;
		if (rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT)
			return;
		//判断是否是分页
		boolean isPage = rowBounds instanceof PageResource;

		 //判断数据库类型是否支持设定的分页操作
        if (DB_TYPE.ORACLE.getType().equals(ConfigDBType.DB_TYPE)) {
            this.dbType = DB_TYPE.ORACLE;
        } else if (DB_TYPE.MYSQL.getType().equals(ConfigDBType.DB_TYPE)) {
            this.dbType = DB_TYPE.MYSQL;
        } else {
            throw new RuntimeException("不支持ORACLE、MYSQL之外的数据库分页.");
        }
        
		MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		Object parameter = queryArgs[PARAMETER_INDEX];
		BoundSql oldBoundSql = ms.getBoundSql(parameter);
		String oldSql = oldBoundSql.getSql().trim();

		// 处理总记录数
		if (isPage) {
			PageResource page = (PageResource) rowBounds;
			// 只有需要查询总数的时候才查询,避免重复查询
			if (page.getTotal() == 0) {
				String countSql = getCountSql(oldSql, rowBounds);
				BoundSql countBoundSql = buildNewBoundSql(ms, oldBoundSql, countSql, NO_ROW_BOUNDS);
				// 这时将要resultType设置为long类型,以取得总记录数信息
				ResultMap pageResultMap = fetchPageResultMap(ms.getConfiguration());
				MappedStatement countMappedStatement = buildNewStatement(ms, new BoundSqlSqlSource(countBoundSql),
						Collections.singletonList(pageResultMap));
				List<Long> list = executor.query(countMappedStatement, parameter, NO_ROW_BOUNDS,
						Executor.NO_RESULT_HANDLER);
				long totalCount = list.get(0);
				page.setTotal(totalCount);
			}
		}

		// 避免结果集再处理一次
		queryArgs[ROWBOUNDS_INDEX] = NO_ROW_BOUNDS;

		// 处理记录信息
		String pageSql = getPageSql(oldSql, rowBounds);
		BoundSql pageBoundSql = buildNewBoundSql(ms, oldBoundSql, pageSql, rowBounds);
		MappedStatement newMs = buildNewStatement(ms, new BoundSqlSqlSource(pageBoundSql), ms.getResultMaps());
		queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
	}

	
	private ResultMap fetchPageResultMap(Configuration configuration) {
		
		//映射文件
		return configuration.getResultMap("WebCrm5Page.pageCount");
	}

	private BoundSql buildNewBoundSql(MappedStatement ms, BoundSql boundSql, String sql, RowBounds rowBounds) {
		List<ParameterMapping> parameterMapping = boundSql.getParameterMappings();

		if (parameterMapping.isEmpty()) {
			parameterMapping = Lists.newArrayList();// 避免出现不能添加参数的问题
		}
		// 为了不改变原始SQL参数，所以重新构造一个SQL参数对象
		List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>();
		newParameterMappings.addAll(parameterMapping);

		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, newParameterMappings,
				boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}

		// 处理额外的分页参数
		if (rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT) {
			PageResource page = (PageResource) rowBounds;
			if (rowBounds.getOffset() > 0) {
				// 如果有起始点, 顺序为 <= end && > start
				newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), END_INDEX, int.class).build());
				newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), START_INDEX, int.class).build());
			} else {
				// 没有起点 顺序为 <= end
				if (this.dbType == DB_TYPE.MYSQL) {
					newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), START_INDEX, int.class).build());
				}
				newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), END_INDEX, int.class).build());
			}

			if (this.dbType == DB_TYPE.ORACLE) {
				newBoundSql.setAdditionalParameter(START_INDEX, rowBounds.getOffset());
				newBoundSql.setAdditionalParameter(END_INDEX, rowBounds.getOffset() + rowBounds.getLimit());
			} else if (this.dbType == DB_TYPE.MYSQL) {
				newBoundSql.setAdditionalParameter(START_INDEX, (page.getPageNum() - 1) * page.getPageCount());
				newBoundSql.setAdditionalParameter(END_INDEX, page.getPageCount());
			} else {
				throw new RuntimeException("不支持ORACLE、MYSQL之外的数据库分页.");
			}
		}
		return newBoundSql;
	}

	private MappedStatement buildNewStatement(MappedStatement ms, SqlSource newSqlSource,List<ResultMap> resultMapList) {
		final Configuration configuration = ms.getConfiguration();

		MappedStatement.Builder builder = new MappedStatement.Builder(configuration, ms.getId(), newSqlSource,ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		builder.keyProperty(StringUtils.join(ms.getKeyProperties(), ","));
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(resultMapList);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		// nothing to do
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	private String getCountSql(String sql, RowBounds bounds) {
		if (this.dbType == DB_TYPE.ORACLE) {
			return "select count(1) from (" + sql + ")";
		} else if (this.dbType == DB_TYPE.MYSQL) {
			return "select count(1) from (" + sql + ") tmp";
		} else {
			throw new RuntimeException("不支持ORACLE、MYSQL之外的数据库分页.");
		}
	}

	private String getPageSql(String sql, RowBounds bounds) {
		PageResource page = (PageResource) bounds;
		if (this.dbType == DB_TYPE.ORACLE) {
			if (sql.toLowerCase().endsWith(" for update")) {
				throw new RuntimeException("不支持for update操作");
			}
			int startIndex = page.getOffset();
			StringBuffer select = new StringBuffer(sql.length() + 100);
			if (startIndex > 0) {
				select.append("select * from ( select row_.*, rownum rownum_ from ( ");
				select.append(sql);
				select.append(") row_ where rownum <= ? ) where rownum_ > ? ");
			} else {
				select.append("select * from ( ");
				select.append(sql);
				select.append(") where rownum <= ? ");
			}
			return select.toString();
		} else if (this.dbType == DB_TYPE.MYSQL) {
			StringBuffer select = new StringBuffer(sql.length() + 100);
			select.append(sql);
			select.append(" limit ?,?");
			return select.toString();
		} else {
			throw new RuntimeException("不支持ORACLE、MYSQL之外的数据库分页.");
		}
	}

}
