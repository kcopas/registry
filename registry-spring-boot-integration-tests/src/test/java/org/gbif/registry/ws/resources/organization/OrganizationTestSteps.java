package org.gbif.registry.ws.resources.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry.RegistryIntegrationTestsConfiguration;
import org.gbif.registry.utils.Organizations;
import org.gbif.registry.utils.RegistryITUtils;
import org.gbif.registry.ws.TestEmailConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TestEmailConfiguration.class, RegistryIntegrationTestsConfiguration.class},
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrganizationTestSteps {

  private static final UUID UK_NODE_KEY = UUID.fromString("f698c938-d36a-41ac-8120-c35903e1acb9");
  private static final UUID UK_NODE_2_KEY = UUID.fromString("9996f2f2-f71c-4f40-8e69-031917b314e0");

  private static final Map<String, UUID> NODE_MAP = new HashMap<>();

  private ResultActions result;

  private Organization organization;

  private String organizationKey;

  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private DataSource ds;

  private Connection connection;

  @Before("@Organization")
  public void setUp() throws Exception {
    connection = ds.getConnection();
    Objects.requireNonNull(connection, "Connection must not be null");

    ScriptUtils.executeSqlScript(connection,
      new ClassPathResource("/scripts/organization/organization_cleanup.sql"));

    mvc = MockMvcBuilders
      .webAppContextSetup(context)
      .apply(springSecurity())
      .build();

    NODE_MAP.put("UK Node", UK_NODE_KEY);
    NODE_MAP.put("UK Node 2", UK_NODE_2_KEY);
  }

  @After("@Organization")
  public void tearDown() throws Exception {
    ScriptUtils.executeSqlScript(connection,
      new ClassPathResource("/scripts/organization/organization_cleanup.sql"));

    connection.close();
  }

  @Given("node 'UK Node' and node 'UK Node 2'")
  public void prepareNode() {
    ScriptUtils.executeSqlScript(connection,
      new ClassPathResource("/scripts/organization/organization_node_prepare.sql"));
  }

  @Given("seven organizations in 'UK Node'")
  public void prepareOrganizations() {
    ScriptUtils.executeSqlScript(connection,
      new ClassPathResource("/scripts/organization/organization_prepare.sql"));
  }

  @When("^call suggest organizations with query \"([^\"]*)\"$")
  public void callSuggestWithQuery(String query) throws Exception {
    result = mvc
      .perform(
        get("/organization/suggest")
          .param("q", query));
  }

  @Then("response status should be {int}")
  public void checkResponseStatus(int status) throws Exception {
    result
      .andExpect(status().is(status));
  }

  @Then("{int} organization\\(s) should be suggested")
  public void checkSuggestResponse(int number) throws Exception {
    result
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(number));
  }

  @When("^call list organizations by country ([^\"]*)$")
  public void callListWithQuery(Country country) throws Exception {
    result = mvc
      .perform(
        get("/organization")
          .param("country", country.getIso2LetterCode()));
  }

  @Then("{int} organization\\(s) should be listed")
  public void checkListResponse(int number) throws Exception {
    result
      .andExpect(jsonPath("$.count").value(number))
      .andExpect(jsonPath("$.results.length()").value(number));
  }

  @When("create a new organization {string} for {string}")
  public void createOrganization(String orgName, String nodeName) throws Exception {
    UUID nodeKey = NODE_MAP.get(nodeName);
    organization = Organizations.newInstance(nodeKey);
    organization.setTitle(orgName);
    String organizationJson = objectMapper.writeValueAsString(organization);

    result = mvc
      .perform(
        post("/organization")
          .with(httpBasic("justadmin", "welcome"))
          .content(organizationJson)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON));

    organizationKey =
      RegistryITUtils.removeQuotes(result.andReturn().getResponse().getContentAsString());
  }

  @When("get organization by id")
  public void getOrganizationById() throws Exception {
    // get an id for the created organization (remove quotes from string)


    // try to get organization by key
    result = mvc
      .perform(
        get("/organization/{key}", organizationKey));

    // TODO: 16/10/2019 assertLenientEquals and stuff (see registry's NetworkEntityTest)
  }

  // TODO: 18/10/2019 can be generilized?
  @Given("{int} organization\\(s) endorsed for {string}")
  public void checkNumberOfEndorsedOrganizationsForNode(int expected, String nodeName) throws Exception {
    UUID nodeKey = NODE_MAP.get(nodeName);
    mvc
      .perform(
        get("/node/{key}/organization", nodeKey))
      .andDo(print())
      .andExpect(jsonPath("$.count").value(expected))
      .andExpect(jsonPath("$.results.length()").value(expected));
  }

  @Given("{int} organization\\(s) pending endorsement for {string}")
  public void checkNumberOfPendingEndorsementOrganizationForNode(int expected, String nodeName) throws Exception {
    UUID nodeKey = NODE_MAP.get(nodeName);
    mvc
      .perform(
        get("/node/{key}/pendingEndorsement", nodeKey))
      .andDo(print())
      .andExpect(jsonPath("$.count").value(expected))
      .andExpect(jsonPath("$.results.length()").value(expected));
  }

  @Given("{int} organization\\(s) pending endorsement in total")
  public void checkNumberOfPendingEndorsementOrganizationTotal(int expected) throws Exception {
    mvc
      .perform(
        get("/node/pendingEndorsement"))
      .andDo(print())
      .andExpect(jsonPath("$.count").value(expected))
      .andExpect(jsonPath("$.results.length()").value(expected));
    ;
  }

  @When("endorse organization {string}")
  public void endorseOrganization(String orgName) throws Exception {
    organization.setTitle(orgName);
    organization.setEndorsementApproved(true);
    organization.setKey(UUID.fromString(organizationKey));

    String organizationJson = objectMapper.writeValueAsString(organization);

    mvc
      .perform(
        put("/organization/{key}", organizationKey)
          .with(httpBasic("justadmin", "welcome"))
          .content(organizationJson)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON));
  }

  @When("create a new organization for {string} with key")
  public void createOrganizationWithKey(String nodeName) throws Exception {
    UUID nodeKey = NODE_MAP.get(nodeName);
    organization = Organizations.newInstance(nodeKey);
    organization.setKey(UUID.randomUUID());
    organization.setLanguage(null);
    String organizationJson = objectMapper.writeValueAsString(organization);

    result = mvc
      .perform(
        post("/organization")
          .with(httpBasic("justadmin", "welcome"))
          .content(organizationJson)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON))
      .andDo(print());
  }

  @When("get titles for empty list")
  public void getTitlesForEmptyList() throws Exception {
    Collection<UUID> emptyList = Collections.emptyList();

    String contentJson = objectMapper.writeValueAsString(emptyList);

    result = mvc
      .perform(
        post("/organization/titles")
          .content(contentJson)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON))
      .andDo(print());
  }

  @Then("empty titles map is returned")
  public void checkEmptyTitlesMap() throws Exception {
    MvcResult mvcResult = result.andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();
    Map responseBody = objectMapper.readValue(contentAsString, Map.class);

    assertEquals(Collections.emptyMap(), responseBody);
  }

  @When("get titles for organizations")
  public void getTitlesForOrganizations(List<String> keys) throws Exception {
    String contentJson = objectMapper.writeValueAsString(keys);

    result = mvc
      .perform(
        post("/organization/titles")
          .content(contentJson)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON))
      .andDo(print());
  }

  @Then("titles map is returned")
  public void checkTitlesMap(Map expected) throws Exception {
    MvcResult mvcResult = result.andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();
    Map responseBody = objectMapper.readValue(contentAsString, Map.class);

    assertEquals(expected, responseBody);
  }
}
