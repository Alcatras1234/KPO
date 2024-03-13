import kotlinx.serialization.Serializable

@Serializable
class Review(val nameFood: String = "", val grade : Int = 0, val comment : String = "") {
}