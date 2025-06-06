/*
 * realCity Query Server API
 * Specification for the realCity Query Server API.
 *
 * The version of the OpenAPI document: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openapitools.client.JSON;

/**
 * ReferencesMethodErrors
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-04-15T15:47:41.658152+02:00[Europe/Budapest]", comments = "Generator version: 7.12.0")
public class ReferencesMethodErrors {
  public static final String SERIALIZED_NAME_AGENCY_IDS = "agencyIds";
  @SerializedName(SERIALIZED_NAME_AGENCY_IDS)
  @javax.annotation.Nullable
  private List<String> agencyIds = new ArrayList<>();

  public static final String SERIALIZED_NAME_ALERT_IDS = "alertIds";
  @SerializedName(SERIALIZED_NAME_ALERT_IDS)
  @javax.annotation.Nullable
  private List<String> alertIds = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROUTE_IDS = "routeIds";
  @SerializedName(SERIALIZED_NAME_ROUTE_IDS)
  @javax.annotation.Nullable
  private List<String> routeIds = new ArrayList<>();

  public static final String SERIALIZED_NAME_STOP_IDS = "stopIds";
  @SerializedName(SERIALIZED_NAME_STOP_IDS)
  @javax.annotation.Nullable
  private List<String> stopIds = new ArrayList<>();

  public ReferencesMethodErrors() {
  }

  public ReferencesMethodErrors agencyIds(@javax.annotation.Nullable List<String> agencyIds) {
    this.agencyIds = agencyIds;
    return this;
  }

  public ReferencesMethodErrors addAgencyIdsItem(String agencyIdsItem) {
    if (this.agencyIds == null) {
      this.agencyIds = new ArrayList<>();
    }
    this.agencyIds.add(agencyIdsItem);
    return this;
  }

  /**
   * A szolgáltató ID-k, amelyek feloldása sikertelen volt.
   * @return agencyIds
   */
  @javax.annotation.Nullable
  public List<String> getAgencyIds() {
    return agencyIds;
  }

  public void setAgencyIds(@javax.annotation.Nullable List<String> agencyIds) {
    this.agencyIds = agencyIds;
  }


  public ReferencesMethodErrors alertIds(@javax.annotation.Nullable List<String> alertIds) {
    this.alertIds = alertIds;
    return this;
  }

  public ReferencesMethodErrors addAlertIdsItem(String alertIdsItem) {
    if (this.alertIds == null) {
      this.alertIds = new ArrayList<>();
    }
    this.alertIds.add(alertIdsItem);
    return this;
  }

  /**
   * A zavar ID-k, amelyek feloldása sikertelen volt.
   * @return alertIds
   */
  @javax.annotation.Nullable
  public List<String> getAlertIds() {
    return alertIds;
  }

  public void setAlertIds(@javax.annotation.Nullable List<String> alertIds) {
    this.alertIds = alertIds;
  }


  public ReferencesMethodErrors routeIds(@javax.annotation.Nullable List<String> routeIds) {
    this.routeIds = routeIds;
    return this;
  }

  public ReferencesMethodErrors addRouteIdsItem(String routeIdsItem) {
    if (this.routeIds == null) {
      this.routeIds = new ArrayList<>();
    }
    this.routeIds.add(routeIdsItem);
    return this;
  }

  /**
   * A járat ID-k, amelyek feloldása sikertelen volt.
   * @return routeIds
   */
  @javax.annotation.Nullable
  public List<String> getRouteIds() {
    return routeIds;
  }

  public void setRouteIds(@javax.annotation.Nullable List<String> routeIds) {
    this.routeIds = routeIds;
  }


  public ReferencesMethodErrors stopIds(@javax.annotation.Nullable List<String> stopIds) {
    this.stopIds = stopIds;
    return this;
  }

  public ReferencesMethodErrors addStopIdsItem(String stopIdsItem) {
    if (this.stopIds == null) {
      this.stopIds = new ArrayList<>();
    }
    this.stopIds.add(stopIdsItem);
    return this;
  }

  /**
   * A megálló ID-k, amelyek feloldása sikertelen volt.
   * @return stopIds
   */
  @javax.annotation.Nullable
  public List<String> getStopIds() {
    return stopIds;
  }

  public void setStopIds(@javax.annotation.Nullable List<String> stopIds) {
    this.stopIds = stopIds;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferencesMethodErrors referencesMethodErrors = (ReferencesMethodErrors) o;
    return Objects.equals(this.agencyIds, referencesMethodErrors.agencyIds) &&
        Objects.equals(this.alertIds, referencesMethodErrors.alertIds) &&
        Objects.equals(this.routeIds, referencesMethodErrors.routeIds) &&
        Objects.equals(this.stopIds, referencesMethodErrors.stopIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agencyIds, alertIds, routeIds, stopIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferencesMethodErrors {\n");
    sb.append("    agencyIds: ").append(toIndentedString(agencyIds)).append("\n");
    sb.append("    alertIds: ").append(toIndentedString(alertIds)).append("\n");
    sb.append("    routeIds: ").append(toIndentedString(routeIds)).append("\n");
    sb.append("    stopIds: ").append(toIndentedString(stopIds)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("agencyIds");
    openapiFields.add("alertIds");
    openapiFields.add("routeIds");
    openapiFields.add("stopIds");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ReferencesMethodErrors
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ReferencesMethodErrors.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ReferencesMethodErrors is not found in the empty JSON string", ReferencesMethodErrors.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!ReferencesMethodErrors.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `ReferencesMethodErrors` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      // ensure the optional json data is an array if present
      if (jsonObj.get("agencyIds") != null && !jsonObj.get("agencyIds").isJsonNull() && !jsonObj.get("agencyIds").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `agencyIds` to be an array in the JSON string but got `%s`", jsonObj.get("agencyIds").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("alertIds") != null && !jsonObj.get("alertIds").isJsonNull() && !jsonObj.get("alertIds").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `alertIds` to be an array in the JSON string but got `%s`", jsonObj.get("alertIds").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("routeIds") != null && !jsonObj.get("routeIds").isJsonNull() && !jsonObj.get("routeIds").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `routeIds` to be an array in the JSON string but got `%s`", jsonObj.get("routeIds").toString()));
      }
      // ensure the optional json data is an array if present
      if (jsonObj.get("stopIds") != null && !jsonObj.get("stopIds").isJsonNull() && !jsonObj.get("stopIds").isJsonArray()) {
        throw new IllegalArgumentException(String.format("Expected the field `stopIds` to be an array in the JSON string but got `%s`", jsonObj.get("stopIds").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ReferencesMethodErrors.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ReferencesMethodErrors' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ReferencesMethodErrors> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ReferencesMethodErrors.class));

       return (TypeAdapter<T>) new TypeAdapter<ReferencesMethodErrors>() {
           @Override
           public void write(JsonWriter out, ReferencesMethodErrors value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public ReferencesMethodErrors read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of ReferencesMethodErrors given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ReferencesMethodErrors
   * @throws IOException if the JSON string is invalid with respect to ReferencesMethodErrors
   */
  public static ReferencesMethodErrors fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ReferencesMethodErrors.class);
  }

  /**
   * Convert an instance of ReferencesMethodErrors to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

