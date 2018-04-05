package com.github.diorgejorge.utils;

import com.github.diorgejorge.bean.AbstractEntity;
import com.github.diorgejorge.exception.DAOException;
import com.github.diorgejorge.pojo.NamedParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LazyInitializationException;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Diorge Jorge on 21/03/2018.
 */
public class DAO implements Serializable,IDAO {

    private static final long serialVersionUID = 1L;

    private final transient Logger logger = Logger.getLogger(DAO.class);

    private final static String STRING_CLASS = "class java.lang.String";
    private final static String DATE_CLASS = "class java.util.Date";
    private final static String OBJECT_CLASS = "class java.lang.Object";
    private final static String ENUM_CLASS = "class java.lang.Enum";
    private final static String NUMBER_CLASS = "java.lang.Number";
    private final static String INICIO = "Inicio";
    private final static String FIM = "Fim";
    private EntityManager entityManager = Persistence.createEntityManagerFactory("crawler").createEntityManager();


    private EntityManager getEm() {
        return this.entityManager;
    }

    @SuppressWarnings("rawtypes")
    public void delete(AbstractEntity entidade) throws DAOException {
        EntityTransaction tx = getEm().getTransaction();
        try {
            tx.begin();
            logger.debug("Iniciando Remove da classe [" + entidade.getClass().getName() + "] " + entidade.toString());
            entidade = getEm().merge(entidade);
            getEm().remove(entidade);
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
            tx.rollback();
            throw new DAOException(e.getMessage());
        }
    }

    public void delete(List<? extends AbstractEntity> entidades) throws DAOException {
        if (entidades.size() > 0) {
            logger.debug("Deletando " + entidades.size() + " entidade(s) do tipo " + entidades.get(0).getClass().getName());
            for (AbstractEntity e : entidades) {
                delete(e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public AbstractEntity findByClass(Class<? extends AbstractEntity> clazz, Object id) throws DAOException {
        try {
            logger.debug("Executando findByClass para classe " + clazz.getName());
            return getEm().find(clazz, id);
        } catch (Exception e) {
            logger.error(e);
            throw new DAOException(e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    public AbstractEntity find(AbstractEntity entidade) throws DAOException {
        try {
            if (entidade.getId() != null) {
                logger.debug("Executando find para classe " + entidade.toString());
                return getEm().find(entidade.getClass(), entidade.getId());
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e);
            throw new DAOException(e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    public void refresh(AbstractEntity entidade) throws DAOException {
        try {
            getEm().refresh(entidade);
        } catch (Exception e) {
            logger.error(e);
            throw new DAOException(e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    public AbstractEntity save(AbstractEntity entidade) throws DAOException {
        EntityTransaction tx = getEm().getTransaction();
        try {
            tx.begin();
            AbstractEntity baseEntity = find(entidade);
            logger.debug("Iniciando Persist da classe [" + entidade.getClass().getName() + "] " + entidade.toString());
            if (baseEntity == null) {
                getEm().persist(entidade);
                tx.commit();
                return entidade;
            } else {
                AbstractEntity entity = getEm().merge(entidade);
                tx.commit();
                return entity;
            }
        } catch (Exception e) {
            logger.error(e);
            tx.rollback();
            throw new DAOException(e.getMessage());
        }
    }

    public void save(HashMap<String, Object> entidades) throws DAOException {
        if (entidades.size() > 0) {
            logger.debug("Salvando " + entidades.size() + " entidade(s) do tipo " + entidades.get(0).getClass().getName());
            for (Object e : entidades.values()) {
                save((AbstractEntity)e);
            }
        }
    }

    @SuppressWarnings({"rawtypes"})
    public List findList(String hql, Map<String, Object> namedParams) throws DAOException {
        return findList(hql, namedParams, null, null, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List findList(String hql, Map<String, Object> namedParams, Integer first,
                         Integer maxResult, boolean buscarTodos) throws DAOException {
        try {
            logger.debug("Executando findList. HQL = " + hql);
            Query query = getEm().createQuery(hql);
            configureParams(namedParams, query);

            if (first != null) {
                query.setFirstResult(first);
            }

            if (maxResult != null) {
                query.setMaxResults(maxResult);
            } else if (!buscarTodos) {
                query.setMaxResults(100);
            }

            return query.getResultList();
        } catch (Exception e) {
            logger.error(e);
            throw new DAOException(e.getMessage());
        }
    }

    public List findList(String hql, Map<String, Object> namedParams, Integer first,
                         Integer maxResult) throws DAOException {
        return findList(hql, namedParams, first, maxResult, false);
    }

    @SuppressWarnings("rawtypes")
    public Object findSingleResult(String hql, Map<String, Object> namedParams) throws DAOException {
        try {
            Query query = getEm().createQuery(hql);
            configureParams(namedParams, query);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void configureParams(Map<String, Object> namedParams, Query query) {
        if (namedParams != null) {
            Map.Entry<String, Object> mapEntry;
            for (Iterator it = namedParams.entrySet().iterator(); it.hasNext(); query
                    .setParameter(mapEntry.getKey(), mapEntry.getValue())) {
                mapEntry = (Map.Entry<String, Object>) it.next();
                logger.debug("Param: " + mapEntry.getKey() + ", Value: " + mapEntry.getValue());
            }

        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private NamedParams cleanParameters(NamedParams namedParamsRange) throws DAOException {
        NamedParams namedParamsRange_ = new NamedParams();

        for (String key : namedParamsRange.keySet()) {
            if (namedParamsRange.get(key) != null) {
                String key_ = key.contains(INICIO) ? key.substring(0, key.indexOf(INICIO)) : key.substring(0, key.indexOf(FIM));
                if (namedParamsRange.containsKey(key_ + FIM) && namedParamsRange.get(key_ + FIM) != null) {
                    namedParamsRange_.put(key, namedParamsRange.get(key));
                } else {
                    throw new DAOException("Os parametros de Range devem possuir inicio e Fim");
                }
                if (namedParamsRange.containsKey(key_ + INICIO) && namedParamsRange.containsKey(key_ + FIM)) {
                    if (namedParamsRange.get(key_ + INICIO) instanceof Date) {
                        if (((Date) namedParamsRange.get(key_ + FIM)).before((Date) namedParamsRange.get(key_ + INICIO))) {
                            throw new DAOException("A data fim não pode ser inferior a data de inicio");
                        }
                    } else {
                        BigDecimal fim = new BigDecimal(namedParamsRange.get(key_ + FIM).toString());
                        BigDecimal inicio = new BigDecimal(namedParamsRange.get(key_ + INICIO).toString());
                        if (fim.compareTo(inicio) > 0) {
                            throw new DAOException("A valor fim não pode ser inferior a valor de inicio");
                        }
                    }

                }
            }
        }
        return namedParamsRange_.isEmpty() ? null : namedParamsRange_;
    }

    /**
     * método analogo ao buscar por exemplo do hibernate
     *
     * @param example
     * @param namedParamsRange LinkedHashMap com os parametros inicio e fim
     *                         i.e:
     *                         atributo dataNascimento, dataNascimentoInicio,dataNascimentoFim
     *                         <pre><code>
     *                              public Date dataNascimento;
     *                              public NamedParams namedParams = new NamedParams("dataNascimentoInicio",dataNascimento+xDias,"dataNascimentoFim",dataNascimento+yDias)
     *                          </code></pre>
     * @param example
     * @param maxResults
     * @param namedParamsRange
     * @param columnOrderBy
     * @return
     * @throws DAOException
     */
    public <T extends AbstractEntity> List<T> findListByModel(AbstractEntity example, Integer maxResults, NamedParams namedParamsRange, String columnOrderBy) throws DAOException {
        try {
            namedParamsRange = namedParamsRange != null ? cleanParameters(namedParamsRange) : namedParamsRange;
            Class<T> tClass = (Class<T>) example.getClass();
            CriteriaBuilder criteriaBuilder = getEm().getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tClass);
            Root<T> root = criteriaQuery.from(tClass);
            Predicate predicate = criteriaBuilder.conjunction();
            Metamodel metamodel = getEm().getMetamodel();
            EntityType<T> entityType = metamodel.entity(tClass);
            Set<Attribute<? super T, ?>> attributes = entityType.getAttributes();
            predicate = predicateBuilderFindByModel(example, namedParamsRange, tClass, criteriaBuilder, root, predicate, attributes);
            if (columnOrderBy != null && !columnOrderBy.equals("")) {
                criteriaQuery.select(root).where(predicate).orderBy(criteriaBuilder.desc(root.get(columnOrderBy)));
            } else {
                criteriaQuery.select(root).where(predicate);
            }
            TypedQuery<T> query = getEm().createQuery(criteriaQuery);

            if (maxResults != null) {
                query.setMaxResults(maxResults);
            } else {
                query.setMaxResults(100);
            }
            return query.getResultList();
        } catch (NoSuchMethodException e) {
            throw new DAOException(e);
        } catch (InvocationTargetException e) {
            throw new DAOException(e);
        } catch (IllegalAccessException e) {
            throw new DAOException(e);
        }
    }


    public <T extends AbstractEntity> List<T> findListByModel(AbstractEntity example, Integer maxResults, NamedParams namedParamsRange) throws DAOException {
        return findListByModel(example, maxResults, namedParamsRange, null);
    }

    public <T extends AbstractEntity> T findByModelSingleResult(AbstractEntity example, NamedParams namedParamsRange) throws DAOException {
        try {
            namedParamsRange = namedParamsRange != null ? cleanParameters(namedParamsRange) : namedParamsRange;
            Class<T> tClass = (Class<T>) example.getClass();
            CriteriaBuilder criteriaBuilder = getEm().getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tClass);
            Root<T> root = criteriaQuery.from(tClass);
            Predicate predicate = criteriaBuilder.conjunction();
            Metamodel metamodel = getEm().getMetamodel();
            EntityType<T> entityType = metamodel.entity(tClass);
            Set<Attribute<? super T, ?>> attributes = entityType.getAttributes();
            predicate = predicateBuilderFindByModel(example, namedParamsRange, tClass, criteriaBuilder, root, predicate, attributes);
            criteriaQuery.select(root).where(predicate);
            TypedQuery<T> query = getEm().createQuery(criteriaQuery);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private <T extends AbstractEntity> Predicate predicateBuilderFindByModel(AbstractEntity example, NamedParams namedParamsRange, Class<T> tClass, CriteriaBuilder criteriaBuilder, Root<T> root, Predicate predicate, Set<Attribute<? super T, ?>> attributes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DAOException {
        for (Attribute<? super T, ?> attribute : attributes) {
            String name = attribute.getName();
            String javaName = attribute.getJavaMember().getName();
            String getter = StringUtils.join((attribute.getJavaType().equals(Boolean.class) ? "is" : "get"), javaName.substring(0, 1).toUpperCase(), javaName.substring(1));
            Method method = tClass.getMethod(getter, (Class<?>[]) null);
            if (isParameter(example, method, attribute.getJavaType())) {
                if (attribute.getJavaType().getSuperclass() != null) {
                    if (isAbstractEntityInstance(attribute.getJavaType().getSuperclass())) {
                        AbstractEntity abstractEntity = (AbstractEntity) method.invoke(example, (Object[]) null);
                        if (abstractEntity != null) {
                            List<AbstractEntity> entities = findListByModel(abstractEntity);
                            if (!CollectionUtils.isEmpty(entities)) {
                                predicate = criteriaBuilder.and(predicate, criteriaBuilder.in(root.get(name)).value(entities));
                            } else {
                                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(criteriaBuilder.literal(1), criteriaBuilder.literal(0)));
                            }
                        }
                    } else {
                        switch (attribute.getJavaType().getSuperclass().toString()) {
                            case NUMBER_CLASS:
                                if (namedParamsRange != null && namedParamsRange.containsKey(name + FIM)) {
                                    if (namedParamsRange.containsKey(name + INICIO)) {
                                        Expression expressionDateInicio = criteriaBuilder.literal(namedParamsRange.get(name + INICIO));
                                        Expression expressionDateFim = criteriaBuilder.literal(namedParamsRange.get(name + FIM));
                                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(name), expressionDateInicio, expressionDateFim));
                                    } else {
                                        Expression expressionDateFim = criteriaBuilder.literal(namedParamsRange.get(name + FIM));
                                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(root.get(name), expressionDateFim));
                                    }
                                } else {
                                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(name), method.invoke(example, (Object[]) null)));
                                }
                                break;
                            case OBJECT_CLASS:
                                switch (attribute.getJavaType().toString()) {
                                    case STRING_CLASS:
                                        //Buscar por upper case ou pelo valor exato
                                        Predicate or = criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get(name)), method.invoke(example, (Object[]) null).toString().toUpperCase() + "%"));
                                        predicate = criteriaBuilder.and(predicate, or);
                                        break;
                                    case DATE_CLASS:
                                        if (namedParamsRange != null && namedParamsRange.containsKey(name + FIM)) {
                                            if (namedParamsRange.containsKey(name + INICIO)) {
                                                Expression<Date> expressionDateInicio = criteriaBuilder.literal((Date) namedParamsRange.get(name + INICIO));
                                                Expression<Date> expressionDateFim = criteriaBuilder.literal((Date) namedParamsRange.get(name + FIM));
                                                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(name), expressionDateInicio, expressionDateFim));
                                            } else {
                                                Expression<Date> expressionDateFim = criteriaBuilder.literal((Date) namedParamsRange.get(name + FIM));
                                                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(root.get(name), expressionDateFim));
                                            }
                                        }
                                        break;
                                    default:
                                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(name), method.invoke(example, (Object[]) null)));
                                        break;
                                }
                                break;
                            case ENUM_CLASS:
                                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(name), method.invoke(example, (Object[]) null)));
                                break;
                        }
                    }
                }
            }
        }
        return predicate;
    }

    private boolean isAbstractEntityInstance(Class clazz) {
        if (clazz.equals(AbstractEntity.class)) {
            return true;
        } else if (clazz.getSuperclass() != null) {
            return isAbstractEntityInstance(clazz.getSuperclass());
        } else {
            return false;
        }
    }

    private <T extends AbstractEntity> boolean isParameter(T example, Method method, Class<?> javaType) throws InvocationTargetException, IllegalAccessException {
        try {
            return (method.invoke(example) != null && (method.invoke(example)).toString().length() > 0) || javaType == Date.class;
        } catch (InvocationTargetException | NullPointerException | LazyInitializationException e) {
            return false;
        }
    }

    public <T extends AbstractEntity> List<T> findListByModel(T example, NamedParams namedParamsPeriodo) throws DAOException {
        return findListByModel(example, null, namedParamsPeriodo);
    }

    public <T extends AbstractEntity> List<T> findListByModel(T example, Integer maxResults) throws DAOException {
        return findListByModel(example, maxResults, null);
    }

    public <T extends AbstractEntity> List<T> findListByModel(T example) throws DAOException {
        return findListByModel(example, null, null);
    }

}



