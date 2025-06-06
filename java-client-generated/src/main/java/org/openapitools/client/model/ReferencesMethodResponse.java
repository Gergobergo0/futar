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
import java.util.Arrays;
import org.openapitools.client.model.ReferencesMethodResult;
import org.openapitools.client.model.TransitReferences;

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
 * ReferencesMethodResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-04-15T15:47:41.658152+02:00[Europe/Budapest]", comments = "Generator version: 7.12.0")
public class ReferencesMethodResponse {
  public static final String SERIALIZED_NAME_LIMIT_EXCEEDED = "limitExceeded";
  @SerializedName(SERIALIZED_NAME_LIMIT_EXCEEDED)
  @javax.annotation.Nullable
  private Boolean limitExceeded;

  public static final String SERIALIZED_NAME_ENTRY = "entry";
  @SerializedName(SERIALIZED_NAME_ENTRY)
  @javax.annotation.Nullable
  private ReferencesMethodResult entry;

  public static final String SERIALIZED_NAME_REFERENCES = "references";
  @SerializedName(SERIALIZED_NAME_REFERENCES)
  @javax.annotation.Nullable
  private TransitReferences references;

  public static final String SERIALIZED_NAME_PROPERTY_CLASS = "class";
  @SerializedName(SERIALIZED_NAME_PROPERTY_CLASS)
  @javax.annotation.Nullable
  private String propertyClass;

  public ReferencesMethodResponse() {
  }

  public ReferencesMethodResponse limitExceeded(@javax.annotation.Nullable Boolean limitExceeded) {
    this.limitExceeded = limitExceeded;
    return this;
  }

  /**
   * Igaz, ha a lista több elemet tartalmaz, mint a limit paraméter. Indulási és érkezési idő típusú lekéréseknél használjuk.
   * @return limitExceeded
   */
  @javax.annotation.Nullable
  public Boolean getLimitExceeded() {
    return limitExceeded;
  }

  public void setLimitExceeded(@javax.annotation.Nullable Boolean limitExceeded) {
    this.limitExceeded = limitExceeded;
  }


  public ReferencesMethodResponse entry(@javax.annotation.Nullable ReferencesMethodResult entry) {
    this.entry = entry;
    return this;
  }

  /**
   * Get entry
   * @return entry
   */
  @javax.annotation.Nullable
  public ReferencesMethodResult getEntry() {
    return entry;
  }

  public void setEntry(@javax.annotation.Nullable ReferencesMethodResult entry) {
    this.entry = entry;
  }


  public ReferencesMethodResponse references(@javax.annotation.Nullable TransitReferences references) {
    this.references = references;
    return this;
  }

  /**
   * Get references
   * @return references
   */
  @javax.annotation.Nullable
  public TransitReferences getReferences() {
    return references;
  }

  public void setReferences(@javax.annotation.Nullable TransitReferences references) {
    this.references = references;
  }


  public ReferencesMethodResponse propertyClass(@javax.annotation.Nullable String propertyClass) {
    this.propertyClass = propertyClass;
    return this;
  }

  /**
   * Az adat típusa. Egy entitás esetén \&quot;entryWithReferences\&quot;.
   * @return propertyClass
   */
  @javax.annotation.Nullable
  public String getPropertyClass() {
    return propertyClass;
  }

  public void setPropertyClass(@javax.annotation.Nullable String propertyClass) {
    this.propertyClass = propertyClass;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferencesMethodResponse referencesMethodResponse = (ReferencesMethodResponse) o;
    return Objects.equals(this.limitExceeded, referencesMethodResponse.limitExceeded) &&
        Objects.equals(this.entry, referencesMethodResponse.entry) &&
        Objects.equals(this.references, referencesMethodResponse.references) &&
        Objects.equals(this.propertyClass, referencesMethodResponse.propertyClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(limitExceeded, entry, references, propertyClass);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferencesMethodResponse {\n");
    sb.append("    limitExceeded: ").append(toIndentedString(limitExceeded)).append("\n");
    sb.append("    entry: ").append(toIndentedString(entry)).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
    sb.append("    propertyClass: ").append(toIndentedString(propertyClass)).append("\n");
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
    openapiFields.add("limitExceeded");
    openapiFields.add("entry");
    openapiFields.add("references");
    openapiFields.add("class");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to ReferencesMethodResponse
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!ReferencesMethodResponse.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in ReferencesMethodResponse is not found in the empty JSON string", ReferencesMethodResponse.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!ReferencesMethodResponse.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `ReferencesMethodResponse` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      // validate the optional field `entry`
      if (jsonObj.get("entry") != null && !jsonObj.get("entry").isJsonNull()) {
        ReferencesMethodResult.validateJsonElement(jsonObj.get("entry"));
      }
      // validate the optional field `references`
      if (jsonObj.get("references") != null && !jsonObj.get("references").isJsonNull()) {
        TransitReferences.validateJsonElement(jsonObj.get("references"));
      }
      if ((jsonObj.get("class") != null && !jsonObj.get("class").isJsonNull()) && !jsonObj.get("class").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `class` to be a primitive type in the JSON string but got `%s`", jsonObj.get("class").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ReferencesMethodResponse.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ReferencesMethodResponse' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ReferencesMethodResponse> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ReferencesMethodResponse.class));

       return (TypeAdapter<T>) new TypeAdapter<ReferencesMethodResponse>() {
           @Override
           public void write(JsonWriter out, ReferencesMethodResponse value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public ReferencesMethodResponse read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of ReferencesMethodResponse given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of ReferencesMethodResponse
   * @throws IOException if the JSON string is invalid with respect to ReferencesMethodResponse
   */
  public static ReferencesMethodResponse fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ReferencesMethodResponse.class);
  }

  /**
   * Convert an instance of ReferencesMethodResponse to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

