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
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.JsonElement;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Gets or Sets WalkProfile
 */
@JsonAdapter(WalkProfile.Adapter.class)
public enum WalkProfile {
  
  SLOW("SLOW"),
  
  MID("MID"),
  
  FAST("FAST");

  private String value;

  WalkProfile(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static WalkProfile fromValue(String value) {
    for (WalkProfile b : WalkProfile.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }

  public static class Adapter extends TypeAdapter<WalkProfile> {
    @Override
    public void write(final JsonWriter jsonWriter, final WalkProfile enumeration) throws IOException {
      jsonWriter.value(enumeration.getValue());
    }

    @Override
    public WalkProfile read(final JsonReader jsonReader) throws IOException {
      String value = jsonReader.nextString();
      return WalkProfile.fromValue(value);
    }
  }

  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
    String value = jsonElement.getAsString();
    WalkProfile.fromValue(value);
  }
}

