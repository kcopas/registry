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
package org.gbif.registry.ws;

import org.gbif.api.model.common.GbifUser;
import org.gbif.api.model.registry.ConfirmationKeyParameter;
import org.gbif.registry.database.DatabaseInitializer;
import org.gbif.registry.domain.ws.AuthenticationDataParameters;
import org.gbif.registry.domain.ws.UserAdminView;
import org.gbif.registry.domain.ws.UserCreation;
import org.gbif.registry.identity.model.ModelMutationError;
import org.gbif.registry.identity.model.UserModelMutationResult;
import org.gbif.registry.identity.mybatis.IdentitySuretyTestHelper;
import org.gbif.registry.identity.service.IdentityService;
import org.gbif.registry.persistence.mapper.UserMapper;
import org.gbif.registry.ws.fixtures.RequestTestFixture;
import org.gbif.registry.ws.fixtures.UserTestFixture;
import org.gbif.ws.security.Md5EncodeService;
import org.gbif.ws.security.SigningService;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;

import static org.gbif.registry.ws.fixtures.TestConstants.IT_APP_KEY;
import static org.gbif.registry.ws.fixtures.TestConstants.IT_APP_KEY2;
import static org.gbif.registry.ws.fixtures.TestConstants.TEST_ADMIN;
import static org.gbif.registry.ws.fixtures.UserTestFixture.ALTERNATE_USERNAME;
import static org.gbif.registry.ws.fixtures.UserTestFixture.PASSWORD;
import static org.gbif.registry.ws.fixtures.UserTestFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests related to the User Manager resource (the service itself is tested in the
 * registry-identity module). Due to the fact that all user management operations are not available
 * in the Java ws client, the tests use a direct HTTP client.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RegistryIntegrationTestsConfiguration.class)
@ContextConfiguration(initializers = {UserManagementIT.ContextInitializer.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserManagementIT {

  private static final String CHANGED_PASSWORD = "123456";

  private IdentitySuretyTestHelper identitySuretyTestHelper;
  private UserTestFixture userTestFixture;
  private RequestTestFixture requestTestFixture;

  @RegisterExtension
  static PreparedDbExtension database =
      EmbeddedPostgresExtension.preparedDatabase(
          LiquibasePreparer.forClasspathLocation("liquibase/master.xml"));

  @RegisterExtension
  public final DatabaseInitializer databaseRule =
      new DatabaseInitializer(database.getTestDatabase());

  static class ContextInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(dbTestPropertyPairs())
          .applyTo(configurableApplicationContext.getEnvironment());
      withSearchEnabled(false, configurableApplicationContext.getEnvironment());
    }

    protected static void withSearchEnabled(
        boolean enabled, ConfigurableEnvironment configurableEnvironment) {
      TestPropertyValues.of("searchEnabled=" + enabled).applyTo(configurableEnvironment);
    }

    protected String[] dbTestPropertyPairs() {
      return new String[] {
        "registry.datasource.url=jdbc:postgresql://localhost:"
            + database.getConnectionInfo().getPort()
            + "/"
            + database.getConnectionInfo().getDbName(),
        "registry.datasource.username=" + database.getConnectionInfo().getUser(),
        "registry.datasource.password="
      };
    }
  }

  @Autowired
  public UserManagementIT(
      MockMvc mvc,
      SigningService signingService,
      Md5EncodeService md5EncodeService,
      @Qualifier("registryObjectMapper") ObjectMapper objectMapper,
      IdentityService identityService,
      UserMapper userMapper,
      IdentitySuretyTestHelper identitySuretyTestHelper) {
    this.identitySuretyTestHelper = identitySuretyTestHelper;
    this.userTestFixture =
        new UserTestFixture(identityService, identitySuretyTestHelper, userMapper);
    this.requestTestFixture =
        new RequestTestFixture(mvc, signingService, md5EncodeService, objectMapper);
  }

  @Test
  public void testCreateUser() throws Exception {
    // create a new user
    UserCreation userCreation = UserTestFixture.generateUser(ALTERNATE_USERNAME);

    requestTestFixture
        .postSignedRequest(IT_APP_KEY, userCreation, "/admin/user")
        .andExpect(status().isCreated());

    // test we can't log in (challengeCode not confirmed)
    requestTestFixture
        .getRequest(ALTERNATE_USERNAME, PASSWORD, "/user/login")
        .andExpect(status().isUnauthorized());

    // generate a new request to confirm challengeCode
    ConfirmationKeyParameter params = new ConfirmationKeyParameter();
    params.setConfirmationKey(userTestFixture.getUserChallengeCode(ALTERNATE_USERNAME));

    requestTestFixture
        .postSignedRequest(ALTERNATE_USERNAME, params, "/admin/user/confirm")
        .andExpect(status().isCreated());

    // test we can log in afterwards
    requestTestFixture
        .getRequest(ALTERNATE_USERNAME, PASSWORD, "/user/login")
        .andExpect(status().isOk());
  }

  @Test
  public void testCreateUserNonWhiteListAppKey() throws Exception {
    // create a new user using an app key which is not present in the white list
    UserCreation userCreation = UserTestFixture.generateUser(ALTERNATE_USERNAME);

    // it will authenticate since the appKey is valid, but it won't get the APP role
    requestTestFixture
        .postSignedRequest(IT_APP_KEY2, IT_APP_KEY2, userCreation, "/admin/user")
        .andExpect(status().isForbidden());
  }

  @Test
  public void testResetPassword() throws Exception {
    GbifUser testUser = userTestFixture.prepareUser();
    GbifUser createdUser = userTestFixture.getUser(testUser.getUserName());

    // ensure there is no challengeCode
    UUID challengeCode = identitySuretyTestHelper.getChallengeCode(createdUser.getKey());
    assertNull(challengeCode, "challengeCode shall be null");

    // reset password
    requestTestFixture
        .postSignedRequest(USERNAME, "/admin/user/resetPassword")
        .andExpect(status().isNoContent());

    challengeCode = identitySuretyTestHelper.getChallengeCode(createdUser.getKey());
    assertNotNull(challengeCode, "challengeCode shall exist");

    // we should still be able to login with username/password
    requestTestFixture.getRequest(USERNAME, PASSWORD, "/user/login").andExpect(status().isOk());
  }

  @Test
  public void testUpdatePassword() throws Exception {
    GbifUser testUser = userTestFixture.prepareUser();

    GbifUser createdUser = userTestFixture.getUser(testUser.getUserName());
    AuthenticationDataParameters params = new AuthenticationDataParameters();
    params.setPassword(CHANGED_PASSWORD);
    params.setChallengeCode(UUID.randomUUID());

    requestTestFixture
        .postSignedRequest(USERNAME, params, "/admin/user/updatePassword")
        .andExpect(status().isUnauthorized());

    // ask to reset password
    requestTestFixture
        .postSignedRequest(USERNAME, "/admin/user/resetPassword")
        .andExpect(status().isNoContent());

    UUID confirmationKey = identitySuretyTestHelper.getChallengeCode(createdUser.getKey());
    assertNotNull(confirmationKey, "challengeCode shall exist");

    // ensure we can check if the challengeCode is valid for the user
    Map<String, String> queryParams =
        ImmutableMap.of("confirmationKey", confirmationKey.toString());
    requestTestFixture
        .getSignedRequest(USERNAME, "/admin/user/confirmationKeyValid", queryParams)
        .andExpect(status().isNoContent());

    // change password using that code
    params = new AuthenticationDataParameters();
    params.setPassword(CHANGED_PASSWORD);
    params.setChallengeCode(confirmationKey);

    requestTestFixture
        .postSignedRequest(USERNAME, params, "/admin/user/updatePassword")
        .andExpect(status().isCreated());

    // ensure we can log in with the new password
    requestTestFixture
        .getRequest(USERNAME, CHANGED_PASSWORD, "/user/login")
        .andExpect(status().isOk());
  }

  @Test
  public void getUserFromAdmin() throws Exception {
    GbifUser testUser = userTestFixture.prepareUser();
    GbifUser createdUser = userTestFixture.getUser(testUser.getUserName());

    ResultActions actions =
        requestTestFixture
            .getSignedRequest(IT_APP_KEY, "/admin/user/" + USERNAME)
            .andExpect(status().isOk());

    UserAdminView actualUserAdminView =
        requestTestFixture.extractResponseEntity(actions, UserAdminView.class);

    assertEquals(createdUser.getKey(), actualUserAdminView.getUser().getKey());
  }

  @Test
  public void getUserBySystemSettings() throws Exception {
    userTestFixture.prepareUser();
    Map<String, String> params = ImmutableMap.of("my.settings.key", "100_tacos=100$");
    GbifUser createdUser = userTestFixture.addSystemSettingsToUser(USERNAME, params);

    ResultActions actions =
        requestTestFixture
            .getSignedRequest(IT_APP_KEY, "/admin/user/find", params)
            .andExpect(status().isOk());

    UserAdminView actualUserAdminView =
        requestTestFixture.extractResponseEntity(actions, UserAdminView.class);

    assertEquals(createdUser.getKey(), actualUserAdminView.getUser().getKey());
  }

  @Test
  public void testUpdateUser() throws Exception {
    GbifUser testUser = userTestFixture.prepareUser();
    final String newUserFirstName = "My new first name";

    requestTestFixture.getRequest(USERNAME, PASSWORD, "/user/login").andExpect(status().isOk());

    testUser.setFirstName(newUserFirstName);
    requestTestFixture
        .putSignedRequest(IT_APP_KEY, testUser, "/admin/user/" + USERNAME)
        .andExpect(status().isNoContent());

    // load user directly from the database
    GbifUser updatedUser = userTestFixture.getUser(testUser.getUserName());
    assertEquals(newUserFirstName, updatedUser.getFirstName());

    // create a new user
    GbifUser testUser2 =
        userTestFixture.prepareUser(UserTestFixture.generateUser(ALTERNATE_USERNAME));
    requestTestFixture
        .getRequest(ALTERNATE_USERNAME, PASSWORD, "/user/login")
        .andExpect(status().isOk());

    // update user2 using email from user1
    testUser2.setEmail(testUser.getEmail());
    ResultActions actions =
        requestTestFixture
            .putSignedRequest(IT_APP_KEY, testUser2, "/admin/user/" + ALTERNATE_USERNAME)
            .andExpect(status().isUnprocessableEntity());

    UserModelMutationResult actualUserModelMutationResult =
        requestTestFixture.extractResponseEntity(actions, UserModelMutationResult.class);

    assertEquals(ModelMutationError.EMAIL_ALREADY_IN_USE, actualUserModelMutationResult.getError());

    testUser2.setEmail("12345@mail.com");
    requestTestFixture
        .putSignedRequest(IT_APP_KEY, testUser2, "/admin/user/" + ALTERNATE_USERNAME)
        .andExpect(status().isNoContent());
  }

  @Test
  public void testUserEditorRights() throws Exception {
    // Create a first admin user; this can't be done through the API
    userTestFixture.prepareAdminUser();
    userTestFixture.prepareUser();
    UUID key = UUID.randomUUID();

    // Admin add right
    requestTestFixture
        .postSignedRequestPlainText(TEST_ADMIN, key, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isCreated());

    // Admin see rights
    requestTestFixture
        .getSignedRequest(TEST_ADMIN, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isOk());

    // See own rights
    requestTestFixture
        .getSignedRequest(USERNAME, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isOk());

    // Admin delete right
    requestTestFixture
        .deleteSignedRequest(TEST_ADMIN, "/admin/user/" + USERNAME + "/editorRight/" + key)
        .andExpect(status().isNoContent());
  }

  @Test
  public void testUserEditorRightsErrors() throws Exception {
    // Create a first admin user; this can't be done through the API
    userTestFixture.prepareAdminUser();
    userTestFixture.prepareUser();
    UUID key = UUID.randomUUID();

    // User doesn't exist
    requestTestFixture
        .postSignedRequestPlainText(TEST_ADMIN, key, "/admin/user/someOtherUser/editorRight")
        .andExpect(status().isNotFound());

    // Not an admin user
    requestTestFixture
        .postSignedRequestPlainText(USERNAME, key, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isForbidden());

    // Right already exists
    requestTestFixture
        .postSignedRequestPlainText(TEST_ADMIN, key, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isCreated());
    requestTestFixture
        .postSignedRequestPlainText(TEST_ADMIN, key, "/admin/user/" + USERNAME + "/editorRight")
        .andExpect(status().isConflict());

    // Right doesn't exist
    UUID randomKey = UUID.randomUUID();
    requestTestFixture
        .deleteSignedRequest(TEST_ADMIN, "/admin/user/" + USERNAME + "/editorRight/" + randomKey)
        .andExpect(status().isNotFound());
  }
}
