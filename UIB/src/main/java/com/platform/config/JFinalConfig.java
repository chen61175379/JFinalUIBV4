package com.platform.config;

import java.util.Map;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.tx.TxByActionKeyRegex;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.jfinal.plugin.activerecord.tx.TxByMethodRegex;
import com.jfinal.plugin.activerecord.tx.TxByMethods;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;
import com.platform.constant.ConstantCache;
import com.platform.constant.ConstantInit;
import com.platform.dto.DataBase;
import com.platform.dto.Redis;
import com.platform.handler.GlobalHandler;
import com.platform.interceptor.AuthInterceptor;
import com.platform.interceptor.ParamPkgInterceptor;
import com.platform.interceptor.XSSInterceptor;
import com.platform.plugin.FileRenamePlugin;
import com.platform.plugin.I18NPlugin;
import com.platform.plugin.ParamInitPlugin;
import com.platform.plugin.QuartzPlugin;
import com.platform.plugin.ServicePlugin;
import com.platform.plugin.SqlXmlPlugin;
import com.platform.thread.ThreadSysLog;
import com.platform.thread.job.DataClearJob;
import com.platform.thread.job.DbBackupJob;
import com.platform.thread.job.ResourcesJob;
import com.platform.tools.ToolBeetl;
import com.platform.tools.ToolCache;
import com.platform.tools.ToolDataBase;
import com.platform.tools.ToolRedis;
import com.platform.tools.ToolString;

/**
 * JFinal API ?????????????????????????????????????????????????????????????????????????????????
 */
public class JFinalConfig extends com.jfinal.config.JFinalConfig {
	
	private static final Log log = Log.getLog(JFinalConfig.class);
	
	/**
	 * ????????????
	 */
	public void configConstant(Constants constants) {
		if(log.isInfoEnabled()) log.info("configConstant ?????? properties");
    	PropKit.use("init.properties");

		if(log.isInfoEnabled()) log.info("configConstant ???????????????");
		constants.setEncoding(ToolString.encoding); 

		if(log.isInfoEnabled()) log.info("configConstant ????????????????????????");
		constants.setDevMode(PropKit.getBoolean(ConstantInit.config_devMode, false));

		//if(log.isInfoEnabled()) log.info("configConstant ??????json?????????");
		//constants.setJsonFactory(JFinalJsonFactory.me()); // ??????????????????
		//constants.setJsonFactory(FastJsonFactory.me()); // FastJson
		//constants.setJsonFactory(JacksonFactory.me()); // Jackson
		
		//if(log.isInfoEnabled()) log.info("configConstant ??????log?????????");
		//constants.setLogFactory(new LogBackFactory()); // ??????Log4jLogFactory???Log4jLog??????LogBackFactory???LogBackLog

		if(log.isInfoEnabled()) log.info("configConstant ??????path??????");
		constants.setBaseUploadPath(PathKit.getWebRootPath()); // ?????????????????? + File.separator + "files"
		constants.setBaseDownloadPath(PathKit.getWebRootPath()); // ?????????????????? + File.separator + "files"
		
		if(log.isInfoEnabled()) log.info("configConstant ??????Beetl??????");
		ToolBeetl.brf.config();
		constants.setRenderFactory(ToolBeetl.brf);
		ToolBeetl.regiseter();
		
		if(log.isInfoEnabled()) log.info("configConstant ??????error page??????");
		constants.setError404View("/platform/common/404.html");
		constants.setError500View("/platform/common/500.html");

		if(log.isInfoEnabled()) log.info("configConstant i18n????????????????????????");
		constants.setI18nDefaultBaseName(PropKit.get(ConstantInit.config_i18n_filePrefix));
		//constants.setI18nDefaultLocale("zh_CN");		

		if(log.isInfoEnabled()) log.info("configConstant formTaken????????????"); // JFinal???????????????session?????????????????????session??????????????????ehcache
		if(ToolCache.getCacheType().equals(ConstantCache.cache_type_ehcache)){
			constants.setTokenCache(new FormTokenByEhcache());
		}else if(ToolCache.getCacheType().equals(ConstantCache.cache_type_redis)){
			constants.setTokenCache(new FormTokenByRedis());
		}
		
		if(log.isInfoEnabled()) log.info("configConstant ????????????sql?????????Log???????????????????????????");
		if(constants.getDevMode()){
			SqlReporter.setLog(true);
		}
	}
	
	/**
	 * ????????????
	 */
	public void configRoute(Routes routes) { 
		// routes.setBaseViewPath("/platform"); // ????????????????????????????????????????????????Routes?????????Routes????????????????????????
		if(log.isInfoEnabled()) log.info("configRoute ??????????????????");
		routes.add(new RoutesScan());
	}

	@Override
	public void configEngine(Engine me) {
		
	}

	/**
	 * ????????????
	 */
	public void configPlugin(Plugins plugins) {
		if(log.isInfoEnabled()) log.info("??????paltform ActiveRecordPlugin");
		
		Map<String, DataBase> dbMap = ToolDataBase.getDbMap();
		for (String name : dbMap.keySet()) {
			DataBase db = dbMap.get(name);
			String db_type = db.getType();

			if(log.isInfoEnabled()) log.info("configPlugin ??????Druid??????????????????????????????");
			DruidPlugin druidPlugin = new DruidPlugin(db.getJdbcUrl(), db.getUserName(), db.getPassWord(), db.getDriverClass());

			if(log.isInfoEnabled()) log.info("configPlugin ??????Druid????????????????????????");
			druidPlugin.set(db.getInitialSize(), db.getMinIdle(), db.getMaxActive());
			
			if(log.isInfoEnabled()) log.info("configPlugin ??????Druid?????????????????????????????????");
			druidPlugin.addFilter(new StatFilter());
			WallFilter wall = new WallFilter();
			wall.setDbType(db_type);
			WallConfig config = new WallConfig();
			config.setFunctionCheck(false); // ?????????????????????
			wall.setConfig(config);
			druidPlugin.addFilter(wall);
			
			if(log.isInfoEnabled()) log.info("configPlugin ??????ActiveRecordPlugin??????");
			ActiveRecordPlugin arp = new ActiveRecordPlugin(name, druidPlugin);
			/**
			 * Connection.TRANSACTION_READ_UNCOMMITTED ?????????????????????????????????????????????????????????3????????????????????????
			 * Connection.TRANSACTION_READ_COMMITTED ?????????????????????????????????
			 * Connection.TRANSACTION_REPEATABLE_READ ???????????????????????????????????????
			 * Connection.TRANSACTION_SERIALIZABLE ???????????????????????????3???????????????????????????????????????
			 */
			//arp.setTransactionLevel(4);//????????????????????????4
			boolean devMode = Boolean.parseBoolean(PropKit.get(ConstantInit.config_devMode));
			arp.setDevMode(devMode); // ??????????????????
			arp.setShowSql(devMode); // ????????????SQL
			arp.setContainerFactory(new CaseInsensitiveContainerFactory(true)); // ??????????????????

//			arp.setBaseSqlTemplatePath(PathKit.getRootClassPath()); // ??????sql???????????????????????????
//			arp.addSqlTemplate("com/platform/mvc/user/User.sql"); // ??????sql??????
			
			if(log.isInfoEnabled()) log.info("configPlugin ?????????????????????");
			if(db_type.equals(ConstantInit.db_type_postgresql)){
				if(log.isInfoEnabled()) log.info("configPlugin ???????????????????????? postgresql");
				arp.setDialect(new PostgreSqlDialect());
				
			}else if(db_type.equals(ConstantInit.db_type_mysql)){
				if(log.isInfoEnabled()) log.info("configPlugin ???????????????????????? mysql");
				arp.setDialect(new MysqlDialect());
				
			}else if(db_type.equals(ConstantInit.db_type_oracle)){
				if(log.isInfoEnabled()) log.info("configPlugin ???????????????????????? oracle");
				druidPlugin.setValidationQuery("select 1 FROM DUAL"); //??????????????????
				arp.setDialect(new OracleDialect());
				
			}else if(db_type.equals(ConstantInit.db_type_sqlserver)){
				if(log.isInfoEnabled()) log.info("configPlugin ???????????????????????? sqlserver");
				arp.setDialect(new SqlServerDialect());
				
			}else if(db_type.equals(ConstantInit.db_type_db2)){
				if(log.isInfoEnabled()) log.info("configPlugin ???????????????????????? db2");
				druidPlugin.setValidationQuery("select 1 from sysibm.sysdummy1"); //??????????????????
				arp.setDialect(new AnsiSqlDialect());
			}

			if(log.isInfoEnabled()) log.info("configPlugin ?????????????????????");
			ModelScan.scan(name, arp);

			if(log.isInfoEnabled()) log.info("configPlugin ??????druidPlugin??????");
			plugins.add(druidPlugin);

			if(log.isInfoEnabled()) log.info("configPlugin ??????ActiveRecordPlugin??????");
			plugins.add(arp);
		}
		
		if(log.isInfoEnabled()) log.info("ServicePlugin Service?????????????????????");
		plugins.add(new ServicePlugin());
		
		if(log.isInfoEnabled()) log.info("I18NPlugin ????????????????????????");
		plugins.add(new I18NPlugin());
		
		if(ToolCache.getCacheType().equals(ConstantCache.cache_type_ehcache)){
			if(log.isInfoEnabled()) log.info("EhCachePlugin EhCache??????");
			plugins.add(new EhCachePlugin());
			
		}else if(ToolCache.getCacheType().equals(ConstantCache.cache_type_redis)){
			if(log.isInfoEnabled()) log.info("RedisPlugin Redis??????");
			Map<String, Redis> redisMap = ToolRedis.getRedisMap();
			Redis redis = null;
			for (String name : redisMap.keySet()) {
				redis = redisMap.get(name);
				RedisPlugin rp = new RedisPlugin(redis.getName(), redis.getIp(), redis.getPort(), redis.getTimeout(), redis.getPassword());
				plugins.add(rp);
			}
		}
		
		if(log.isInfoEnabled()) log.info("configPlugin SqlXmlPlugin ??????????????? xml sql");
		plugins.add(new SqlXmlPlugin());
		
		if(log.isInfoEnabled()) log.info("configPlugin ParamInitPlugin ????????????");
		plugins.add(new ParamInitPlugin());
		
		if(log.isInfoEnabled()) log.info("configPlugin FileRenamePlugin ????????????????????????????????????");
		plugins.add(new FileRenamePlugin());

		if(log.isInfoEnabled()) log.info("configPlugin QuartzPlugin ??????Quartz??????");
		plugins.add(new QuartzPlugin());
	}

	/**
	 * ?????????????????????
	 */
	public void configInterceptor(Interceptors interceptors) {
		//if(log.isInfoEnabled()) log.info("configInterceptor ????????????session");
		//interceptors.add(new SessionInViewInterceptor(true));
		
		if(log.isInfoEnabled()) log.info("configInterceptor XSS??????");
		interceptors.add(new XSSInterceptor());
		
		if(log.isInfoEnabled()) log.info("configInterceptor ?????????????????????");
		interceptors.add(new AuthInterceptor());
		
		if(log.isInfoEnabled()) log.info("configInterceptor ?????????????????????");
		interceptors.add(new ParamPkgInterceptor());
		
		if(log.isInfoEnabled()) log.info("configInterceptor ????????????????????????");
		interceptors.add(new TxByMethods("save", "update", "delete"));
		interceptors.add(new TxByMethodRegex("(.*save.*|.*update.*|.*delete.*)")); // 2.1???????????????????????????????????????????????????????????????????????????
		interceptors.add(new TxByActionKeys("/test/user/save", "/test/user/delete"));
		interceptors.add(new TxByActionKeyRegex("/test/user.*"));

		if(log.isInfoEnabled()) log.info("configInterceptor i18n?????????");
		interceptors.add(new I18nInterceptor());
	}
	
	/**
	 * ???????????????
	 */
	public void configHandler(Handlers handlers) {
//		if(log.isInfoEnabled()) log.info("configHandler ????????????????????????http 301 ?????????????????????????????????????????????????????????????????????(??? GET ??? HEAD ???????????????)??????????????????????????????????????????");
//		handlers.add(new ServerNameRedirect301Handler("http://127.0.0.1", "http://localhost"));
		
		if(log.isInfoEnabled()) log.info("configHandler ????????????????????????druid??????????????????");
		handlers.add(new DruidStatViewHandler("/platform/druid"));
		
		//if(log.isInfoEnabled()) log.info("configHandler ?????????????????????????????????????????????");
		//handlers.add(new ContextPathHandler("cxt")); // ??????UIB???GlobalHandler????????????????????????????????????????????????

		//if(log.isInfoEnabled()) log.info("configHandler ???????????????????????????????????????????????????????????????????????????jfinal????????????????????????????????????.html");
		//handlers.add(new FakeStaticHandler(".html")); // ????????????????????????.html??????????????????????????????????????????????????????????????????????????????????????????

		//if(log.isInfoEnabled()) log.info("configHandler ??????????????????????????????????????????URL?????????");
		handlers.add(new UrlSkipHandler("/ca/.*|/se/.*|.*.jsp|.*.htm|.*.html|.*.js|.*.css|.*.json|.*.png|.*.gif|.*.jpg|.*.jpeg|.*.bmp|.*.ico|.*.exe|.*.txt|.*.zip|.*.rar|.*.7z", false));
		
		if(log.isInfoEnabled()) log.info("configHandler ???????????????????????????????????????request????????????");
		handlers.add(new GlobalHandler());
	}
	
	/**
	 * ???????????????????????????
	 */
	public void afterJFinalStart() {
		if(log.isInfoEnabled()) log.info("afterJFinalStart ??????????????????????????????");
		ThreadSysLog.startSaveDBThread();

		if(log.isInfoEnabled()) log.info("afterJFinalStart ???????????????????????????");
		QuartzPlugin.addJob("ResourcesJob", "0 0/2 * * * ?", ResourcesJob.class);

		if(log.isInfoEnabled()) log.info("afterJFinalStart ???????????????????????????");
		QuartzPlugin.addJob("DataClearJob", "0 0 2 * * ?", DataClearJob.class);

		if(log.isInfoEnabled()) log.info("afterJFinalStart ??????????????????????????????");
		QuartzPlugin.addJob("DbBackupJob",  "0 0 2 * * ?", DbBackupJob.class);
	}
	
	/**
	 * ?????????????????????
	 */
	public void beforeJFinalStop() {
		if(log.isInfoEnabled()) log.info("beforeJFinalStop ????????????????????????");
		ThreadSysLog.setThreadRun(false);

		if(log.isInfoEnabled()) log.info("beforeJFinalStop ???????????????????????????");
		QuartzPlugin.deleteJob("ResourcesJob");
		
		if(log.isInfoEnabled()) log.info("beforeJFinalStop ???????????????????????????");
		QuartzPlugin.deleteJob("DataClearJob");
		
		if(log.isInfoEnabled()) log.info("beforeJFinalStop ??????????????????????????????");
		QuartzPlugin.deleteJob("DbBackupJob");
	}

}
