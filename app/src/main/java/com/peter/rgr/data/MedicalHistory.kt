data class MedicalHistory(
    val diabetes: Boolean = false,
    val hypertension: Boolean = false,
    val cardiovascularDisease: Boolean = false,
    val headInjury: Boolean = false,
    val familyHistoryAlzheimers: Boolean = false,
    val systolicBP: Int = 0,
    val diastolicBP: Int = 0,
    val alcoholConsumption: Int = 0,
    val physicalActivity: Int = 0,
    val dietQuality: String = "",
    val sleepQuality: String = "",
    val smoking: String = ""
) 
