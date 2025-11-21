package com.mcabrera.logitrackapi.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.AvailableSettings;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class JpaProducer {

    private EntityManagerFactory emf;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        if (emf != null) {
            return emf;
        }

        System.out.println("=== Iniciando configuración de JPA ===");

        Map<String, Object> props = new HashMap<>();

        String dbUrl = get("DB_URL");
        String dbUser = get("DB_USER");
        String dbDriver = get("DB_DRIVER");

        System.out.println("DB_URL: " + dbUrl);
        System.out.println("DB_USER: " + dbUser);
        System.out.println("DB_DRIVER: " + dbDriver);

        props.put("jakarta.persistence.jdbc.driver", dbDriver);
        props.put("jakarta.persistence.jdbc.url", dbUrl);
        props.put("jakarta.persistence.jdbc.user", dbUser);
        props.put("jakarta.persistence.jdbc.password", get("DB_PASSWORD"));

        props.put("hibernate.dialect", get("HIBERNATE_DIALECT"));
        props.put("hibernate.hbm2ddl.auto", get("HIBERNATE_DDL"));
        props.put("hibernate.show_sql", get("HIBERNATE_SHOW_SQL"));
        props.put("hibernate.format_sql", get("HIBERNATE_FORMAT_SQL"));

        // Configuración para respetar nombres de tablas exactos
        props.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        props.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

        // Importante: deshabilitar la conversión automática de nombres
        props.put("hibernate.globally_quoted_identifiers", "true");
        props.put("hibernate.globally_quoted_identifiers_skip_column_definitions", "true");

        // Configuración de transacciones
        props.put("hibernate.connection.autocommit", "false");
        props.put("hibernate.enable_lazy_load_no_trans", "false");

        // Escanear entidades
        Set<Class<?>> entities = new Reflections("com.mcabrera.logitrackapi.models")
                .getTypesAnnotatedWith(Entity.class);

        System.out.println("Entidades encontradas: " + entities.size());
        for (Class<?> entity : entities) {
            System.out.println("  - " + entity.getSimpleName());
        }

        props.put(AvailableSettings.LOADED_CLASSES, new ArrayList<>(entities));

        try {
            emf = Persistence.createEntityManagerFactory("logitrackapiPU", props);
            System.out.println("=== EntityManagerFactory creado exitosamente ===");

            // Probar la conexión
            EntityManager testEm = emf.createEntityManager();
            testEm.close();
            System.out.println("=== Conexión a BD verificada ===");

            return emf;
        } catch (Exception e) {
            System.err.println("ERROR al crear EntityManagerFactory:");
            e.printStackTrace();
            throw e;
        }
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        System.out.println("EntityManager creado para request: " + em.hashCode());
        return em;
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            System.out.println("Cerrando EntityManager: " + em.hashCode());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private String get(String name) {
        String p = System.getProperty(name);
        if (p != null) return p;
        String env = System.getenv(name);
        if (env != null) return env;

        System.err.println("WARNING: Propiedad " + name + " no encontrada");
        return null;
    }
}