package ie.strix.rtk_survey;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Create an {@link EntityManager} for each thread. Call up an {@link EntityManager}
 * at any time using <code>HiberateUtil.getEntityManager()</code>.
 * 
 * @author Joe Desbonnet
 *
 */
public class HibernateUtil {

	private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

	/**
	 * Map of {@link EntityManagerFactory} objects: one per datasource. These are expensive
	 * to create, so only create one per datasource.
	 */
	private static final Map<String,EntityManagerFactory> emfMap = new HashMap<>();
	
	private static final ThreadLocal<HashMap<String,EntityManager>> emMap = new ThreadLocal<HashMap<String,EntityManager>>() {
		@Override
		protected HashMap<String, EntityManager> initialValue() {
			return new HashMap<>();
		}
	};
	
	
	
	//public static final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();
	
	//private static EntityManagerFactory emf = createEntityManagerFactory();
		
	private static EntityManagerFactory createEntityManagerFactory() {
			return createEntityManagerFactory("tirtbx");
	}
	
	private static EntityManagerFactory createEntityManagerFactory(String datasource) {
		try {
			return Persistence.createEntityManagerFactory(datasource);
		} catch (Throwable ex) {
			System.err.println("ERROR: cannot create EntityManagerFactory for " + datasource + ": " + ex);
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	


	
	public static EntityManager getEntityManager() {
		return getEntityManager("tirtbx");
	}
	
	public static EntityManager getEntityManager(String datasource) {

		EntityManager em = emMap.get().get(datasource);
		
		// Open a new Session, if this Thread has none yet
		if (em == null || !em.isOpen()) {
			EntityManagerFactory emf = emfMap.get(datasource);
			if (emf == null) {
				emf = createEntityManagerFactory(datasource);
				log.info("creating EntityManagerFactory for datasource " + datasource + " " + emf.hashCode() 
				+ " thread=" + Thread.currentThread());
				emfMap.put(datasource, emf);
			}
			em = emf.createEntityManager();
			log.info("creating EntityManager for datasource " + datasource + " " + em.hashCode() 
				+ " thread=" + Thread.currentThread());
			emMap.get().put(datasource, em);
		}

		return em;
	}
	
	
	public static String getDataSource() {
		try {
			Session s = (Session) getEntityManager().getDelegate();
			org.hibernate.SessionFactory sessionFactory = s.getSessionFactory();

			ConnectionProvider cp = ((SessionFactoryImpl) sessionFactory).getSessionFactoryOptions()
					.getServiceRegistry().getService(ConnectionProvider.class);

			Connection connection = cp.getConnection();
			DatabaseMetaData dbmetadata = connection.getMetaData();
			String dtsource = dbmetadata.getUserName();
			return dtsource;
		} catch (Exception e) {
			return "unknown_datasource";
		}
	}
	
	public static void closeEntityManagers () {
		
		for (EntityManager em : emMap.get().values()) {

			if (em.getTransaction().isActive()) {
				if (em.getTransaction().getRollbackOnly()) {
					log.warn(
							"transaction in " + em.hashCode() + " in rollback only state: rollingback the transaction");
					em.getTransaction().rollback();
				} else {
					log.debug("commiting the transaction in " + em.hashCode());
					em.getTransaction().commit();
				}
			}

			if (em.isOpen()) {
				log.info("closing EntityManager " + em.hashCode() + " on thread " + Thread.currentThread());
				em.close();
			}
		}
		
	}
}
