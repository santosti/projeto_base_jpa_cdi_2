package com.stefanini.model;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.h2.tools.RunScript;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class PerfilTest {

	private Validator validator;
	private SessionFactory factoryJpa;
	private Boolean h2Carregador = Boolean.FALSE;
	
	private String nome = "joao";

	@Before
	public void setUp() {
		runScrip();
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		this.validator = validatorFactory.getValidator();
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		factoryJpa = new MetadataSources(registry).buildMetadata().buildSessionFactory();

	}

	public void runScrip() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
			if (conn != null) {
				final Statement st = conn.createStatement();
				final ResultSet rs = st.executeQuery("show tables");
				while (rs.next()) {
					h2Carregador = true;
				}
				if (!h2Carregador) {
					ClassLoader classLoader = getClass().getClassLoader();
					File file = new File(classLoader.getResource("db.sql").getFile());
					System.out.println("Carregado o SCRIPT");
					RunScript.execute(conn, new FileReader(file));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	@Test()
    public void findPerfilCriteria() {
		System.out.println("CHEGUEI AQUI");
        try (Session session = factoryJpa.openSession()) {
            Perfil perfil = findPerfilCriteria(session, nome);
            System.out.println("Perfil: " +perfil.getNome());
            System.out.println("Novo: " + perfil);
        }
    }

	private Perfil findPerfilCriteria(Session session, String nome) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Perfil> q = cb.createQuery(Perfil.class);
		Root<Perfil> entityRoot = q.from(Perfil.class);
		q.select(entityRoot);
		ParameterExpression<String> p = cb.parameter(String.class);
		q.where(cb.equal(entityRoot.get("nome"), nome));
		return session.createQuery(q).getSingleResult();
	}
}

