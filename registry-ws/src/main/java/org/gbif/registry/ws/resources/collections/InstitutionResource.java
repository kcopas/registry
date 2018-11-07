package org.gbif.registry.ws.resources.collections;

import org.gbif.api.model.collections.Institution;
import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.service.collections.InstitutionService;
import org.gbif.registry.persistence.mapper.IdentifierMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.persistence.mapper.collections.AddressMapper;
import org.gbif.registry.persistence.mapper.collections.InstitutionMapper;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Class that acts both as the WS endpoint for {@link Institution} entities and also provides an *
 * implementation of {@link InstitutionService}.
 */
@Singleton
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("grbio/institution")
public class InstitutionResource extends BaseExtendableCollectionResource<Institution>
    implements InstitutionService {

  private final InstitutionMapper institutionMapper;

  @Inject
  public InstitutionResource(InstitutionMapper institutionMapper, AddressMapper addressMapper, IdentifierMapper identifierMapper, TagMapper tagMapper) {
    super(institutionMapper, addressMapper, institutionMapper, tagMapper, institutionMapper, identifierMapper, institutionMapper);
    this.institutionMapper = institutionMapper;
  }

  @GET
  public PagingResponse<Institution> list(@Nullable @QueryParam("q") String query, @Context Pageable page) {
    long total = institutionMapper.count(query);
    return new PagingResponse<>(page, total, institutionMapper.list(query, page));
  }
}
