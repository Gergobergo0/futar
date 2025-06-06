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
import org.openapitools.jackson.nullable.JsonNullable;

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
 * OnboardDepartPosition
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-04-15T15:47:41.658152+02:00[Europe/Budapest]", comments = "Generator version: 7.12.0")
public class OnboardDepartPosition {
  public static final String SERIALIZED_NAME_LAT = "lat";
  @SerializedName(SERIALIZED_NAME_LAT)
  @javax.annotation.Nonnull
  private Double lat;

  public static final String SERIALIZED_NAME_LON = "lon";
  @SerializedName(SERIALIZED_NAME_LON)
  @javax.annotation.Nonnull
  private Double lon;

  public static final String SERIALIZED_NAME_TIMESTAMP = "timestamp";
  @SerializedName(SERIALIZED_NAME_TIMESTAMP)
  @javax.annotation.Nonnull
  private Long timestamp;

  public static final String SERIALIZED_NAME_ACCURACY = "accuracy";
  @SerializedName(SERIALIZED_NAME_ACCURACY)
  @javax.annotation.Nullable
  private Double accuracy;

  public static final String SERIALIZED_NAME_SPEED = "speed";
  @SerializedName(SERIALIZED_NAME_SPEED)
  @javax.annotation.Nullable
  private Double speed;

  public OnboardDepartPosition() {
  }

  public OnboardDepartPosition lat(@javax.annotation.Nonnull Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * A szélességi koordinátája egy pozició pontnak.
   * @return lat
   */
  @javax.annotation.Nonnull
  public Double getLat() {
    return lat;
  }

  public void setLat(@javax.annotation.Nonnull Double lat) {
    this.lat = lat;
  }


  public OnboardDepartPosition lon(@javax.annotation.Nonnull Double lon) {
    this.lon = lon;
    return this;
  }

  /**
   * A hosszúsági koordinátája egy pozició pontnak.
   * @return lon
   */
  @javax.annotation.Nonnull
  public Double getLon() {
    return lon;
  }

  public void setLon(@javax.annotation.Nonnull Double lon) {
    this.lon = lon;
  }


  public OnboardDepartPosition timestamp(@javax.annotation.Nonnull Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Az időpontja egy pozició pontnak epoch másodpercben.
   * @return timestamp
   */
  @javax.annotation.Nonnull
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(@javax.annotation.Nonnull Long timestamp) {
    this.timestamp = timestamp;
  }


  public OnboardDepartPosition accuracy(@javax.annotation.Nullable Double accuracy) {
    this.accuracy = accuracy;
    return this;
  }

  /**
   * A pontossága egy pozició pontnak méterben.
   * @return accuracy
   */
  @javax.annotation.Nullable
  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(@javax.annotation.Nullable Double accuracy) {
    this.accuracy = accuracy;
  }


  public OnboardDepartPosition speed(@javax.annotation.Nullable Double speed) {
    this.speed = speed;
    return this;
  }

  /**
   * A sebesség egy pozició pontban m/s-ban.
   * @return speed
   */
  @javax.annotation.Nullable
  public Double getSpeed() {
    return speed;
  }

  public void setSpeed(@javax.annotation.Nullable Double speed) {
    this.speed = speed;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OnboardDepartPosition onboardDepartPosition = (OnboardDepartPosition) o;
    return Objects.equals(this.lat, onboardDepartPosition.lat) &&
        Objects.equals(this.lon, onboardDepartPosition.lon) &&
        Objects.equals(this.timestamp, onboardDepartPosition.timestamp) &&
        Objects.equals(this.accuracy, onboardDepartPosition.accuracy) &&
        Objects.equals(this.speed, onboardDepartPosition.speed);
  }

  private static <T> boolean equalsNullable(JsonNullable<T> a, JsonNullable<T> b) {
    return a == b || (a != null && b != null && a.isPresent() && b.isPresent() && Objects.deepEquals(a.get(), b.get()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(lat, lon, timestamp, accuracy, speed);
  }

  private static <T> int hashCodeNullable(JsonNullable<T> a) {
    if (a == null) {
      return 1;
    }
    return a.isPresent() ? Arrays.deepHashCode(new Object[]{a.get()}) : 31;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OnboardDepartPosition {\n");
    sb.append("    lat: ").append(toIndentedString(lat)).append("\n");
    sb.append("    lon: ").append(toIndentedString(lon)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    accuracy: ").append(toIndentedString(accuracy)).append("\n");
    sb.append("    speed: ").append(toIndentedString(speed)).append("\n");
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
    openapiFields.add("lat");
    openapiFields.add("lon");
    openapiFields.add("timestamp");
    openapiFields.add("accuracy");
    openapiFields.add("speed");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("lat");
    openapiRequiredFields.add("lon");
    openapiRequiredFields.add("timestamp");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to OnboardDepartPosition
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!OnboardDepartPosition.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in OnboardDepartPosition is not found in the empty JSON string", OnboardDepartPosition.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!OnboardDepartPosition.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `OnboardDepartPosition` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : OnboardDepartPosition.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!OnboardDepartPosition.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'OnboardDepartPosition' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<OnboardDepartPosition> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(OnboardDepartPosition.class));

       return (TypeAdapter<T>) new TypeAdapter<OnboardDepartPosition>() {
           @Override
           public void write(JsonWriter out, OnboardDepartPosition value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public OnboardDepartPosition read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of OnboardDepartPosition given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of OnboardDepartPosition
   * @throws IOException if the JSON string is invalid with respect to OnboardDepartPosition
   */
  public static OnboardDepartPosition fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, OnboardDepartPosition.class);
  }

  /**
   * Convert an instance of OnboardDepartPosition to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

