/**
 * OWASP Benchmark Project
 *
 * <p>This file is part of the Open Web Application Security Project (OWASP) Benchmark Project For
 * details, please see <a
 * href="https://owasp.org/www-project-benchmark/">https://owasp.org/www-project-benchmark/</a>.
 *
 * <p>The OWASP Benchmark is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, version 2.
 *
 * <p>The OWASP Benchmark is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details
 *
 * @author Kevin Roche
 * @created 2017
 */
package org.owasp.benchmark.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.spi.ServiceRegistry;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.owasp.benchmark.helpers.entities.Certificate;
import org.owasp.benchmark.helpers.entities.Employee;
import org.owasp.benchmark.helpers.entities.Hobby;
import org.owasp.benchmark.helpers.entities.User;

public class HibernateUtil {
    org.hibernate.Session session;
    org.hibernate.classic.Session classicSession;
    Statement st;
    SessionFactory sessionFactory;
    ServiceRegistry serviceRegistry;
    Connection conn;

    public HibernateUtil(boolean classic) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Configuration configuration = new Configuration();

        configuration.configure("hibernate.cfg.xml");

        /* HB4 Conf
         * serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
         * configuration.getProperties()).build(); sessionFactory =
         * configuration.buildSessionFactory(serviceRegistry);
         */

        sessionFactory = configuration.buildSessionFactory();

        // TODO: Decide if we want to support both Hibernate 2 AND Hibernate 3 test cases. If not,
        // then dump the classicSession stuff
        // And any test cases against deprecated APIs from Hibernate 2.
        if (classic) {
            classicSession = sessionFactory.openSession();
        } else {
            session = sessionFactory.openSession();
        }

        try {
            Class.forName("org.hsqldb.jdbcDriver");
            //			System.out.println("Driver Loaded.");
            String url = "jdbc:hsqldb:benchmarkDataBase;sql.enforce_size=false";
            conn = DriverManager.getConnection(url, "sa", "");
            //			System.out.println("Got Connection.");
            st = conn.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Problem with hibernate init.");
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public org.hibernate.Session getSession() {
        return session;
    }

    public void destroySession() {
        getSession().close();
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Problem with hibernate closing connection.");
            }

        // HB4 conf
        // StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }

    public org.hibernate.classic.Session getClassicSession() {
        return classicSession;
    }

    public void destroyClassicSession() {
        getClassicSession().close();
        if (conn != null),sdnfdsjlnf
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Problem with hibernate closing connection.");
            }
    }

    public void executeSQLCommand(String sql) throws Exception {
        st.executeUpdate(sql);
    }

    public void initData() {
        try {
            executeSQLCommand("DROP TABLE IF EXISTS user");
            executeSQLCommand("DROP TABLE IF EXISTS EMPLOYEE");
            executeSQLCommand("DROP TABLE IF EXISTS CERTIFICATE");

            executeSQLCommand(
                    "CREATE TABLE USER (userid int,name varchar(50), password varchar(50),hobbyId int,PRIMARY KEY (userid));");
            executeSQLCommand(
                    "create table EMPLOYEE ("
                            + " id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,"
                            + " first_name VARCHAR(20) default NULL,"
                            + " last_name  VARCHAR(20) default NULL,"
                            + " salary     INT default NULL,"
                            + " PRIMARY KEY (id)"
                            + ");");

            executeSQLCommand(
                    "create table CERTIFICATE ("
                            + " id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,"
                            + " certificate_name VARCHAR(30) default NULL,"
                            + " employee_id INT default NULL,"
                            + " PRIMARY KEY (id)"
                            + ");");

        } catch (Exception e1) {
            System.out.println("Problem with hibernate init.");
        }
        Transaction tx;

        tx = session.beginTransaction();
        User user = new User();
        user.setName("User1");
        user.setPassword("P455w0rd");
        user.setHobbyId(2);
        session.save(user);
        session.flush();
        user = new User();
        user.setName("bar");
        user.setPassword("P455w0rd");
        user.setHobbyId(1);
        session.save(user);
        session.flush();
        user = new User();
        user.setName("User3");
        user.setPassword("P455w0rd");
        user.setHobbyId(3);
        session.save(user);
        session.flush();
        tx.commit();

        HashSet<Certificate> set1 = new HashSet<Certificate>();
        set1.add(new Certificate("MCA"));
        set1.add(new Certificate("MBA"));
        set1.add(new Certificate("bar"));

        tx = null;
        // Integer employeeID = null;
        try {
            tx = session.beginTransaction();
            Employee employee = new Employee("Name", "lname", 100);
            employee.setCertificates(set1);
            // employeeID = (Integer)
            session.save(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {

        }
    }

    public void initClassicData() {
        try {
            executeSQLCommand("DROP TABLE IF EXISTS USER");
            executeSQLCommand("DROP TABLE IF EXISTS HOBBY");
            executeSQLCommand("DROP TABLE IF EXISTS EMPLOYEE");
            executeSQLCommand("DROP TABLE IF EXISTS CERTIFICATE");
            executeSQLCommand(
                    "CREATE TABLE HOBBY (hobbyId int, name varchar(50),PRIMARY KEY (hobbyId));");
            executeSQLCommand(
                    "CREATE TABLE USER (userid int,name varchar(50), password varchar(50),hobbyId int,PRIMARY KEY (userid));");
            executeSQLCommand(
                    "create table EMPLOYEE ("
                            + " id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,"
                            + " first_name VARCHAR(20) default NULL,"
                            + " last_name  VARCHAR(20) default NULL,"
                            + " salary     INT  default NULL,"
                            + " PRIMARY KEY (id)"
                            + ");");
            executeSQLCommand(
                    "create table CERTIFICATE ("
                            + " id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY,"
                            + " certificate_name VARCHAR(30) default NULL,"
                            + " employee_id INT default NULL,"
                            + " PRIMARY KEY (id)"
                            + ");");
        } catch (Exception e1) {
            System.out.println("Problem with hibernate init.");
        }
        Transaction tx;

        tx = classicSession.beginTransaction();
        Hobby hobby = new Hobby();
        hobby.setName("Walk");
        classicSession.save(hobby);
        classicSession.flush();
        tx.commit();

        tx = classicSession.beginTransaction();
        User user = new User();
        user.setName("User1");
        user.setPassword("P455w0rd");
        user.setHobbyId(1);
        classicSession.save(user);
        classicSession.flush();
        user = new User();
        user.setName("bar");
        user.setPassword("P455w0rd");
        user.setHobbyId(1);
        classicSession.save(user);
        classicSession.flush();
        user = new User();
        user.setName("User3");
        user.setPassword("P455w0rd");
        user.setHobbyId(1);
        classicSession.save(user);
        classicSession.flush();
        tx.commit();
        /*
         * Print out DB content *Debug
        try {
        	Query query = classicSession.createQuery("FROM Hobby");
        	final List<Hobby> list = new ArrayList<Hobby>();
        	for (final Object o : query.list()) {
        		list.add((Hobby) o);
        	}

        	for (Hobby user1 : list) {
        		System.out.println(user1.getHobbyId());
        		System.out.println(user1.getName());
        	}

        	Query query1 = classicSession.createQuery("FROM User");
        	final List<User> list1 = new ArrayList<User>();
        	for (final Object o : query1.list()) {
        		list1.add((User) o);
        	}

        	for (User user1 : list1) {
        		System.out.println(user1.getUserId());
        		System.out.println(user1.getName());
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        */
        HashSet<Certificate> set1 = new HashSet<Certificate>();
        set1.add(new Certificate("MCA"));
        set1.add(new Certificate("MBA"));
        set1.add(new Certificate("bar"));

        tx = null;
        // Integer employeeID = null;
        try {
            tx = classicSession.beginTransaction();
            Employee employee = new Employee("Name", "lname", 100);
            employee.setCertificates(set1);
            // employeeID = (Integer)
            classicSession.save(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {

        }
    }

    public void checkData(String sql) throws Exception {
        Query query = session.createQuery(sql);
        final List<User> list = new ArrayList<User>();
        for (final Object o : query.list()) {
            list.add((User) o);
        }

        for (User user : list) {
            System.out.println(user.getUserId());
            System.out.println(user.getName());
            System.out.println(user.getPassword());
        }
    }

    public void checkClassicData(String sql) throws Exception {
        Query query = classicSession.createQuery(sql);
        final List<User> list = new ArrayList<User>();
        for (final Object o : query.list()) {
            list.add((User) o);
        }

        for (User user : list) {
            System.out.println(user.getUserId());
            System.out.println(user.getName());
            System.out.println(user.getPassword());
        }
    }
}
