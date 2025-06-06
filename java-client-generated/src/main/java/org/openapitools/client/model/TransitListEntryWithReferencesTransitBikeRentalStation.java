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
import org.openapitools.client.model.TransitBikeRentalStation;
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
 * A válasz adat.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-04-15T15:47:41.658152+02:00[Europe/Budapest]", comments = "Generator version: 7.12.0")
public class TransitListEntryWithReferencesTransitBikeRentalStation {
  public static final String SERIALIZED_NAME_LIST = "list";
  @SerializedName(SERIALIZED_NAME_LIST)
  @javax.annotation.Nullable
  private List<TransitBikeRentalStation> _list = new ArrayList<>();

  public static final String SERIALIZED_NAME_OUT_OF_RANGE = "outOfRange";
  @SerializedName(SERIALIZED_NAME_OUT_OF_RANGE)
  @javax.annotation.Nullable
  private Boolean outOfRange;

  public static final String SERIALIZED_NAME_LIMIT_EXCEEDED = "limitExceeded";
  @SerializedName(SERIALIZED_NAME_LIMIT_EXCEEDED)
  @javax.annotation.Nullable
  private Boolean limitExceeded;

  public static final String SERIALIZED_NAME_REFERENCES = "references";
  @SerializedName(SERIALIZED_NAME_REFERENCES)
  @javax.annotation.Nullable
  private TransitReferences references;

  public static final String SERIALIZED_NAME_PROPERTY_CLASS = "class";
  @SerializedName(SERIALIZED_NAME_PROPERTY_CLASS)
  @javax.annotation.Nullable
  private String propertyClass;

  public TransitListEntryWithReferencesTransitBikeRentalStation() {
  }

  public TransitListEntryWithReferencesTransitBikeRentalStation _list(@javax.annotation.Nullable List<TransitBikeRentalStation> _list) {
    this._list = _list;
    return this;
  }

  public TransitListEntryWithReferencesTransitBikeRentalStation addListItem(TransitBikeRentalStation _listItem) {
    if (this._list == null) {
      this._list = new ArrayList<>();
    }
    this._list.add(_listItem);
    return this;
  }

  /**
   * A lekért adatok listája.
   * @return _list
   */
  @javax.annotation.Nullable
  public List<TransitBikeRentalStation> getList() {
    return _list;
  }

  public void setList(@javax.annotation.Nullable List<TransitBikeRentalStation> _list) {
    this._list = _list;
  }


  public TransitListEntryWithReferencesTransitBikeRentalStation outOfRange(@javax.annotation.Nullable Boolean outOfRange) {
    this.outOfRange = outOfRange;
    return this;
  }

  /**
   * Az értéke mindig &#x60;false&#x60;.
   * @return outOfRange
   */
  @javax.annotation.Nullable
  public Boolean getOutOfRange() {
    return outOfRange;
  }

  public void setOutOfRange(@javax.annotation.Nullable Boolean outOfRange) {
    this.outOfRange = outOfRange;
  }


  public TransitListEntryWithReferencesTransitBikeRentalStation limitExceeded(@javax.annotation.Nullable Boolean limitExceeded) {
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


  public TransitListEntryWithReferencesTransitBikeRentalStation references(@javax.annotation.Nullable TransitReferences references) {
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


  public TransitListEntryWithReferencesTransitBikeRentalStation propertyClass(@javax.annotation.Nullable String propertyClass) {
    this.propertyClass = propertyClass;
    return this;
  }

  /**
   * Az adat típusa. Lista esetén \&quot;listWithReferences\&quot;.
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
    TransitListEntryWithReferencesTransitBikeRentalStation transitListEntryWithReferencesTransitBikeRentalStation = (TransitListEntryWithReferencesTransitBikeRentalStation) o;
    return Objects.equals(this._list, transitListEntryWithReferencesTransitBikeRentalStation._list) &&
        Objects.equals(this.outOfRange, transitListEntryWithReferencesTransitBikeRentalStation.outOfRange) &&
        Objects.equals(this.limitExceeded, transitListEntryWithReferencesTransitBikeRentalStation.limitExceeded) &&
        Objects.equals(this.references, transitListEntryWithReferencesTransitBikeRentalStation.references) &&
        Objects.equals(this.propertyClass, transitListEntryWithReferencesTransitBikeRentalStation.propertyClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_list, outOfRange, limitExceeded, references, propertyClass);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransitListEntryWithReferencesTransitBikeRentalStation {\n");
    sb.append("    _list: ").append(toIndentedString(_list)).append("\n");
    sb.append("    outOfRange: ").append(toIndentedString(outOfRange)).append("\n");
    sb.append("    limitExceeded: ").append(toIndentedString(limitExceeded)).append("\n");
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
    openapiFields.add("list");
    openapiFields.add("outOfRange");
    openapiFields.add("limitExceeded");
    openapiFields.add("references");
    openapiFields.add("class");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to TransitListEntryWithReferencesTransitBikeRentalStation
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!TransitListEntryWithReferencesTransitBikeRentalStation.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in TransitListEntryWithReferencesTransitBikeRentalStation is not found in the empty JSON string", TransitListEntryWithReferencesTransitBikeRentalStation.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!TransitListEntryWithReferencesTransitBikeRentalStation.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `TransitListEntryWithReferencesTransitBikeRentalStation` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if (jsonObj.get("list") != null && !jsonObj.get("list").isJsonNull()) {
        JsonArray jsonArray_list = jsonObj.getAsJsonArray("list");
        if (jsonArray_list != null) {
          // ensure the json data is an array
          if (!jsonObj.get("list").isJsonArray()) {
            throw new IllegalArgumentException(String.format("Expected the field `list` to be an array in the JSON string but got `%s`", jsonObj.get("list").toString()));
          }

          // validate the optional field `list` (array)
          for (int i = 0; i < jsonArray_list.size(); i++) {
            TransitBikeRentalStation.validateJsonElement(jsonArray_list.get(i));
          };
        }
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
       if (!TransitListEntryWithReferencesTransitBikeRentalStation.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'TransitListEntryWithReferencesTransitBikeRentalStation' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<TransitListEntryWithReferencesTransitBikeRentalStation> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(TransitListEntryWithReferencesTransitBikeRentalStation.class));

       return (TypeAdapter<T>) new TypeAdapter<TransitListEntryWithReferencesTransitBikeRentalStation>() {
           @Override
           public void write(JsonWriter out, TransitListEntryWithReferencesTransitBikeRentalStation value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public TransitListEntryWithReferencesTransitBikeRentalStation read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of TransitListEntryWithReferencesTransitBikeRentalStation given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of TransitListEntryWithReferencesTransitBikeRentalStation
   * @throws IOException if the JSON string is invalid with respect to TransitListEntryWithReferencesTransitBikeRentalStation
   */
  public static TransitListEntryWithReferencesTransitBikeRentalStation fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, TransitListEntryWithReferencesTransitBikeRentalStation.class);
  }

  /**
   * Convert an instance of TransitListEntryWithReferencesTransitBikeRentalStation to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

