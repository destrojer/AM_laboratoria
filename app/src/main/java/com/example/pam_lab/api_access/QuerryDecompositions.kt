package com.example.pam_lab.api_access

data class OverpassResponse(
    val elements: List<OsmElement>
)

data class OsmElement(
    val id: Long,
    val tags: Map<String, String>?
)
{
    val name: String get() = tags?.get("name") ?: "Bez nazwy"
    val description: String get() = tags?.get("description") ?: tags?.get("note") ?: "Brak opisu dla tej trasy."
}