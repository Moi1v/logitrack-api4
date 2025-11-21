package com.mcabrera.logitrackapi.repositories;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    @Inject
    protected EntityManager entityManager;

    protected abstract Class<T> entity();

    @Transactional
    public Optional<T> save(T entity) {
        try {
            EntityManager em = entityManager;

            // Verificar si la transacción está activa, si no, iniciarla
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            T result;
            if (em.contains(entity)) {
                // La entidad ya está en el contexto de persistencia
                result = entity;
            } else {
                // Hacer merge para entidades nuevas o detached
                result = em.merge(entity);
            }

            em.flush();

            // Hacer commit si iniciamos la transacción aquí
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }

            return Optional.of(result);
        } catch (Exception e) {
            // Rollback en caso de error
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("ERROR en BaseRepository.save(): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<T> findById(ID id) {
        try {
            T entity = entityManager.find(entity(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            System.err.println("ERROR en findById: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<T> getAll() {
        try {
            return entityManager.createQuery("SELECT e FROM " + entity().getSimpleName() + " e", entity())
                    .getResultList();
        } catch (Exception e) {
            System.err.println("ERROR en getAll: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void delete(T entity) {
        try {
            EntityManager em = entityManager;

            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            T managedEntity = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managedEntity);
            em.flush();

            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("ERROR en delete: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}