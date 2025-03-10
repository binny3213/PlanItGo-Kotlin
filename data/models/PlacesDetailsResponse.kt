package com.example.planitgo_finalproject.data.models

data class PlacesDetailsResponse(
    val result: PlaceDetailsResult
)
data class PlaceDetailsResult(
    val opening_hours: OpeningHours?,
    val editorial_summary: EditorialSummary?
)

data class OpeningHours(val weekday_text: List<String>)
data class EditorialSummary(val overview: String?)