package com.mcabrera.logitrackapi.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected abstract Class<T> entity();

    @Transactional
    public Optional<T> save(T entity) {
        try {
            if (entityManager.contains(entity)) {
                entity = entityManager.merge(entity);
            } else {
                entityManager.persist(entity);
            }
            entityManager.flush();
            return Optional.of(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<T> findById(ID id) {
        try {
            T entity = entityManager.find(entity(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<T> getAll() {
        return entityManager.createQuery("SELECT e FROM " + entity().getSimpleName() + " e", entity())
                .getResultList();
    }

    @Transactional
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Transactional
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}