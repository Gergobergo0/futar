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
 * LegTimeZone
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-04-15T15:47:41.658152+02:00[Europe/Budapest]", comments = "Generator version: 7.12.0")
public class LegTimeZone {
  public static final String SERIALIZED_NAME_DISPLAY_NAME = "displayName";
  @SerializedName(SERIALIZED_NAME_DISPLAY_NAME)
  @javax.annotation.Nullable
  private String displayName;

  public static final String SERIALIZED_NAME_ID = "id";
  @SerializedName(SERIALIZED_NAME_ID)
  @javax.annotation.Nullable
  private String id;

  public static final String SERIALIZED_NAME_DSTSAVINGS = "dstsavings";
  @SerializedName(SERIALIZED_NAME_DSTSAVINGS)
  @javax.annotation.Nullable
  private Integer dstsavings;

  public static final String SERIALIZED_NAME_RAW_OFFSET = "rawOffset";
  @SerializedName(SERIALIZED_NAME_RAW_OFFSET)
  @javax.annotation.Nullable
  private Integer rawOffset;

  public LegTimeZone() {
  }

  public LegTimeZone displayName(@javax.annotation.Nullable String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
   */
  @javax.annotation.Nullable
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(@javax.annotation.Nullable String displayName) {
    this.displayName = displayName;
  }


  public LegTimeZone id(@javax.annotation.Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @javax.annotation.Nullable
  public String getId() {
    return id;
  }

  public void setId(@javax.annotation.Nullable String id) {
    this.id = id;
  }


  public LegTimeZone dstsavings(@javax.annotation.Nullable Integer dstsavings) {
    this.dstsavings = dstsavings;
    return this;
  }

  /**
   * Get dstsavings
   * @return dstsavings
   */
  @javax.annotation.Nullable
  public Integer getDstsavings() {
    return dstsavings;
  }

  public void setDstsavings(@javax.annotation.Nullable Integer dstsavings) {
    this.dstsavings = dstsavings;
  }


  public LegTimeZone rawOffset(@javax.annotation.Nullable Integer rawOffset) {
    this.rawOffset = rawOffset;
    return this;
  }

  /**
   * Get rawOffset
   * @return rawOffset
   */
  @javax.annotation.Nullable
  public Integer getRawOffset() {
    return rawOffset;
  }

  public void setRawOffset(@javax.annotation.Nullable Integer rawOffset) {
    this.rawOffset = rawOffset;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LegTimeZone legTimeZone = (LegTimeZone) o;
    return Objects.equals(this.displayName, legTimeZone.displayName) &&
        Objects.equals(this.id, legTimeZone.id) &&
        Objects.equals(this.dstsavings, legTimeZone.dstsavings) &&
        Objects.equals(this.rawOffset, legTimeZone.rawOffset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName, id, dstsavings, rawOffset);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LegTimeZone {\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    dstsavings: ").append(toIndentedString(dstsavings)).append("\n");
    sb.append("    rawOffset: ").append(toIndentedString(rawOffset)).append("\n");
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
    openapiFields.add("displayName");
    openapiFields.add("id");
    openapiFields.add("dstsavings");
    openapiFields.add("rawOffset");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to LegTimeZone
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!LegTimeZone.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in LegTimeZone is not found in the empty JSON string", LegTimeZone.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!LegTimeZone.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `LegTimeZone` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("displayName") != null && !jsonObj.get("displayName").isJsonNull()) && !jsonObj.get("displayName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `displayName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("displayName").toString()));
      }
      if ((jsonObj.get("id") != null && !jsonObj.get("id").isJsonNull()) && !jsonObj.get("id").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `id` to be a primitive type in the JSON string but got `%s`", jsonObj.get("id").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!LegTimeZone.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'LegTimeZone' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<LegTimeZone> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(LegTimeZone.class));

       return (TypeAdapter<T>) new TypeAdapter<LegTimeZone>() {
           @Override
           public void write(JsonWriter out, LegTimeZone value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public LegTimeZone read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of LegTimeZone given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of LegTimeZone
   * @throws IOException if the JSON string is invalid with respect to LegTimeZone
   */
  public static LegTimeZone fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, LegTimeZone.class);
  }

  /**
   * Convert an instance of LegTimeZone to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

