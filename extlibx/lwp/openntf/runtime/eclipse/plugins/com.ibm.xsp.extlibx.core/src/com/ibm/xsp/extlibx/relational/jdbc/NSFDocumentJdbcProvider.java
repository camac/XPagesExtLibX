package com.ibm.xsp.extlibx.relational.jdbc;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.ibm.commons.log.LogMgr;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.resources.ResourceFactoriesException;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.extlib.log.ExtlibCoreLogger;
import com.ibm.xsp.extlib.relational.jdbc.datasource.xpages.JdbcPool;
import com.ibm.xsp.extlib.relational.jdbc.datasource.xpages.JdbcPoolDataSource;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;
import com.ibm.xsp.extlib.relational.resources.provider.IJdbcResourceFactoryProvider;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

public class NSFDocumentJdbcProvider implements IJdbcResourceFactoryProvider {

	private static final LogMgr logger = ExtlibCoreLogger.RELATIONAL;

	private static final String FIELD_URL = "url";
	private static final String FIELD_DRIVER = "driver";
	private static final String FIELD_USERNAME = "user";
	private static final String FIELD_PASSWORD = "password";

	private static final String VIEWNAME = "JdbcConnections";

	private String filePath = null;

	public NSFDocumentJdbcProvider() {
		logger.traceDebug("Created a NSF DocumentJdbcProvider");
	}

	public NSFDocumentJdbcProvider(String filePath) {
		this.filePath = filePath;
	}

	public Session getSession() throws NotesException {

		Session s = NotesContext.getCurrent().getSessionAsSigner(true);

		if (s != null) {
			logger.traceDebug("Using SessionAsSigner for NSF Document JDBC Provider");
		}
		
		if (s == null) {

			//s = DominoSession.getSession();
			s = NotesContext.getCurrent().getCurrentSession();

			if (s != null) {
				return s;
			}

		}

		return s;

	}

	public Database getDatabase(Session s) throws NotesException {

		if (StringUtil.isEmpty(filePath)) {

			logger.traceDebug("File Path is Empty, try for current database");

			Database db = s.getCurrentDatabase();

			if (db == null) {
				String dbpath = NotesContext.getCurrent().getModule().getDatabasePath();
				return s.getDatabase(null, dbpath);
			}

			return db;

		} else {

			logger.traceDebug("Get Database with FilePath '{0}'", filePath);
			return s.getDatabase(null, filePath);
		}
	}

	public String getServerName() throws NotesException {
		return getSession().getServerName();
	}
	
	@Override
	public String[] getConnectionNames() throws ResourceFactoriesException {

		logger.traceDebug("In GetConnectionNames");

		Set<String> names = new HashSet<String>();

		Session s = null;

		try {
			s = getSession();

			if (s == null) {
				throw new NullPointerException("Could not get a Session");
			}

			Database db = getDatabase(s);

			if (db == null) {
				throw new NullPointerException("Database Not Found");
			}

			View view = db.getView(VIEWNAME);

			if (view != null) {

				view.setAutoUpdate(false);

				ViewEntryCollection vec = view.getAllEntries();

				if (vec.getCount() < 1) {
					logger.traceDebug("No JDBC Connections found in view '{0}'", VIEWNAME);
				}

				ViewEntry ve = vec.getFirstEntry();
				ViewEntry nextve = null;

				while (ve != null) {

					if (ve.isDocument()) {
						String name = (String) ve.getColumnValues().get(1);
						if (StringUtil.isNotEmpty(name)) {

							logger.traceDebug("Adding name to connections: '{0}'", name);
							names.add(name);

						}
					}

					nextve = vec.getNextEntry(ve);
					ve.recycle();
					ve = nextve;

				}
			} else {
				logger.traceDebug("Connection Names not loaded due to missing view '{0}'", VIEWNAME);
			}

		} catch (Exception e) {
			if (StringUtil.isEmpty(filePath)) {
				logger.error("Error loading Connection Names from Database");
			} else {
				logger.error("Error loading Connection Names from Database '{0}'", filePath);
			}
		}

		String[] namearr = names.toArray(new String[0]);
		return namearr;
	}

	@Override
	public IJdbcResourceFactory loadResourceFactory(String name) throws ResourceFactoriesException {
		return loadConnection(name);
	}
	
	public IJdbcResourceFactory loadConnection(String name) {

		logger.traceDebug("Load Connection '{0}'", name);

		try {

			Session s = getSession();
			Database db = getDatabase(s);

			if (db == null) {
				logger.traceDebug("No Database Found");
				return null;
			}

			View view = db.getView(VIEWNAME);

			if (view == null) {
				logger.traceDebug("No View found");
				return null;
			}

			String server = getServerName();

			Vector<String> v = new Vector<String>(2);
			v.add(server);
			v.add(name);

			Document doc = view.getDocumentByKey(v, true);

			if (doc == null) {
				logger.traceDebug("No JDBC Document Found for '{0}', '{0}'", server, name);
				v.set(0, "Default");
				doc = view.getDocumentByKey(v, true);
			}

			if (doc == null) {
				logger.traceDebug("No JDBC Document Found for 'Default', '{0}'", name);
				return null;
			}

			String url = doc.getItemValueString(FIELD_URL);
			String driver = doc.getItemValueString(FIELD_DRIVER);
			String user = doc.getItemValueString(FIELD_USERNAME);
			String password = doc.getItemValueString(FIELD_PASSWORD);

			int minPoolSize = JdbcPool.MIN_POOLSIZE; // $NON-NLS-1$
			int maxPoolSize = JdbcPool.MAX_POOLSIZE; // $NON-NLS-1$
			int maxConnectionSize = JdbcPool.MAX_CONNECTION_NUMBER; // $NON-NLS-1$
			long useTimeout = JdbcPool.USE_TIMEOUT; // $NON-NLS-1$
			long idleTimeout = JdbcPool.IDLE_TIMEOUT; // $NON-NLS-1$
			long maxLiveTime = JdbcPool.MAXLIVETIME; // $NON-NLS-1$
			long acquireTimeout = JdbcPool.ACQUIRE_TIMEOUT; // $NON-NLS-1$

			return new JdbcPoolDataSource(driver, url, user, password, minPoolSize, maxPoolSize, maxConnectionSize,
					useTimeout, idleTimeout, maxLiveTime, acquireTimeout);

		} catch (Exception e) {
			logger.error("Error Loading Connection Information from Document '{0}'", e);
		}
		return null;

	}

}