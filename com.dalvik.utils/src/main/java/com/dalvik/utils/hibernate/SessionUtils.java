package com.dalvik.utils.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionUtils {

	private static SessionUtils instance = new SessionUtils();
	private SessionFactory sessionFactory;

	public static SessionUtils getInstance() {
		return instance;
	}

	private SessionUtils() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		sessionFactory = configuration.buildSessionFactory();
	}

	public static Session getSession() {
		Session session = getInstance().sessionFactory.openSession();
		return session;
	}

}
