/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry.ws.resources;

import org.gbif.api.annotation.NullToNotFound;
import org.gbif.api.annotation.Trim;
import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.ConfirmationKeyParameter;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Installation;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.model.registry.PrePersist;
import org.gbif.api.model.registry.search.KeyTitleResult;
import org.gbif.api.service.registry.OrganizationService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry.domain.ws.OrganizationRequestSearchParams;
import org.gbif.registry.events.EventManager;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.DatasetMapper;
import org.gbif.registry.persistence.mapper.InstallationMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.service.MapperServiceLocator;
import org.gbif.registry.security.EditorAuthorizationService;
import org.gbif.registry.security.SecurityContextCheck;
import org.gbif.registry.ws.surety.OrganizationEndorsementService;
import org.gbif.ws.WebApplicationException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.gbif.registry.security.UserRoles.ADMIN_ROLE;
import static org.gbif.registry.security.UserRoles.APP_ROLE;
import static org.gbif.registry.security.UserRoles.EDITOR_ROLE;

@SuppressWarnings("UnstableApiUsage")
@Validated
@Primary
@RestController
@RequestMapping(value = "organization", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganizationResource extends BaseNetworkEntityResource<Organization>
    implements OrganizationService {

  private static final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

  public static final int MINIMUM_PASSWORD_SIZE = 12;
  public static final int MAXIMUM_PASSWORD_SIZE = 15;
  private static final String ALLOWED_CHARACTERS =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

  private final DatasetMapper datasetMapper;
  private final OrganizationMapper organizationMapper;
  private final InstallationMapper installationMapper;
  private final OrganizationEndorsementService<UUID> organizationEndorsementService;
  private final EditorAuthorizationService userAuthService;

  public OrganizationResource(
      MapperServiceLocator mapperServiceLocator,
      OrganizationEndorsementService<UUID> organizationEndorsementService,
      EventManager eventManager,
      EditorAuthorizationService userAuthService,
      WithMyBatis withMyBatis) {
    super(
        mapperServiceLocator.getOrganizationMapper(),
        mapperServiceLocator,
        Organization.class,
        eventManager,
        userAuthService,
        withMyBatis);
    this.datasetMapper = mapperServiceLocator.getDatasetMapper();
    this.organizationMapper = mapperServiceLocator.getOrganizationMapper();
    this.installationMapper = mapperServiceLocator.getInstallationMapper();
    this.organizationEndorsementService = organizationEndorsementService;
    this.userAuthService = userAuthService;
  }

  @GetMapping("{key}")
  @NullToNotFound("/organization/{key}")
  @Override
  public Organization get(@PathVariable UUID key) {
    return super.get(key);
  }

  /**
   * This method overrides the create method for an organization, populating the password field with
   * a randomly generated string before passing on to the superclass create method.
   *
   * @param organization organization
   * @return key of entity created
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Validated({PrePersist.class, Default.class})
  @Secured({ADMIN_ROLE, EDITOR_ROLE, APP_ROLE})
  @Trim
  @Override
  public UUID create(@RequestBody @Trim Organization organization) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    organization.setPassword(generatePassword());
    UUID newOrganization = super.create(organization);

    if (SecurityContextCheck.checkUserInRole(authentication, APP_ROLE)) {
      // for trusted app, we accept contacts to include on the endorsement request
      Optional.ofNullable(organization.getContacts())
          .filter(c -> !c.isEmpty())
          .ifPresent(contacts -> contacts.forEach(c -> addContact(newOrganization, c)));
      Optional.ofNullable(organization.getComments())
          .filter(c -> !c.isEmpty())
          .ifPresent(comments -> comments.forEach(c -> addComment(newOrganization, c)));
      organizationEndorsementService.onNewOrganization(organization);
    }
    return newOrganization;
  }

  /**
   * Randomly generates a shared token (password) for an organization.
   *
   * @return generated password
   */
  @VisibleForTesting
  protected static String generatePassword() {
    Random random = new Random();
    // randomly calculate the size of the password, between 0 and MAXIMUM_PASSWORD_SIZE
    int size = random.nextInt(MAXIMUM_PASSWORD_SIZE);
    // ensure the size is at least greater than or equal to MINIMUM_PASSWORD_SIZE
    size = Math.max(size, MINIMUM_PASSWORD_SIZE);

    // generate the password
    StringBuilder password = new StringBuilder();
    int randomIndex;
    while (size-- > 0) {
      randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
      password.append(ALLOWED_CHARACTERS.charAt(randomIndex));
    }
    return password.toString();
  }

  @Override
  public PagingResponse<Organization> search(String query, Pageable page) {
    final OrganizationRequestSearchParams request = new OrganizationRequestSearchParams();
    request.setQ(query);
    return list(null, request, page);
  }

  /**
   * All network entities support simple (!) search with "&q=". This is to support the console user
   * interface, and is in addition to any complex, faceted search that might additionally be
   * supported, such as dataset search.
   */
  @GetMapping
  public PagingResponse<Organization> list(
      @Nullable Country country, @Valid OrganizationRequestSearchParams request, Pageable page) {
    // Hack: Intercept identifier search
    if (request.getIdentifierType() != null && request.getIdentifier() != null) {
      return listByIdentifier(request.getIdentifierType(), request.getIdentifier(), page);
    } else if (request.getIdentifier() != null) {
      return listByIdentifier(request.getIdentifier(), page);
    }

    // Intercept machine tag search
    if (!Strings.isNullOrEmpty(request.getMachineTagNamespace())
        || !Strings.isNullOrEmpty(request.getMachineTagName())
        || !Strings.isNullOrEmpty(request.getMachineTagValue())) {
      return listByMachineTag(
          request.getMachineTagNamespace(),
          request.getMachineTagName(),
          request.getMachineTagValue(),
          page);
    }

    // short circuited list all
    if (country == null
        && request.getIsEndorsed() == null
        && Strings.isNullOrEmpty(request.getQ())) {
      return list(page);
    }

    // This uses to Organization Mapper overloaded option of search which will scope (AND) the
    // query, country and endorsement.
    String query =
        request.getQ() != null
            ? Strings.emptyToNull(CharMatcher.WHITESPACE.trimFrom(request.getQ()))
            : request.getQ();
    long total = organizationMapper.count(query, country, request.getIsEndorsed());
    page = page == null ? new PagingRequest() : page;
    return new PagingResponse<>(
        page.getOffset(),
        page.getLimit(),
        total,
        organizationMapper.search(query, country, request.getIsEndorsed(), page));
  }

  @GetMapping("{key}/hostedDataset")
  @Override
  public PagingResponse<Dataset> hostedDatasets(
      @PathVariable("key") UUID organizationKey, Pageable page) {
    return pagingResponse(
        page,
        datasetMapper.countDatasetsHostedBy(organizationKey),
        datasetMapper.listDatasetsHostedBy(organizationKey, page));
  }

  @GetMapping("{key}/publishedDataset")
  @Override
  public PagingResponse<Dataset> publishedDatasets(
      @PathVariable("key") UUID organizationKey, Pageable page) {
    return pagingResponse(
        page,
        datasetMapper.countDatasetsPublishedBy(organizationKey),
        datasetMapper.listDatasetsPublishedBy(organizationKey, page));
  }

  /**
   * This is an HTTP only method to provide the count for the homepage of the portal. The homepage
   * count excludes non publishing an non endorsed datasets.
   */
  @GetMapping("count")
  public int countOrganizations() {
    return organizationMapper.countPublishing();
  }

  @GetMapping("{key}/installation")
  @Override
  public PagingResponse<Installation> installations(
      @PathVariable("key") UUID organizationKey, Pageable page) {
    return pagingResponse(
        page,
        installationMapper.countInstallationsByOrganization(organizationKey),
        installationMapper.listInstallationsByOrganization(organizationKey, page));
  }

  @Override
  public PagingResponse<Organization> listByCountry(Country country, Pageable page) {
    return pagingResponse(
        page,
        organizationMapper.countOrganizationsByCountry(country),
        organizationMapper.organizationsByCountry(country, page));
  }

  @GetMapping("deleted")
  @Override
  public PagingResponse<Organization> listDeleted(Pageable page) {
    return pagingResponse(
        page, organizationMapper.countDeleted(), organizationMapper.deleted(page));
  }

  @GetMapping("pending")
  @Override
  public PagingResponse<Organization> listPendingEndorsement(Pageable page) {
    return pagingResponse(
        page,
        organizationMapper.countPendingEndorsements(null),
        organizationMapper.pendingEndorsements(null, page));
  }

  @GetMapping("nonPublishing")
  @Override
  public PagingResponse<Organization> listNonPublishing(Pageable page) {
    return pagingResponse(
        page, organizationMapper.countNonPublishing(), organizationMapper.nonPublishing(page));
  }

  @GetMapping("suggest")
  @Override
  public List<KeyTitleResult> suggest(@RequestParam(value = "q", required = false) String label) {
    return organizationMapper.suggest(label);
  }

  /**
   * This is an HTTP only method to retrieve the shared token (password) for an organization.
   *
   * @param organizationKey organization key
   * @return password if set, warning message if not set, or null if organization doesn't exist
   */
  @GetMapping(value = "{key}/password", produces = MediaType.TEXT_PLAIN_VALUE)
  @Secured({ADMIN_ROLE, EDITOR_ROLE})
  public String retrievePassword(@PathVariable("key") UUID organizationKey) {
    Organization o = get(organizationKey);
    if (o == null) {
      return null;
    }
    // Organization.password is never null according to database schema. API doesn't mirror this
    // though.
    return o.getPassword();
  }

  /**
   * Confirm the endorsement of an organization.
   * This endpoint is used by email endorsement.
   */
  @PostMapping(path = "{key}/endorsement", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Secured(APP_ROLE)
  public ResponseEntity<Void> confirmEndorsement(
      @PathVariable("key") UUID organizationKey,
      @RequestBody @Valid @NotNull ConfirmationKeyParameter confirmationKeyParameter) {
    return (confirmEndorsement(organizationKey, confirmationKeyParameter.getConfirmationKey())
            ? ResponseEntity.noContent()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST))
        .build();
  }

  @PostMapping("{key}/endorsement/{confirmationKey}")
  @Secured(APP_ROLE)
  @Override
  public boolean confirmEndorsement(
      @PathVariable("key") UUID organizationKey,
      @PathVariable("confirmationKey") UUID confirmationKey) {
    return organizationEndorsementService.confirmEndorsement(organizationKey, confirmationKey);
  }

  /**
   * Confirm the endorsement of an organization.
   * This endpoint is used by the registry console endorsement.
   */
  @PutMapping("{key}/endorsement")
  @Secured({ADMIN_ROLE, EDITOR_ROLE})
  public ResponseEntity<Void> confirmEndorsementEndpoint(
      @PathVariable("key") UUID organizationKey) {
    boolean isEndorsed = confirmEndorsement(organizationKey);
    return (isEndorsed ? ResponseEntity.noContent() : ResponseEntity.status(HttpStatus.BAD_REQUEST))
        .build();
  }

  @Override
  public boolean confirmEndorsement(UUID organizationKey) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Organization organization = super.get(organizationKey);
    checkNotNull(organization, "Organization not found");

    allowedToEndorseOrganization(authentication, organization);

    return organizationEndorsementService.confirmEndorsement(organizationKey);
  }

  /**
   * Revoke the endorsement from an organization.
   * This endpoint is used by the registry console endorsement.
   */
  @DeleteMapping("{key}/endorsement")
  @Secured({ADMIN_ROLE, EDITOR_ROLE})
  public ResponseEntity<Void> revokeEndorsementEndpoint(@PathVariable("key") UUID organizationKey) {
    return (revokeEndorsement(organizationKey)
            ? ResponseEntity.noContent()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST))
        .build();
  }

  @Override
  public boolean revokeEndorsement(UUID organizationKey) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Organization organization = super.get(organizationKey);
    checkNotNull(organization, "Organization not found");

    allowedToEndorseOrganization(authentication, organization);

    return organizationEndorsementService.revokeEndorsement(organizationKey);
  }

  @GetMapping("{key}/endorsement/user/{username}")
  public ResponseEntity<Void> userAllowedToEndorseOrganization(
      @PathVariable("key") UUID organizationKey, @PathVariable("username") String username) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Organization organization = super.get(organizationKey);
    checkNotNull(organization, "Organization not found");

    if (!SecurityContextCheck.checkUserInRole(authentication, ADMIN_ROLE)
        && !userAuthService.allowedToModifyEntity(username, organization.getEndorsingNodeKey())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private void allowedToEndorseOrganization(
      Authentication authentication, Organization organization) {
    String nameFromContext = authentication != null ? authentication.getName() : null;

    // If  the user is only an EDITOR, they must have node permission.
    if (!SecurityContextCheck.checkUserInRole(authentication, ADMIN_ROLE)
        && !userAuthService.allowedToModifyEntity(
            nameFromContext, organization.getEndorsingNodeKey())) {
      LOG.warn(
          "User {} is not allowed to endorse organization {}",
          nameFromContext,
          organization.getKey());
      throw new WebApplicationException(
          MessageFormat.format(
              "User {0} is not allowed to endorse organization {1}",
              nameFromContext, organization.getKey()),
          HttpStatus.FORBIDDEN);
    }
  }
}
