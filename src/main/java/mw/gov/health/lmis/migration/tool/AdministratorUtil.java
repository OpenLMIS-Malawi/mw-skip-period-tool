package mw.gov.health.lmis.migration.tool;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.migration.tool.config.ToolProperties;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

@Component
class AdministratorUtil {
  private static final String ACCESS_TOKEN = "access_token";

  @Autowired
  private ToolProperties toolProperties;

  void assignPrograms(String... programs) {
    String serverUrl = toolProperties.getParameters().getServerUrl();
    String adminId = toolProperties.getParameters().getAdminId();

    JsonObject admin = findOne(serverUrl, UUID.fromString(adminId));

    JsonArrayBuilder roleAssignmentsBuilder = Json.createArrayBuilder();
    admin.getJsonArray("roleAssignments").forEach(roleAssignmentsBuilder::add);

    for (String program : programs) {
      JsonObject object = Json
          .createObjectBuilder()
          .add("programCode", program)
          .add("roleId", toolProperties.getParameters().getProgramSupervisorRoleId())
          .add("supervisoryNodeCode", "supervisory-node-program")
          .build();

      roleAssignmentsBuilder.add(object);
    }

    JsonObjectBuilder adminBuilder = Json.createObjectBuilder();
    admin.forEach(adminBuilder::add);
    adminBuilder.add("roleAssignments", roleAssignmentsBuilder.build());

    update(serverUrl, adminBuilder.build());
  }

  private void update(String serverUrl, JsonObject object) {
    Map<String, String> params = new HashMap<>();
    params.put(ACCESS_TOKEN, obtainAccessToken(serverUrl));

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      StringEntity entity = new StringEntity(object.toString());
      entity.setContentType("application/json; charset=utf8");

      HttpPut httpPut = new HttpPut(buildUri(serverUrl + "/api/users/", params));
      httpPut.setEntity(entity);

      httpClient.execute(httpPut);
    } catch (IOException exp) {
      throw new IllegalStateException("Can't update", exp);
    }
  }

  private JsonObject findOne(String serverUrl, UUID id) {
    Map<String, String> params = new HashMap<>();
    params.put(ACCESS_TOKEN, obtainAccessToken(serverUrl));

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet httpGet = new HttpGet(buildUri(serverUrl + "/api/users/" + id, params));

      CloseableHttpResponse response = httpClient.execute(httpGet);
      HttpEntity entity = response.getEntity();

      return convertToJsonObject(EntityUtils.toString(entity, "UTF-8"));
    } catch (IOException exp) {
      throw new IllegalStateException("Can't find one", exp);
    }
  }

  private String obtainAccessToken(String serverUrl) {
    String clientId = toolProperties.getParameters().getClientId();
    String clientSecret = toolProperties.getParameters().getClientSecret();

    String plainCreds = clientId + ":" + clientSecret;
    byte[] plainCredsBytes = plainCreds.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);

    Map<String, String> params = new HashMap<>();
    params.put("grant_type", "client_credentials");

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(buildUri(serverUrl + "/api/oauth/token", params));
      httpPost.setHeader("Authorization", "Basic " + base64Creds);

      CloseableHttpResponse response = httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();
      JsonObject object = convertToJsonObject(EntityUtils.toString(entity, "UTF-8"));

      return object.getString(ACCESS_TOKEN);
    } catch (IOException exp) {
      throw new IllegalStateException("Can't obtain access token", exp);
    }
  }

  private URI buildUri(String url, Map<String, String> params) {
    URIBuilder builder = new URIBuilder(URI.create(url));

    params.entrySet().forEach(e -> builder.addParameter(e.getKey(), e.getValue()));

    try {
      return builder.build();
    } catch (URISyntaxException exp) {
      throw new IllegalStateException("Can't create URI", exp);
    }
  }

  private JsonObject convertToJsonObject(String body) {
    try (JsonReader reader = Json.createReader(new StringReader(body))) {
      return reader.readObject();
    }
  }

}
