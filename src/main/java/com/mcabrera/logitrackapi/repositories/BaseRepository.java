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
            System.out.println("=== BaseRepository.save() ===");
            System.out.println("Entity type: " + entity.getClass().getSimpleName());
            System.out.println("EntityManager: " + (entityManager != null ? "OK" : "NULL"));

            if (entityManager == null) {
                System.err.println("ERROR: EntityManager es NULL!");
                return Optional.empty();
            }

            System.out.println("EntityManager contains entity: " + entityManager.contains(entity));

            if (entityManager.contains(entity)) {
                System.out.println("Haciendo merge...");
                entity = entityManager.merge(entity);
            } else {
                System.out.println("Haciendo persist...");
                entityManager.persist(entity);
            }

            System.out.println("Haciendo flush...");
            entityManager.flush();

            System.out.println("Entidad guardada exitosamente");
            return Optional.of(entity);
        } catch (Exception e) {
            System.err.println("ERROR en BaseRepository.save(): " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
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
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Transactional
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}