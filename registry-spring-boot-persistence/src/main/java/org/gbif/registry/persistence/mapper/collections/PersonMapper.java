package org.gbif.registry.persistence.mapper.collections;

import org.gbif.api.model.collections.Person;
import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.registry.search.collections.PersonSuggestResult;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Mapper for {@link Person} entities.
 */
@Repository
public interface PersonMapper extends BaseMapper<Person> {

  // TODO: 2019-07-24 get, create, delete, update inherited explicitly because of exception
  @Override
  Person get(@Param("key") UUID key);

  @Override
  void create(Person entity);

  @Override
  void delete(@Param("key") UUID key);

  @Override
  void update(Person entity);

  List<Person> list(@Nullable @Param("institutionKey") UUID institutionKey,
                    @Nullable @Param("collectionKey") UUID collectionKey,
                    @Nullable @Param("query") String query,
                    @Nullable @Param("page") Pageable page);

  long count(@Nullable @Param("institutionKey") UUID institutionKey,
             @Nullable @Param("collectionKey") UUID collectionKey,
             @Nullable @Param("query") String query);

  /**
   * A simple suggest by title service.
   */
  List<PersonSuggestResult> suggest(@Nullable @Param("q") String q);

  /**
   * @return the persons marked as deleted
   */
  List<Person> deleted(@Param("page") Pageable page);

  /**
   * @return the count of the persons marked as deleted.
   */
  long countDeleted();

}
