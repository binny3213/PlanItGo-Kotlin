import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.NearbyPlacesResponse
import com.google.gson.*
import java.lang.reflect.Type

class NearbyPlacesResponseDeserializer : JsonDeserializer<NearbyPlacesResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NearbyPlacesResponse {
        val jsonObject = json.asJsonObject
        val resultsArray = jsonObject.getAsJsonArray("results")
        val apiAttractions = resultsArray.map { placeJson ->
            ApiAttraction(
                placeId = placeJson.asJsonObject.get("place_id").asString,
                name = placeJson.asJsonObject.get("name")?.asString ?: "Unknown",
                rating = placeJson.asJsonObject.get("rating")?.asString,
                photoReference = placeJson.asJsonObject.getAsJsonArray("photos")?.firstOrNull()
                    ?.asJsonObject?.get("photo_reference")?.asString,
                address = placeJson.asJsonObject.get("vicinity")?.asString,
                icon = placeJson.asJsonObject.get("icon")?.asString
            )
        }
        return NearbyPlacesResponse(apiAttractions)
    }
}